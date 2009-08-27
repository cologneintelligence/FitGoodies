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

import java.text.ParseException;

import org.jmock.Expectations;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.file.DirectoryHelperMock;

/**
 * $Id: RunFixtureTest.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public final class RunFixtureTest extends FitGoodiesTestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	public void testFile() throws ParseException {
		Parse table = prepareFileFixtureData();

		RunFixture fixture = new RunFixture();
		fixture.doTable(table);

		assertEquals(0, fixture.counts.exceptions);
		assertEquals(6, fixture.counts.right);
		assertEquals(2, fixture.counts.wrong);
		assertEquals(10, fixture.counts.ignores);

		assertArray(new String[]{"/results/", "/tests2/"},
				((DirectoryHelperMock) RunnerHelper.instance().getHelper()).getPathes());
	}

	public void testFileTableProcessing() throws ParseException {
		Parse table = prepareFileFixtureData();

		RunFixture fixture = new RunFixture();
		fixture.doTable(table);

		assertEquals("<a href=\"file1.html\">file1.html</a>",
				table.parts.more.parts.body);
		assertEquals("1 right, 2 wrong, 3 ignored, 0 exceptions",
				table.parts.more.parts.more.body);
		assertEquals("<a href=\"../tests2/test2.html\">../tests2/test2.html</a>",
				table.parts.more.more.parts.body);
		assertEquals("5 right, 0 wrong, 7 ignored, 0 exceptions",
				table.parts.more.more.parts.more.body);

		assertTrue(table.parts.more.parts.more.tag.contains("ffcfcf"));
		assertTrue(table.parts.more.more.parts.more.tag.contains("cfffcf"));
	}

	private Parse prepareFileFixtureData() throws ParseException {
		final Runner runner = mock(Runner.class);

		DirectoryHelperMock helper = new DirectoryHelperMock();
		checking(new Expectations() {{
			oneOf(runner).run("/tests/file1.html", "/results/file1.html");
				will(returnValue(mkCounts(1, 2, 3, 0)));
			oneOf(runner).run("/tests2/test2.html", "/tests2/test2.html");
				will(returnValue(mkCounts(5, 0, 7, 0)));
		}});

		RunnerHelper.instance().setFilePath("/tests/testfile.html");
		RunnerHelper.instance().setResultFilePath("/results/resultfile.html");
		RunnerHelper.instance().setHelper(helper);
		RunnerHelper.instance().setRunner(runner);

		Parse table = new Parse("<table><tr><td>ignored</td></tr>"
				+ "<tr><td>file</td><td>file1.html</td></tr>"
				+ "<tr><td>file</td><td>../tests2/test2.html</td></tr></table>");
		return table;
	}
}
