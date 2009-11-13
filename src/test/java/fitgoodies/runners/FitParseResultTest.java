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


package fitgoodies.runners;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;

import fit.Counts;
import fit.Parse;
import fitgoodies.FitGoodiesTestCase;

/**
 * $Id$
 * @author jwierum
 */
public final class FitParseResultTest extends FitGoodiesTestCase {
	private FitParseResult result1;
	private FitParseResult result2;

	private String tr(final String name, final String color,
			final int r, final int w, final int i, final int e) {
		StringBuilder builder = new StringBuilder();
		builder.append("<tr><td>");
		builder.append("<a href=\"");
		builder.append(name);
		builder.append("\">");
		builder.append(name);
		builder.append("</a></td><td bgcolor=\"#");
		builder.append(color);
		builder.append("\">");
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

	@Override
	public void setUp() throws Exception {
		super.setUp();

		result1 = new FitParseResult();
		result2 = new FitParseResult();

		result1.put("file1.html", mkCounts(2, 0, 2, 0));
		result1.put("file2.html", mkCounts(1, 1, 0, 1));
		result1.put("/x/y/file3.html", mkCounts(5, 0, 0, 0));
		result1.put("/x/y/file4.html", mkCounts(6, 7, 8, 9));
		result1.put("/x/y/file4.html", mkCounts(5, 0, 0, 0));

		result2.put("/tests/suite1/setup.html", mkCounts(0, 0, 0, 0));
		result2.put("/tests/suite1/tests1.html", mkCounts(10, 0, 0, 0));
		result2.put("/tests/suite1/tests2.html", mkCounts(7, 0, 2, 0));
		result2.put("/tests/suite2/tests.html", mkCounts(23, 0, 1, 0));
	}

	public void testGetCounts() {
		Counts c = result1.getCounts();
		assertEquals(13, c.right);
		assertEquals(1, c.wrong);
		assertEquals(2, c.ignores);
		assertEquals(1, c.exceptions);

		c = result2.getCounts();
		assertEquals(40, c.right);
		assertEquals(0, c.wrong);
		assertEquals(3, c.ignores);
		assertEquals(0, c.exceptions);
	}

	public void testReplaceLine() throws ParseException {
		Parse table = new Parse("<table>"
				+ "<tr><td>x</td></tr>"
				+ "<tr><td>y</td></tr>"
				+ "<tr><td>z</td></tr></table>");

		Parse line = table.parts.more;
		result1.replaceLine(line);

		assertEquals("x", table.parts.parts.text());
		assertEquals("y", table.parts.more.parts.body);
		assertEquals("<a href=\"file1.html\">file1.html</a>",
				table.parts.more.parts.more.body);
		assertEquals("<a href=\"file2.html\">file2.html</a>",
				table.parts.more.more.parts.body);
		assertEquals("2 right, 0 wrong, 2 ignored, 0 exceptions",
				table.parts.more.parts.more.more.body);
		assertEquals("z", table.parts.more.more.more.more.more.parts.text());


		table = new Parse("<table>"
				+ "<tr><td>a</td></tr>"
				+ "<tr><td>ignore me, replace me, forget me</td></tr>"
				+ "<tr><td>c</td></tr></table>");

		line = table.parts.more;
		result2.replaceLine(line);

		assertEquals("a", table.parts.parts.text());
		assertEquals("<a href=\"/tests/suite1/tests2.html\">/tests/suite1/tests2.html</a>",
				table.parts.more.more.more.parts.body);
		assertEquals("7 right, 0 wrong, 2 ignored, 0 exceptions",
				table.parts.more.more.more.parts.more.body);
		assertEquals("c", table.parts.more.more.more.more.more.parts.text());
	}

	public void testPrint() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		result1.print("dir", new PrintStream(bos));
		bos.close();

		assertEquals("<table><tr><th colspan=\"2\">dir</th></tr>"
				+ tr("file1.html", "cfffcf", 2, 0, 2, 0)
				+ tr("file2.html", "ffcfcf", 1, 1, 0, 1)
				+ tr("/x/y/file3.html", "cfffcf", 5, 0, 0, 0)
				+ tr("/x/y/file4.html", "cfffcf", 5, 0, 0, 0)
				+ "</table>",
				bos.toString());
	}
}
