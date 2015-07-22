// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

package fit;

import de.cologneintelligence.fitgoodies.util.FitUtils;

import java.util.*;

abstract public class RowFixture extends ColumnFixture {

	protected Object results[];
	protected List<Object> missing = new LinkedList<>();
	protected List<Object> surplus = new LinkedList<>();


	protected void doRows(Parse rows) {
		try {
			bind(rows.parts);
			results = query();
			match(list(rows.more), list(results), 0);
			Parse last = rows.last();
			last.more = buildRows(surplus.toArray());
			mark(last.more, "surplus");
			mark(missing.iterator(), "missing");
		} catch (Exception e) {
			exception(rows.leaf(), e);
		}
	}

	abstract protected Object[] query() throws Exception;  // get rows to be compared

	abstract protected Class getTargetClass();             // get expected type of row

	protected void match(List<Parse> expected, List<Object> computed, int col) {
		if (col >= columnBindings.length) {
			check(expected, computed);
		} else if (columnBindings[col] == null) {
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

	protected List<Parse> list(Parse rows) {
		List<Parse> result = new LinkedList<>();
		while (rows != null) {
			result.add(rows);
			rows = rows.more;
		}
		return result;
	}

	protected List<Object> list(Object[] rows) {
		List<Object> result = new LinkedList<>();
		Collections.addAll(result, rows);
		return result;
	}

	protected Map<Object, List<Parse>> eSort(List<Parse> list, int col) {
		TypeAdapter a = columnBindings[col];
		Map<Object, List<Parse>> result = new HashMap<>(list.size());
		for (Parse row : list) {
			Parse cell = row.parts.at(col);
			try {
				Object key = a.parse(cell.text());
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

	protected Map<Object, List<Object>> cSort(List<Object> list, int col) {
		TypeAdapter a = columnBindings[col];
		Map<Object, List<Object>> result = new HashMap<>(list.size());
		for (Object row : list) {
			try {
				a.target = row;
				Object key = a.get();
				bin(result, key, row);
			} catch (Exception e) {
				// surplus anything with bad keys, including null
				surplus.add(row);
			}
		}
		return result;
	}

	protected <T> void bin(Map<Object, List<T>> map, Object key, T row) {
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

	protected <T> Set<T> union(Set<T> a, Set<T> b) {
		Set<T> result = new HashSet<>();
		result.addAll(a);
		result.addAll(b);
		return result;
	}

	protected void check(List eList, List cList) {
		if (eList.size() == 0) {
			surplus.addAll(cList);
			return;
		}
		if (cList.size() == 0) {
			missing.addAll(eList);
			return;
		}
		Parse row = (Parse) eList.remove(0);
		Parse cell = row.parts;
		Object obj = cList.remove(0);
		for (int i = 0; i < columnBindings.length && cell != null; i++) {
			TypeAdapter a = columnBindings[i];
			if (a != null) {
				a.target = obj;
			}
			check(cell, a);
			cell = cell.more;
		}
		check(eList, cList);
	}

	protected void mark(Parse rows, String message) {
		String annotation = FitUtils.label(message);
		while (rows != null) {
			wrong(rows.parts);
			rows.parts.addToBody(annotation);
			rows = rows.more;
		}
	}

	protected void mark(Iterator rows, String message) {
		String annotation = FitUtils.label(message);
		while (rows.hasNext()) {
			Parse row = (Parse) rows.next();
			wrong(row.parts);
			row.parts.addToBody(annotation);
		}
	}

	protected Parse buildRows(Object[] rows) {
		Parse root = new Parse(null, null, null, null);
		Parse next = root;
		for (Object row : rows) {
			next = next.more = new Parse("tr", null, buildCells(row), null);
		}
		return root.more;
	}

	protected Parse buildCells(Object row) {
		if (row == null) {
			Parse nil = new Parse("td", "null", null, null);
			nil.addToTag(" colspan=" + columnBindings.length);
			return nil;
		}
		Parse root = new Parse(null, null, null, null);
		Parse next = root;
		for (TypeAdapter columnBinding : columnBindings) {
			next = next.more = new Parse("td", "&nbsp;", null, null);
			if (columnBinding == null) {
				ignore(next);
			} else {
				try {
					columnBinding.target = row;
					info(next, columnBinding.toString(columnBinding.get()));
				} catch (Exception e) {
					exception(next, e);
				}
			}
		}
		return root.more;
	}
}
