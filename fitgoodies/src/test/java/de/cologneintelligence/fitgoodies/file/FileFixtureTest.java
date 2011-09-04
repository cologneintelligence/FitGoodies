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


package de.cologneintelligence.fitgoodies.file;

import java.text.ParseException;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.FileFixture;
import de.cologneintelligence.fitgoodies.file.FileFixtureHelper;

import fit.Parse;

/**
 * $Id$
 * @author jwierum
 */
public class FileFixtureTest extends FitGoodiesTestCase {
	public final void testErrors() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>too short</td></tr>"
				+ "<tr><td>wrong</td><td>value</td></tr></table>"
				);

		FileFixture fixture = new FileFixture();
		fixture.doTable(table);

		assertEquals(0, fixture.counts.wrong);
		assertEquals(2, fixture.counts.exceptions);
	}

	public final void testPattern() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>pattern</td><td>.*\\.txt</td></tr></table>");

		FileFixture fixture = new FileFixture();
		fixture.doTable(table);

		assertEquals(0, fixture.counts.wrong);
		assertEquals(0, fixture.counts.exceptions);
		assertEquals(".*\\.txt", FileFixtureHelper.instance().getPattern());

		table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>pattern</td><td>testfile</td></tr></table>"
				);

		fixture.doTable(table);

		assertEquals(0, fixture.counts.wrong);
		assertEquals(0, fixture.counts.exceptions);
		assertEquals(0, fixture.counts.right);
		assertEquals("testfile", FileFixtureHelper.instance().getPattern());
	}

	public final void testEncoding() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>directory</td><td>dir</td></tr>"
				+ "<tr><td>encoding</td><td>utf-8</td></tr>"
				+ "</table>");

		FileFixture fixture = new FileFixture();
		fixture.doTable(table);

		assertEquals(0, fixture.counts.right);
		assertEquals(0, fixture.counts.wrong);
		assertEquals(0, fixture.counts.exceptions);
		assertEquals("utf-8", FileFixtureHelper.instance().getEncoding());

		table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>directory</td><td>c:\\</td></tr>"
				+ "<tr><td>encoding</td><td>latin-1</td></tr>"
				+ "</table>");

		fixture.doTable(table);

		assertEquals(0, fixture.counts.right);
		assertEquals(0, fixture.counts.wrong);
		assertEquals(0, fixture.counts.exceptions);
		assertEquals("latin-1", FileFixtureHelper.instance().getEncoding());
	}
}
