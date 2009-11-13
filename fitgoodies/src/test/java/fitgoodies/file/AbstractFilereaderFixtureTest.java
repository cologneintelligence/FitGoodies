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


package fitgoodies.file;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;

/**
 * $Id$
 * @author jwierum
 */
public class AbstractFilereaderFixtureTest extends FitGoodiesTestCase {
	public static class TestFixture extends AbstractFileReaderFixture {
		public String x;
	}

	public final void testDefaultParameters() throws Exception {
		FileFixtureHelper.instance().setEncoding("latin-1");
		FileFixtureHelper.instance().setProvider(new DirectoryProviderMock());
		FileFixtureHelper.instance().setPattern(".*\\.bat");

		TestFixture fixture = new TestFixture();
		Parse table = new Parse("<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>x</td></tr>"
				+ "</table>");

		fixture.doTable(table);

		assertEquals("/f.txt.bat", fixture.getFile().fullname());
		assertEquals("latin-1", fixture.getEncoding());

		FileFixtureHelper.instance().setEncoding("utf-16");
		FileFixtureHelper.instance().setProvider(new DirectoryProviderMock());
		FileFixtureHelper.instance().setPattern(".*");
		fixture = new TestFixture();
		fixture.doTable(table);

		assertEquals("/test/file1.txt", fixture.getFile().fullname());
		assertEquals("utf-16", fixture.getEncoding());
	}

	public final void testErrors() throws Exception {
		TestFixture fixture = new TestFixture();
		Parse table = new Parse("<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>x</td></tr>"
				+ "</table>");

		fixture.doTable(table);

		assertEquals(1, fixture.counts.exceptions);
	}

	public final  void testCustomParameters() throws Exception {
		TestFixture fixture = new TestFixture();
		Parse table = new Parse("<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>x</td></tr>"
				+ "</table>");

		FileFixtureHelper.instance().setProvider(new DirectoryProviderMock());
		fixture.setParams(new String[] {"pattern=.*\\.bat", "encoding=cp1252"});
		fixture.doTable(table);

		assertEquals(0, fixture.counts.exceptions);

		assertEquals("/f.txt.bat", fixture.getFile().fullname());
		assertEquals("cp1252", fixture.getEncoding());
	}
}
