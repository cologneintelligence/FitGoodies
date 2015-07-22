package fit;

// Copyright (c) 2002-2005 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

import de.cologneintelligence.fitgoodies.ScientificDouble;
import de.cologneintelligence.fitgoodies.util.FitUtils;

import java.text.DateFormat;
import java.util.Date;

public class Fixture {

	private Counts counts = new Counts();

	protected String[] args;

	// Traversal //////////////////////////

	public void doTable(Parse table) {
		doRows(table.parts.more);
	}

	protected void doRows(Parse rows) {
		while (rows != null) {
			Parse more = rows.more;
			doRow(rows);
			rows = more;
		}
	}

	protected void doRow(Parse row) {
		doCells(row.parts);
	}

	protected void doCells(Parse cells) {
		for (int i = 0; cells != null; i++) {
			try {
				doCell(cells, i);
			} catch (Exception e) {
				exception(cells, e);
			}
			cells = cells.more;
		}
	}

	protected void doCell(Parse cell, int columnNumber) {
		ignore(cell);
	}


	// Annotation ///////////////////////////////

	public void right(Parse cell) {
		FitUtils.right(cell);
		counts.right++;
	}

	public void wrong(Parse cell) {
		FitUtils.wrong(cell);
		counts.wrong++;
	}

	protected void wrong(Parse cell, String actual) {
		wrong(cell);
		FitUtils.wrong(cell, actual);
	}

	public void info(Parse cell, String message) {
		FitUtils.info(cell, message);
	}

	protected void ignore(Parse cell) {
		FitUtils.ignore(cell);
		counts.ignores++;
	}

	protected void error(Parse cell, String message) {
		FitUtils.error(cell, message);
		counts.exceptions++;
	}

	protected void exception(Parse cell, Throwable exception) {
		FitUtils.exception(cell, exception);
		counts.exceptions++;
	}

	// Utility //////////////////////////////////

	public Object parse(String s, Class type) throws Exception {
		if (type.equals(String.class)) {
			return s;
		}
		if (type.equals(Date.class)) {
			return DateFormat.getDateInstance().parse(s);
		}
		if (type.equals(ScientificDouble.class)) {
			return ScientificDouble.valueOf(s);
		}
		throw new Exception("can't yet parse " + type);
	}

	public void check(Parse cell, TypeAdapter a) {
		String text = cell.text();
		if (text.equals("")) {
			try {
				info(cell, a.toString(a.get()));
			} catch (Exception e) {
				info(cell, "error");
			}
		} else if (a == null) {
			ignore(cell);
		} else if (text.equals("error")) {
			try {
				Object result = a.invoke();
				wrong(cell, a.toString(result));
			} catch (IllegalAccessException e) {
				exception(cell, e);
			} catch (Exception e) {
				right(cell);
			}
		} else {
			try {
				Object result = a.get();
				if (a.equals(a.parse(text), result)) {
					right(cell);
				} else {
					wrong(cell, a.toString(result));
				}
			} catch (Exception e) {
				exception(cell, e);
			}
		}
	}

	/* Added by Rick, from FitNesse */
	protected String[] getArgs() {
		return args;
	}

	public Counts counts() {
		return counts;
	}

}
