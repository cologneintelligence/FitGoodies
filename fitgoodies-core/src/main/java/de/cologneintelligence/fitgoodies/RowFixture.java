/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
 * This file is part of FitGoodies.
 *
 * FitGoodies is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FitGoodies is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FitGoodies.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.htmlparser.FitRow;
import de.cologneintelligence.fitgoodies.htmlparser.FitTable;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiverFactory;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;

abstract public class RowFixture extends Fixture {

    protected List<FitRow> missing = new LinkedList<>();
    protected List<Object> surplus = new LinkedList<>();
    private String[] columnParameters;
    private String[] columnNames;
    private FitTable table;

    /**
     * get rows to be compared
     * @return Actual values to be compared with the HTML content.
     * @throws Exception when querying for object fails.
     */
    abstract protected Object[] query() throws Exception;

    /**
     * get expected type of row
     *
     * @return Class which is processed by this RowFixture.
     */
    abstract protected Class getTargetClass();

    @Override
    public void doTable(FitTable table) {
        this.table = table;
        super.doTable(table);
    }

    protected void doRows(List<FitRow> rows) throws Exception {
        FitRow header = rows.get(0);
        columnParameters = extractColumnParameters(header);
        columnNames = findColumnNames(header);

        List<FitRow> expected = new LinkedList<>(rows.subList(1, rows.size()));
        List<Object> computed = new LinkedList<>(Arrays.asList(query()));
        multiLevelMatch(expected, computed, 0);

        appendSurplusRows();
        markMissingRows();
    }

    protected String[] findColumnNames(FitRow heads) {
        String[] names = new String[heads.size()];
        int i = 0;

        for (FitCell fitCell : heads.cells()) {
            names[i++] = validator.preProcess(fitCell);
        }

        return names;
    }

    /**
     * Sorts and compare two lists.
     * It is assumed that all items in {@code expected} and {@code computed}
     * are equal on the {@code col - 1} columns.
     */
    private void multiLevelMatch(List<FitRow> expected, List<Object> computed, int col) {
        boolean cantGoDeeper = col >= columnNames.length;
        if (cantGoDeeper) {
            check(expected, computed);
        } else {
            boolean isComment = isComment(col);
            if (isComment) {
                multiLevelMatch(expected, computed, col + 1);
            } else {
                groupAndMatch(expected, computed, col);
            }
        }
    }

    private boolean isComment(int col) {
        return columnNames[col] == null || columnNames[col].isEmpty();
    }

    /**
     * Groups both lists by column {@code col}. For every resulting group check if
     * there is a 1:1 or a 1:0 match. Otherwise, group further.
     */
    private void groupAndMatch(List<FitRow> expected, List<Object> computed, int col) {
        Map<Object, List<FitRow>> expectedMap = groupExpectedByColumn(expected, col);
        Map<Object, List<Object>> computedMap = groupComputedByColumn(computed, col);
        Set keys = union(expectedMap.keySet(), computedMap.keySet());

        for (Object key : keys) {
            List<FitRow> expectedList = expectedMap.get(key);
            List<Object> computedList = computedMap.get(key);

            boolean isAmbiguous = hasMultipleEntries(expectedList) && hasMultipleEntries(computedList);

            if (isAmbiguous) {
                multiLevelMatch(expectedList, computedList, col + 1);
            } else {
                check(expectedList, computedList);
            }
        }
    }

    private boolean hasMultipleEntries(List<?> expectedList) {
        return expectedList != null && expectedList.size() > 1;
    }

    private Map<Object, List<FitRow>> groupExpectedByColumn(List<FitRow> list, int col) {
        Map<Object, List<FitRow>> result = new HashMap<>(list.size());

        for (FitRow row : list) {
            List<FitCell> cells = row.cells();

            FitCell keyCell = cells.get(col);
            try {
                Object key = parseCell(col, keyCell);
                addToMap(result, key, row);
            } catch (Exception e) {
                keyCell.exception(e);
                for (int i = col + 1; i < cells.size(); i++) {
                    cells.get(i).ignore();
                }
            }
        }
        return result;
    }

    private Object parseCell(int col, FitCell cell) throws NoSuchMethodException, NoSuchFieldException, ParseException {
        String preprocessedText = validator.preProcess(cell);
        String parameter = FitUtils.saveGet(col, columnParameters);
        Class<?> columnType = getColumnType(columnNames[col]);
        TypeHandler typeHandler = typeHandlerFactory.getHandler(columnType, parameter);
        return typeHandler.parse(preprocessedText);
    }

    private Map<Object, List<Object>> groupComputedByColumn(List<Object> list, int col) {
        Map<Object, List<Object>> result = new HashMap<>(list.size());

        for (Object row : list) {
            try {
                Object key = createReceiver(row, columnNames[col]).get();
                addToMap(result, key, row);
            } catch (Exception e) {
                // surplus anything with bad keys, including null
                surplus.add(row);
            }
        }
        return result;
    }

