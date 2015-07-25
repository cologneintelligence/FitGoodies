// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.typehandler.TypeHandlerFactory;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FitUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;

abstract public class RowFixture extends Fixture {

	protected Object results[];

	// FIXME: should be protected
	public List<Object> missing = new LinkedList<>();
	public List<Object> surplus = new LinkedList<>();

	private String[] columnParameters;
	private String[] columnNames;

	protected void doRows(Parse rows) {
		try {
			columnParameters = extractColumnParameters(rows);
			results = query();
			columnNames = findColumnNames(rows.parts);
			match(list(rows.more), list(results), 0);
			Parse last = rows.last();
			last.more = buildRows(surplus.toArray());
			mark(last.more, "surplus");
			mark(missing.iterator(), "missing");
		} catch (Exception e) {
			exception(rows.leaf(), e);
		}
	}

	protected String[] findColumnNames(Parse heads) {
		String[] names = new String[heads.size()];
		for (int i = 0; heads != null; i++, heads = heads.more) {
			names[i] = heads.text();
		}
		return names;
	}

	abstract protected Object[] query() throws Exception;  // get rows to be compared

	abstract protected Class getTargetClass();             // get expected type of row

	private void match(List<Parse> expected, List<Object> computed, int col) {
		if (col < columnNames.length) {
			for (Parse row : expected) {
				Parse cell = row.parts.at(col);
				try {
					// FIXME: throws an exception on an empty computed list
					ValueReceiver valueReceiver = createReceiver(computed.get(0), columnNames[col]);
					processCell(cell, valueReceiver);
				} catch (NoSuchMethodException | NoSuchFieldException e) {
					exception(cell, e);
				}
			}
		}

		if (col >= columnNames.length) {
			check(expected, computed);
		} else if (columnNames[col] == null) {
			match(expected, computed, col + 1);
		} else {
			Map<Object, List<Parse>> eMap = eSort(expected, col);
			Map<Object, List<Object>> cMap = cSort(computed, col);
			Set keys = union(eMap.keySet(), cMap.keySet());
			for (Object key : keys) {
				List<Parse> eList = eMap.get(key);
				List<Object> cList = cMap.get(key);
				if (eList == null) {
					surplus.addAll(cList);
				} else if (cList == null) {
					missing.addAll(eList);
				} else if (eList.size() == 1 && cList.size() == 1) {
					check(eList, cList);
				} else {
					match(eList, cList, col + 1);
				}
			}
		}
	}

	private List<Parse> list(Parse rows) {
		List<Parse> result = new LinkedList<>();
		while (rows != null) {
			result.add(rows);
			rows = rows.more;
		}
		return result;
	}

	private List<Object> list(Object[] rows) {
		List<Object> result = new LinkedList<>();
		Collections.addAll(result, rows);
		return result;
	}

	private Map<Object, List<Parse>> eSort(List<Parse> list, int col) {
		setCurrentCellParameter(columnParameters[col]);

		Map<Object, List<Parse>> result = new HashMap<>(list.size());
		for (Parse row : list) {
			Parse cell = row.parts.at(col);
			try {
				Class<?> columnType = getColumnType(columnNames[col]);
				Object key = DependencyManager.getOrCreate(TypeHandlerFactory.class)
						.getHandler(columnType, columnParameters[col]).parse(cell.text());
				bin(result, key, row);
			} catch (Exception e) {
				exception(cell, e);
				for (Parse rest = cell.more; rest != null; rest = rest.more) {
					ignore(rest);
				}
			}
		}
		return result;
	}

	private Map<Object, List<Object>> cSort(List<Object> list, int col) {
		setCurrentCellParameter(columnParameters[col]);

		Map<Object, List<Object>> result = new HashMap<>(list.size());
		for (Object row : list) {
			try {
				Object key = createReceiver(row, columnNames[col]).get();
				bin(result, key, row);
			} catch (Exception e) {
				// surplus anything with bad keys, including null
				surplus.add(row);
			}
		}
		return result;
	}

	private <T> void bin(Map<Object, List<T>> map, Object key, T row) {
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
		Set<T> result = new HashSet<>();
		result.addAll(a);
		result.addAll(b);
		return result;
	}

	protected void check(List<Parse> eList, List<Object> cList) {
		if (eList.size() == 0) {
			surplus.addAll(cList);
			return;
		}

		if (cList.size() == 0) {
			missing.addAll(eList);
			return;
		}

		Parse row = eList.remove(0);
		Parse cell = row.parts;
		Object obj = cList.remove(0);
		for (int i = 0; i < columnNames.length && cell != null; i++) {
			try {
				ValueReceiver a = createReceiver(obj, columnNames[i]);
				check(cell, a);
				cell = cell.more;
			} catch (NoSuchMethodException | NoSuchFieldException e) {
				exception(cell, e);
			}
		}
		check(eList, cList);
	}

	private void mark(Parse rows, String message) {
		String annotation = FitUtils.label(message);
		while (rows != null) {
			wrong(rows.parts);
			rows.parts.addToBody(annotation);
			rows = rows.more;
		}
	}

	private void mark(Iterator rows, String message) {
		String annotation = FitUtils.label(message);
		while (rows.hasNext()) {
			Parse row = (Parse) rows.next();
			wrong(row.parts);
			row.parts.addToBody(annotation);
		}
	}

	private Parse buildRows(Object[] rows) {
		Parse root = new Parse(null, null, null, null);
		Parse next = root;
		for (Object row : rows) {
			next = next.more = new Parse("tr", null, buildCells(row), null);
		}
		return root.more;
	}

	private Parse buildCells(Object row) {
		if (row == null) {
			Parse nil = new Parse("td", "null", null, null);
			nil.addToTag(" colspan=" + columnNames.length);
			return nil;
		}
		Parse root = new Parse(null, null, null, null);
		Parse next = root;
		for (String name : columnNames) {
			next = next.more = new Parse("td", "&nbsp;", null, null);
			if (name == null) {
				ignore(next);
			} else {
				try {
					ValueReceiver receiver = createReceiver(row, name);
					info(next, createTypeHandler(receiver).toString(receiver.get()));
				} catch (Exception e) {
					exception(next, e);
				}
			}
		}
		return root.more;
	}

	private Class<?> getColumnType(String name) throws NoSuchMethodException, NoSuchFieldException {
		Matcher matcher = METHOD_PATTERN.matcher(name);

		if (matcher.find()) {
			final String methodName = FitUtils.camel(matcher.group(1));
			return getTargetClass().getMethod(methodName).getReturnType();
		} else {
			final Field field = getTargetClass().getField(FitUtils.camel(name));
			return field.getType();
		}
	}
}
