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

import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiverFactory;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;

abstract public class RowFixture extends Fixture {

	protected List<Object> missing = new LinkedList<>();
	protected List<Object> surplus = new LinkedList<>();
	private String[] columnParameters;
	private String[] columnNames;

	/**
	 * get rows to be compared
	 */
	abstract protected Object[] query() throws Exception;

	/**
	 * get expected type of row
	 */
	abstract protected Class getTargetClass();

	protected void doRows(Parse rows) {
		try {
			columnParameters = extractColumnParameters(rows);
			columnNames = findColumnNames(rows.parts);

			List<Parse> expected = rowsToRowList(rows.more);
			List<Object> computed = new LinkedList<>(Arrays.asList(query()));
			multiLevelMatch(expected, computed, 0);

			appendSurplusRows(rows);
			markMissingRows();
		} catch (Exception e) {
			exception(rows.leaf(), e);
		}
	}

	protected String[] findColumnNames(Parse heads) {
		String[] names = new String[heads.size()];
		for (int i = 0; heads != null; i++, heads = heads.more) {
			names[i] = validator.preProcess(heads);
		}
		return names;
	}

	private List<Parse> rowsToRowList(Parse rows) {
		List<Parse> result = new LinkedList<>();
		while (rows != null) {
			result.add(rows);
			rows = rows.more;
		}
		return result;
	}

	/**
	 * Sorts and compare two lists.
	 * It is assumed that all items in {@code expected} and {@code computed}
	 * are equal on the {@code col - 1} columns.
	 */
	private void multiLevelMatch(List<Parse> expected, List<Object> computed, int col) {
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
	private void groupAndMatch(List<Parse> expected, List<Object> computed, int col) {
		Map<Object, List<Parse>> expectedMap = groupExpectedByColumn(expected, col);
		Map<Object, List<Object>> computedMap = groupComputedByColumn(computed, col);
		Set keys = union(expectedMap.keySet(), computedMap.keySet());

		for (Object key : keys) {
			List<Parse> expectedList = expectedMap.get(key);
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

	private Map<Object, List<Parse>> groupExpectedByColumn(List<Parse> list, int col) {
		Map<Object, List<Parse>> result = new HashMap<>(list.size());

		for (Parse row : list) {
			Parse keyCell = row.parts.at(col);
			try {
				Object key = parseCell(col, keyCell);
				addToMap(result, key, row);
			} catch (Exception e) {
				exception(keyCell, e);
				for (Parse rest = keyCell.more; rest != null; rest = rest.more) {
					ignore(rest);
				}
			}
		}
		return result;
	}

	private Object parseCell(int col, Parse cell) throws NoSuchMethodException, NoSuchFieldException, ParseException {
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
	 */
	protected void check(List<Parse> expectedList, List<Object> computedList) {
		if (expectedList == null || expectedList.size() == 0) {
			surplus.addAll(computedList);
		} else if (computedList == null || computedList.size() == 0) {
			missing.addAll(expectedList);
		} else {
			Object computedRow = computedList.remove(0);
			Parse expectedRow = expectedList.remove(0).parts;
			compareRow(computedRow, expectedRow);

			check(expectedList, computedList);
		}
	}

	/**
	 * Compares two rows item by item using {@link Fixture#check(Parse, ValueReceiver, String)}.
	 * Each cell will be marked as right, wrong, ignored or exception
	 */
	private void compareRow(Object computedRow, Parse expectedRow) {
		for (int i = 0; i < columnNames.length && expectedRow != null; i++) {
			try {
				ValueReceiver valueReceiver;
				if (isComment(i)) {
					valueReceiver = null;
				} else {
					valueReceiver = createReceiver(computedRow, columnNames[i]);
				}

				String columnParameter = FitUtils.saveGet(i, columnParameters);
				check(expectedRow, valueReceiver, columnParameter);

				expectedRow = expectedRow.more;
			} catch (NoSuchMethodException | NoSuchFieldException e) {
				exception(expectedRow, e);
			}
		}
	}

	private void markWrongWithAnnotation(Parse cell, String annotation) {
		wrong(cell);
		cell.addToBody(annotation);
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

	private void appendSurplusRows(Parse rows) {
		Parse lastRow = rows.last();
		lastRow.more = buildRows(surplus.toArray());
		markRowsAs(lastRow.more, "surplus");
	}

	private Parse buildRows(Object[] rows) {
		Parse root = new Parse(null, null, null, null);
		Parse next = root;

		for (Object row : rows) {
			next.more = new Parse("tr", null, buildRow(row), null);
			next = next.more;
		}

		return root.more;
	}

	private Parse buildRow(Object row) {
		if (row == null) {
			Parse nil = new Parse("td", "null", null, null);
			nil.addToTag(" colspan=" + columnNames.length);
			return nil;
		} else {
			Parse root = new Parse(null, null, null, null);
			Parse next = root;

			for (int i = 0; i < columnNames.length; i++) {
				next.more = buildCell(row, i);
				next = next.more;
			}

			return root.more;
		}
	}

	private Parse buildCell(Object row, int i) {
		Parse td = new Parse("td", "&nbsp;", null, null);

		if (columnNames[i] == null) {
			ignore(td);
		} else {
			try {
				ValueReceiver receiver = createReceiver(row, columnNames[i]);
				String parameter = FitUtils.saveGet(i, columnParameters);
				TypeHandler typeHandler = createTypeHandler(receiver, parameter);
				info(td, typeHandler.toString(receiver.get()));
			} catch (Exception e) {
				exception(td, e);
			}
		}

		return td;
	}

	private void markRowsAs(Iterator rows, String message) {
		String annotation = FitUtils.label(message);
		while (rows.hasNext()) {
			Parse row = (Parse) rows.next();
			preprocessMissingCells(row);

			markWrongWithAnnotation(row.parts, annotation);
		}
	}

	private void markRowsAs(Parse rows, String message) {
		String annotation = FitUtils.label(message);
		while (rows != null) {
			markWrongWithAnnotation(rows.parts, annotation);
			rows = rows.more;
		}
	}

	private void preprocessMissingCells(Parse rows) {
		int i;
		Parse cell = rows.parts;

		for (i = 0; cell != null; ++i, cell = cell.more) {
			Object preprocessed = validator.preProcess(cell);

			if (isComment(i)) {
				cell.body = Objects.toString(preprocessed);
			} else {
				String parameter = FitUtils.saveGet(i, columnParameters);
				try {
					Class<?> columnType = getColumnType(columnNames[i]);
					TypeHandler handler = typeHandlerFactory.getHandler(columnType, parameter);
					cell.body = handler.toString(preprocessed);
				} catch (NoSuchMethodException | NoSuchFieldException e) {
					cell.body = Objects.toString(preprocessed);
				}
			}
		}
	}

	private void markMissingRows() {
		markRowsAs(missing.iterator(), "missing");
	}
}
