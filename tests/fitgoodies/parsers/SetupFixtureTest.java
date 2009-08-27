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


package fitgoodies.parsers;

import fit.Fixture;
import fit.Parse;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.util.FixtureTools;

/**
 * $Id$
 * @author jwierum
 */
public class SetupFixtureTest extends FitGoodiesTestCase {
	public final void testSetup() throws Exception {
		assertNull(FixtureTools.parse("42", Long.class, null));
		Fixture fixture = new SetupFixture();
		Parse table = new Parse("<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>load</td><td>fitgoodies.parsers.LongParserMock</td></tr>"
				+ "</table>");

		fixture.doTable(table);
		assertNotNull(FixtureTools.parse("42", Long.class, null));
	}
}
