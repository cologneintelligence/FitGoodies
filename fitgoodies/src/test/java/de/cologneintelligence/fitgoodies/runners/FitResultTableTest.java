/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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


package de.cologneintelligence.fitgoodies.runners;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.DirectoryHelperMock;
import de.cologneintelligence.fitgoodies.runners.FitResultTable;

import fit.Counts;

/**
 * $Id$
 * @author jwierum
 */
public final class FitResultTableTest extends FitGoodiesTestCase {
	private FitResultTable result;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		result = new FitResultTable(new DirectoryHelperMock());
	}

	private static String tr(final String file, final int indent,
			final String name, final String color,
			final int r, final int w, final int i, final int e) {
		StringBuilder builder = new StringBuilder();
		builder.append("<tr bgcolor=\"#");
		builder.append(color);
		builder.append("\"><td>");
		for (int j = 0; j < indent; ++j) {
			builder.append(" &nbsp; &nbsp; &nbsp; &nbsp;");
		}
		builder.append("<a href=\"");
		builder.append(file);
		builder.append("\">");
		builder.append(name);
		builder.append("</a></td><td>");
		builder.append(r);
		builder.append(" right, ");
		builder.append(w);
		builder.append(" wrong, ");
		builder.append(i);
		builder.append(" ignored, ");
		builder.append(e);
		builder.append(" exceptions</td></tr>");
		return builder.toString();
	}

	private static String tr(final String file, final String color,
			final int r, final int w, final int i, final int e) {
		return tr(file, 0, file, color, r, w, i, e);
	}

	private static String th(final String path, final String color,
			final int r, final int w, final int i, final int e) {
		StringBuilder builder = new StringBuilder();
		builder.append("<tr bgcolor=\"#");
		builder.append(color);
		builder.append("\">");
		builder.append("<th style=\"text-align: left\">");
		builder.append(path);
		builder.append("</th><td>");
		builder.append(r);
		builder.append(" right, ");
		builder.append(w);
		builder.append(" wrong, ");
		builder.append(i);
		builder.append(" ignored, ");
		builder.append(e);
		builder.append(" exceptions</td></tr>");
		return builder.toString();
	}

	public void testGetPut() {
		Counts file1 = mkCounts(2, 1, 3, 0);
		Counts file2 = mkCounts(3, 5, 2, 11);
		Counts file3 = mkCounts(5, 7, 3, 4);
		Counts file4 = mkCounts(4, 4, 8, 0);

		result.put("file1.html", file1);
		result.put("file2.html", file2);
		result.put("file3.html", file3);
		result.put("file1.html", file4);

		assertSame(file4, result.get("file1.html"));
		assertSame(file2, result.get("file2.html"));
		assertSame(file3, result.get("file3.html"));

		result.put("file1.html", file1);
		assertSame(file1, result.get("file1.html"));

		assertNull(result.get("file7.html"));
	}

	public void testFiles() {
		result.put("file1.html", null);
		result.put("file2.html", null);
		result.put("file3.html", null);
		result.put("file2.html", null);

		String[] files = result.getFiles();
		assertArrayElements(new String[]{"file1.html", "file2.html", "file3.html"},
				files);
	}

	public void testSummary() {
		result.put("f1", mkCounts(1, 2, 3, 4));
		result.put("f2", mkCounts(4, 4, 4, 4));

		Counts counts = result.getSummary();
		assertEquals(5, counts.right);
		assertEquals(6, counts.wrong);
		assertEquals(7, counts.ignores);
		assertEquals(8, counts.exceptions);
	}

	public void testRow() {
		result.put("file1.html", mkCounts(2, 1, 3, 0));
		result.put("file2.html", mkCounts(3, 0, 0, 11));
		result.put("file3.html", mkCounts(0, 0, 0, 0));

		assertEquals(tr("file1.html", "ffcfcf", 2, 1, 3, 0), result.getRow("file1.html"));
		assertEquals(tr("file2.html", "ffcfcf", 3, 0, 0, 11), result.getRow("file2.html"));
		assertEquals(tr("file3.html", "cfffcf", 0, 0, 0, 0), result.getRow("file3.html"));

		result.put("file3.html", mkCounts(7, 0, 0, 0));
		assertEquals(tr("file3.html", "cfffcf", 7, 0, 0, 0), result.getRow("file3.html"));
	}

	public void testSummaryRow() {
		assertEquals("<tr bgcolor=\"#cfffcf\"><th style=\"text-align: left\">x"
				+ "</th><th style=\"text-align: left\">"
				+ "0 right, 0 wrong, 0 ignored, "
				+ "0 exceptions</th></tr>", result.getSummaryRow("x"));

		result.put("file1.html", mkCounts(2, 1, 3, 0));
		result.put("file2.html", mkCounts(3, 0, 0, 11));

		assertEquals("<tr bgcolor=\"#ffcfcf\"><th style=\"text-align: left\">y"
				+ "</th><th style=\"text-align: left\">"
				+ "5 right, 1 wrong, 3 ignored, "
				+ "11 exceptions</th></tr>", result.getSummaryRow("y"));

		result.put("file1.html", mkCounts(2, 0, 0, 0));
		result.put("file2.html", mkCounts(3, 0, 1, 0));
		result.put("file3.html", mkCounts(5, 0, 0, 0));

		assertEquals("<tr bgcolor=\"#cfffcf\"><th style=\"text-align: left\">z"
				+ "</th><th style=\"text-align: left\">"
				+ "10 right, 0 wrong, 1 ignored, "
				+ "0 exceptions</th></tr>", result.getSummaryRow("z"));
	}

	public void testPrint() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		result.put("file1.html", mkCounts(2, 1, 3, 0));
		result.put("file2.html", mkCounts(3, 0, 0, 11));
		result.put("file3.html", mkCounts(0, 0, 0, 0));

		result.print("mydir", bos);
		bos.close();

		assertEquals("<table>"
				+ "<tr bgcolor=\"#ffcfcf\">"
					+ "<th style=\"text-align: left\">mydir</th>"
					+ "<th style=\"text-align: left\">5 right, 1 wrong, "
						+ "3 ignored, 11 exceptions</th>"
				+ "</tr>"
				+ "<tr><td colspan=\"2\"></td></tr>"
				+ tr("file1.html", "ffcfcf", 2, 1, 3, 0)
				+ tr("file2.html", "ffcfcf", 3, 0, 0, 11)
				+ tr("file3.html", "cfffcf", 0, 0, 0, 0)
				+ "</table>",
				bos.toString());
	}

	public void testPringWithSubDirs() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		result.put("file1.html", mkCounts(3, 2, 1, 2));
		result.put("file2.html", mkCounts(20, 2, 0, 0));
		result.put("g/a/file2.html", mkCounts(25, 0, 0, 0));
		result.put("g/file1.html", mkCounts(1, 1, 0, 0));
		result.put("g/file2.html", mkCounts(0, 0, 0, 0));
		result.put("m/file2.html", mkCounts(1, 1, 1, 1));
		result.put("x.html", mkCounts(2, 0, 0, 1));

		result.print("tests", bos);
		bos.close();

		assertEquals("<table>"
				+ "<tr bgcolor=\"#ffcfcf\">"
					+ "<th style=\"text-align: left\">tests</th>"
					+ "<th style=\"text-align: left\">52 right, 6 wrong,"
						+ " 2 ignored, 4 exceptions</th>"
				+ "</tr>"
				+ "<tr><td colspan=\"2\"></td></tr>"
				+ tr("file1.html", "ffcfcf", 3, 2, 1, 2)
				+ tr("file2.html", "ffcfcf", 20, 2, 0, 0)
				+ th("g/", "ffcfcf", 26, 1, 0, 0)
				+ th("g/a/", "cfffcf", 25, 0, 0, 0)
				+ tr("g/a/file2.html", 2, "file2.html", "cfffcf", 25, 0, 0, 0)
				+ tr("g/file1.html", 1, "file1.html", "ffcfcf", 1, 1, 0, 0)
				+ tr("g/file2.html", 1, "file2.html", "cfffcf", 0, 0, 0, 0)
				+ th("m/", "ffcfcf", 1, 1, 1, 1)
				+ tr("m/file2.html", 1, "file2.html", "ffcfcf", 1, 1, 1, 1)
				+ tr("x.html", "ffcfcf", 2, 0, 0, 1)
				+ "</table>",
				bos.toString());
	}

	public void testNull() {
		result.put("file3.html", null);

		assertEquals("<tr bgcolor=\"#efefef\"><td><a href=\"file3.html\">"
				+ "file3.html</a></td><td>(none)</td></tr>",
				result.getRow("file3.html"));
		assertEquals("<tr bgcolor=\"#cfffcf\"><th style=\"text-align: left\">n</th>"
				+ "<th style=\"text-align: left\">"
				+ "0 right, 0 wrong, 0 ignored, "
				+ "0 exceptions</th></tr>", result.getSummaryRow("n"));
	}

	public void testIndent() {
		result.put("file1.html", null);
		result.put("file2.html", null);
		result.put("g/file1.html", null);
		result.put("g/file2.html", null);
		result.put("g/g/file2.html", null);
		result.put("m/file2.html", null);
		result.put("x.html", null);

		assertEquals("<tr bgcolor=\"#efefef\"><td><a href=\"file1.html\">"
				+ "file1.html</a></td><td>(none)</td></tr>",
				result.getRow("file1.html"));
		assertEquals("<tr bgcolor=\"#efefef\"><td><a href=\"file2.html\">"
				+ "file2.html</a></td><td>(none)</td></tr>",
				result.getRow("file2.html"));
		assertEquals("<tr bgcolor=\"#efefef\"><td> &nbsp; &nbsp; &nbsp; &nbsp;"
				+ "<a href=\"g/file1.html\">file1.html</a></td><td>(none)</td></tr>",
				result.getRow("g/file1.html"));
		assertEquals("<tr bgcolor=\"#efefef\"><td> &nbsp; &nbsp; &nbsp; &nbsp;"
				+ "<a href=\"g/file2.html\">file2.html</a></td><td>(none)</td></tr>",
				result.getRow("g/file2.html"));
		assertEquals("<tr bgcolor=\"#efefef\"><td> &nbsp; &nbsp; &nbsp; &nbsp;"
				+ " &nbsp; &nbsp; &nbsp; &nbsp;<a href=\"g/g/file2.html\">"
				+ "file2.html</a></td><td>(none)</td></tr>",
				result.getRow("g/g/file2.html"));
		assertEquals("<tr bgcolor=\"#efefef\"><td> &nbsp; &nbsp; &nbsp; &nbsp;"
				+ "<a href=\"m/file2.html\">file2.html</a></td><td>(none)</td></tr>",
				result.getRow("m/file2.html"));
		assertEquals("<tr bgcolor=\"#efefef\"><td><a href=\"x.html\">"
				+ "x.html</a></td><td>(none)</td></tr>",
				result.getRow("x.html"));
	}

	public void testSubSummary() {
		result.put("file1.html", mkCounts(3, 2, 1, 2));
		result.put("file2.html", mkCounts(20, 2, 0, 0));
		result.put("g/a/file2.html", mkCounts(25, 0, 0, 0));
		result.put("g/file1.html", mkCounts(1, 1, 0, 0));
		result.put("g/file2.html", mkCounts(0, 0, 0, 0));
		result.put("m/file2.html", mkCounts(1, 1, 1, 1));
		result.put("x.html", mkCounts(2, 0, 0, 1));

		assertEquals(th("g/", "ffcfcf", 26, 1, 0, 0), result.getSubSummaryRow("g"));
		assertEquals(th("g/", "ffcfcf", 26, 1, 0, 0), result.getSubSummaryRow("g/"));
		assertEquals(th("g/a/", "cfffcf", 25, 0, 0, 0), result.getSubSummaryRow("g/a"));
	}

	public void testEmptyTable() throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		result.print("test", stream);
		assertEquals("<table><tr bgcolor=\"#cfffcf\"><th style=\"text-align: left\">test</th>"
				+ "<th style=\"text-align: left\">0 right, 0 wrong, 0 ignored, 0 exceptions</th>"
				+ "</tr><tr><td colspan=\"2\"></td></tr>"
				+ "<tr><td colspan=\"2\">no files found</td></tr>"
				+ "</table>", stream.toString());
	}

	public void testTitles() throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		result.put("1000_w/it_01/a/f.html",
				mkCounts(1, 0, 0 , 0));
		result.put("1000_w/it_01/a/g.html",
				mkCounts(1, 0, 0 , 0));
		result.put("1000_w/it_01/a/hij/m.html",
				mkCounts(1, 0, 0 , 0));
		result.put("1001_x/20_a.html",
				mkCounts(1, 0, 0 , 0));
		result.put("1001_x/260-b.html",
				mkCounts(1, 0, 0 , 0));

		result.print("test", stream);
		assertEquals("<table><tr bgcolor=\"#cfffcf\"><th style=\"text-align: left\">test</th>"
				+ "<th style=\"text-align: left\">5 right, 0 wrong, 0 ignored, 0 exceptions</th>"
				+ "</tr><tr><td colspan=\"2\"></td></tr>"

				+ th("1000_w/", "cfffcf", 3, 0, 0, 0)
				+ th("1000_w/it_01/", "cfffcf", 3, 0, 0, 0)
				+ th("1000_w/it_01/a/", "cfffcf", 3, 0, 0, 0)
				+ tr("1000_w/it_01/a/f.html", 3, "f.html", "cfffcf", 1, 0, 0, 0)
				+ tr("1000_w/it_01/a/g.html", 3, "g.html", "cfffcf", 1, 0, 0, 0)
				+ th("1000_w/it_01/a/hij/", "cfffcf", 1, 0, 0, 0)
				+ tr("1000_w/it_01/a/hij/m.html", 4, "m.html", "cfffcf", 1, 0, 0, 0)
				+ th("1001_x/", "cfffcf", 2, 0, 0, 0)
				+ tr("1001_x/20_a.html", 1, "20_a.html", "cfffcf", 1, 0, 0, 0)
				+ tr("1001_x/260-b.html", 1, "260-b.html", "cfffcf", 1, 0, 0, 0)
				+ "</table>", stream.toString());
	}
}
