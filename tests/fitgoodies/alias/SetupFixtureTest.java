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


package fitgoodies.alias;

import java.text.ParseException;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;

/**
 * $Id: SetupFixtureTest.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public class SetupFixtureTest extends FitGoodiesTestCase {
	public final void testParsing() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>asdf</td><td>java.lang.String</td></tr>"
				+ "</table>");

		SetupFixture fixture = new SetupFixture();
		fixture.doTable(table);

		assertEquals(0, fixture.counts.exceptions);
		assertEquals("java.lang.String", AliasHelper.instance().getClazz("asdf"));

		table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>i</td><td>java.lang.Integer</td></tr>"
				+ "</table>");

		fixture.doTable(table);
		assertEquals("java.lang.Integer", AliasHelper.instance().getClazz("i"));
	}

	public final void testError() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>x</td></tr>"
				+ "</table>");

		SetupFixture fixture = new SetupFixture();
		fixture.doTable(table);

		assertEquals(0, fixture.counts.exceptions);
		assertEquals(1, fixture.counts.ignores);
	}
}
