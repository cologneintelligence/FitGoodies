/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
 * This file is part of FitGoodies.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package fitgoodies.log4j;

import java.text.ParseException;
import java.util.Map;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;

/**
 * @author jwierum
 * @version $Id: CellArgumentParserFactoryImplTest.java 197 2009-08-21 12:30:26Z jwierum $
 *
 */
public final class CellArgumentParserFactoryImplTest extends FitGoodiesTestCase {
	private CellArgumentParserFactory factory;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		factory = new CellArgumentParserFactoryImpl();
	}

	public void testReturn() throws ParseException {
		Parse cell = new Parse("<td>cell[x=y]</td>", new String[]{"td"});
		CellArgumentParser parser = factory.getParserFor(cell);

		assertEquals(CellArgumentParserImpl.class, parser.getClass());
	}

	public void testParameterProcessing() throws ParseException {
		Parse cell = new Parse("<td>cell[x=y]</td>", new String[]{"td"});
		CellArgumentParser parser = factory.getParserFor(cell);

		Map<String, String> parameters = parser.getExtractedCommandParameters();

		assertEquals("cell", cell.text());
		assertEquals("y", parameters.get("x"));
	}
}
