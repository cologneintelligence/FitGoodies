package fit;

// Copyright (c) 2002-2005 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class Fixture {

	public Counts counts = new Counts();
	protected String[] args;

	// Traversal //////////////////////////


	public void doTable(Parse table) {
		doRows(table.parts.more);
	}

	public void doRows(Parse rows) {
		while (rows != null) {
			Parse more = rows.more;
			doRow(rows);
			rows = more;
		}
	}

	public void doRow(Parse row) {
		doCells(row.parts);
	}

	public void doCells(Parse cells) {
		for (int i = 0; cells != null; i++) {
			try {
				doCell(cells, i);
			} catch (Exception e) {
				exception(cells, e);
			}
			cells = cells.more;
		}
	}

	public void doCell(Parse cell, int columnNumber) {
		ignore(cell);
	}


	// Annotation ///////////////////////////////

	public static String green = "#cfffcf";
	public static String red = "#ffcfcf";
	public static String gray = "#efefef";
	public static String yellow = "#ffffcf";

	public void right(Parse cell) {
		cell.addToTag(" bgcolor=\"" + green + "\"");
		counts.right++;
	}

	public void wrong(Parse cell) {
		cell.addToTag(" bgcolor=\"" + red + "\"");
		cell.body = escape(cell.text());
		counts.wrong++;
	}

	public void wrong(Parse cell, String actual) {
		wrong(cell);
		cell.addToBody(label("expected") + "<hr>" + escape(actual) + label("actual"));
	}

	public void info(Parse cell, String message) {
		cell.addToBody(info(message));
	}

	public String info(String message) {
		return " <font color=\"#808080\">" + escape(message) + "</font>";
	}

	public void ignore(Parse cell) {
		cell.addToTag(" bgcolor=\"" + gray + "\"");
		counts.ignores++;
	}

	public void error(Parse cell, String message) {
		cell.body = escape(cell.text());
		cell.addToBody("<hr><pre>" + escape(message) + "</pre>");
		cell.addToTag(" bgcolor=\"" + yellow + "\"");
		counts.exceptions++;
	}

	public void exception(Parse cell, Throwable exception) {
		while (exception.getClass().equals(InvocationTargetException.class)) {
			exception = ((InvocationTargetException) exception).getTargetException();
		}
		final StringWriter buf = new StringWriter();
		exception.printStackTrace(new PrintWriter(buf));
		error(cell, buf.toString());
	}

	// Utility //////////////////////////////////

	public String counts() {
		return counts.toString();
	}

	public static String label(String string) {
		return " <font size=-1 color=\"#c08080\"><i>" + string + "</i></font>";
	}

	public static String escape(String string) {
		string = string.replaceAll("&", "&amp;");
		string = string.replaceAll("<", "&lt;");
		string = string.replaceAll("  ", " &nbsp;");
		string = string.replaceAll("\r\n", "<br />");
		string = string.replaceAll("\r", "<br />");
		string = string.replaceAll("\n", "<br />");
		return string;
	}

	public static String camel(String name) {
		StringBuilder b = new StringBuilder(name.length());
		StringTokenizer t = new StringTokenizer(name);
		if (!t.hasMoreTokens())
			return name;
		b.append(t.nextToken());
		while (t.hasMoreTokens()) {
			String token = t.nextToken();
			b.append(token.substring(0, 1).toUpperCase());      // replace spaces with camelCase
			b.append(token.substring(1));
		}
		return b.toString();
	}

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
	public String[] getArgs() {
		return args;
	}

}