    private <T> void addToMap(Map<Object, List<T>> map, Object key, T row) {
        if (key.getClass().isArray()) {
            key = Arrays.asList((Object[]) key);
        }

        if (map.containsKey(key)) {
            map.get(key).add(row);
        } else {
            List<T> list = new LinkedList<>();
            list.add(row);
            map.put(key, list);
        }
    }

    private <T> Set<T> union(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.addAll(b);
        return result;
    }

    /**
     * Compares two lists.
     * <ul>
     * <li>if {@code expectedList} is empty, all {@code computedList} items are surplus</li>
     * <li>if {@code computedList} is empty, all {@code expectedList} items are missing</li>
     * <li>otherwise, match the first rows and compare the rest recursively</li>
     * </ul>
     *
     * @param computedList Objects from {@link #query()}.
     * @param expectedList Objects from HTML.
     */
    protected void check(List<FitRow> expectedList, List<Object> computedList) {
        if (expectedList == null || expectedList.size() == 0) {
            surplus.addAll(computedList);
        } else if (computedList == null || computedList.size() == 0) {
            missing.addAll(expectedList);
        } else {
            Object computedRow = computedList.remove(0);
            List<FitCell> expectedRow = expectedList.remove(0).cells();
            compareRow(computedRow, expectedRow);

            check(expectedList, computedList);
        }
    }

    /**
     * Compares two rows item by item using {@link Fixture#check(FitCell, ValueReceiver, String)}.
     * Each cell will be marked as right, wrong, ignored or exception
     */
    private void compareRow(Object computedRow, List<FitCell> expectedCells) {
        for (int i = 0; i < columnNames.length && expectedCells != null; i++) {
            try {
                ValueReceiver valueReceiver;
                if (isComment(i)) {
                    valueReceiver = null;
                } else {
                    valueReceiver = createReceiver(computedRow, columnNames[i]);
                }

                String columnParameter = FitUtils.saveGet(i, columnParameters);
                check(expectedCells.get(i), valueReceiver, columnParameter);
            } catch (NoSuchMethodException | NoSuchFieldException e) {
                expectedCells.indexOf(e);
            }
        }
    }

    private Class<?> getColumnType(String name) throws NoSuchMethodException, NoSuchFieldException {
        Matcher matcher = ValueReceiverFactory.METHOD_PATTERN.matcher(name);

        if (matcher.find()) {
            final String methodName = FitUtils.camel(matcher.group(1));
            return getTargetClass().getMethod(methodName).getReturnType();
        } else {
            final Field field = getTargetClass().getField(FitUtils.camel(name));
            return field.getType();
        }
    }

    private void appendSurplusRows() {
        for (Object row : surplus) {
            FitRow fitRow = table.appendRow();
            buildRow(fitRow, row, "surplus");
        }
    }

    private void buildRow(FitRow fitRow, Object row, String info) {
        if (row == null) {
            fitRow.insert(0).blank("null", columnNames.length);
            fitRow.wrong(info);
        } else {
            fitRow.wrong(info);
            for (int i = 0; i < columnNames.length; i++) {
                FitCell cell = fitRow.insert(i);
                buildCell(cell, row, i);
            }
        }
    }

    private void buildCell(FitCell cell, Object row, int i) {
        if (columnNames[i] == null) {
            cell.ignore();
        } else {
            try {
                ValueReceiver receiver = createReceiver(row, columnNames[i]);
                String parameter = FitUtils.saveGet(i, columnParameters);
                TypeHandler typeHandler = createTypeHandler(receiver, parameter);
                cell.setDisplayValue(typeHandler.toString(receiver.get()));
                //cell.ignore();
            } catch (Exception e) {
                cell.exception(e);
            }
        }
    }

    private void markMissingRows() {
        markRowsAs(missing.iterator(), "missing");
    }

    private void markRowsAs(Iterator<FitRow> rows, String message) {
        while (rows.hasNext()) {
            FitRow row = rows.next();
            preprocessMissingCells(row);

            row.wrong(message);
        }
    }

    private void preprocessMissingCells(FitRow row) {
        for (int i = 0; i < row.size(); i++) {
            FitCell cell = row.cells().get(i);
            Object preprocessed = validator.preProcess(cell);

            if (isComment(i)) {
                cell.setDisplayValue(Objects.toString(preprocessed));
            } else {
                String parameter = FitUtils.saveGet(i, columnParameters);
                try {
                    Class<?> columnType = getColumnType(columnNames[i]);
                    TypeHandler handler = typeHandlerFactory.getHandler(columnType, parameter);
                    cell.setDisplayValue(handler.toString(preprocessed));
                } catch (NoSuchMethodException | NoSuchFieldException e) {
                    cell.setDisplayValue(Objects.toString(preprocessed));
                }
            }
        }
    }
}
