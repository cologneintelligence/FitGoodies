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

package de.cologneintelligence.fitgoodies.runners;

import de.cologneintelligence.fitgoodies.Counts;
import de.cologneintelligence.fitgoodies.htmlparser.FitRow;
import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public final class FitParseResultTest extends FitGoodiesTestCase {
	private FitParseResult result1;
	private FitParseResult result2;

	private String tr(final String name, final String color,
	                  final int r, final int w, final int i, final int e) {
		return String.format(tr("<a href=\"%s\">%s</a></td><td bgcolor=\"#%s\">%d right, %d wrong, %d ignored, %d exceptions"),
				name, name, color, r, w, i, e);
	}

	@Before
	public void setUp() throws Exception {
		result1 = new FitParseResult();
		result2 = new FitParseResult();

		result1.put(new File("file1.html"), mkCounts(2, 0, 2, 0));
		result1.put(new File("file2.html"), mkCounts(1, 1, 0, 1));
		result1.put(new File("/x/y/file3.html"), mkCounts(5, 0, 0, 0));
		result1.put(new File("/x/y/file4.html"), mkCounts(6, 7, 8, 9));
		result1.put(new File("/x/y/file4.html"), mkCounts(5, 0, 0, 0));

		result2.put(new File("/tests/suite1/setup.html"), mkCounts(0, 0, 0, 0));
		result2.put(new File("/tests/suite1/tests1.html"), mkCounts(10, 0, 0, 0));
		result2.put(new File("/tests/suite1/tests2.html"), mkCounts(7, 0, 2, 0));
		result2.put(new File("/tests/suite2/tests.html"), mkCounts(23, 0, 1, 0));
	}

	@Test
	public void testEmptyResult() {
        useTable(tr("x"), tr("y"), tr("z"));

        FitRow line = lastFitTable.rows().get(2);
		new FitParseResult().insertAndReplace(line);

		assertThat(htmlAt(0, 0), is(equalTo("x")));
		assertThat(htmlAt(1, 0), is(equalTo("y")));
		assertThat(htmlAt(2, 0), is(equalTo("z")));
	}

	@Test
	public void testGetCounts() {
		Counts c = result1.getCounts();
		assertThat(c.right, is(equalTo((Object) 13)));
		assertThat(c.wrong, is(equalTo((Object) 1)));
		assertThat(c.ignores, is(equalTo((Object) 2)));
		assertThat(c.exceptions, is(equalTo((Object) 1)));

		c = result2.getCounts();
		assertThat(c.right, is(equalTo((Object) 40)));
		assertThat(c.wrong, is(equalTo((Object) 0)));
		assertThat(c.ignores, is(equalTo((Object) 3)));
		assertThat(c.exceptions, is(equalTo((Object) 0)));
	}

	@Test
	public void testReplaceLine1() {
        useTable(tr("x"), tr("y"), tr("z"));

        result1.insertAndReplace(lastFitTable.rows().get(2));

        assertThat(htmlAt(0, 0), is(equalTo("x")));
        assertThat(htmlAt(1, 0), is(equalTo("y")));
        assertThat(htmlAt(2, 0), is(equalTo("<a href=\"file1.html\">file1.html</a>")));
        assertThat(htmlAt(2, 1), is(equalTo("2 right, 0 wrong, 2 ignored, 0 exceptions")));
        assertThat(htmlAt(3, 0), is(equalTo("<a href=\"file2.html\">file2.html</a>")));
        assertThat(htmlAt(3, 1), is(equalTo("1 right, 1 wrong, 0 ignored, 1 exceptions")));
    }

    @Test
    public void testReplaceLine2() {
		useTable(tr("a"), tr("ignore me, replace me, forget me"), tr("c"));

		result2.insertAndReplace(lastFitTable.rows().get(1));

		assertThat(htmlAt(0, 0), is(equalTo("a")));
		assertThat(htmlAt(3, 0), is(equalTo("<a href=\"/tests/suite1/tests2.html\">/tests/suite1/tests2.html</a>")));
		assertThat(htmlAt(3, 1), is(equalTo("7 right, 0 wrong, 2 ignored, 0 exceptions")));
		assertThat(htmlAt(5, 0), is(equalTo("c")));
	}

	@Test
	public void testPrint() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		result1.print(new File("dir"), new PrintStream(bos));
		bos.close();

		assertThat(bos.toString(), is(equalTo("<table><tr><th colspan=\"2\">dir</th></tr>"
				+ tr("file1.html", "cfffcf", 2, 0, 2, 0)
				+ tr("file2.html", "ffcfcf", 1, 1, 0, 1)
				+ tr("/x/y/file3.html", "cfffcf", 5, 0, 0, 0)
				+ tr("/x/y/file4.html", "cfffcf", 5, 0, 0, 0)
				+ "</table>")));
	}
}
