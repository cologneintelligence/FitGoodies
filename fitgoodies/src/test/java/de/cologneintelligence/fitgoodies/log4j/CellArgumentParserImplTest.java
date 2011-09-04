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

package de.cologneintelligence.fitgoodies.log4j;

import java.text.ParseException;
import java.util.Map;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.log4j.CellArgumentParserImpl;

import fit.Parse;

/**
 * @author jwierum
 * @version $Id$
 */
public final class CellArgumentParserImplTest extends FitGoodiesTestCase {
	private Parse makeCell(final String innerText) throws ParseException {
		return new Parse("<td>" + innerText + "</td>", new String[]{"td"});
	}

	public void testRegularParsing() throws ParseException {
		Parse cell = makeCell("x[a=b, C=d , d =  e]");

		CellArgumentParserImpl parser = new CellArgumentParserImpl(cell);
		Map<String, String> parameters = parser.getExtractedCommandParameters();

		assertEquals(3, parameters.keySet().size());
		assertEquals("b", parameters.get("a"));
		assertEquals("d", parameters.get("c"));
		assertEquals("e", parameters.get("d"));
		assertEquals("x", cell.text());
	}

	public void testRegularParsingWithMultipleEqualSigns() throws ParseException {
		Parse cell = makeCell("y [some=crazy=command, 1=2=3]");

		CellArgumentParserImpl parser = new CellArgumentParserImpl(cell);
		Map<String, String> parameters = parser.getExtractedCommandParameters();

		assertEquals(2, parameters.keySet().size());
		assertEquals("crazy=command", parameters.get("some"));
		assertEquals("2=3", parameters.get("1"));
		assertEquals("y", cell.text());
	}

	public void testError() throws ParseException {
		Parse cell = makeCell("command[oops...]");
		CellArgumentParserImpl parser = new CellArgumentParserImpl(cell);

		try {
			parser.getExtractedCommandParameters();
			fail("could parse invalid input");
		} catch (IllegalArgumentException e) {
		}

		cell = makeCell("command[a=b,,c=d]");
		parser = new CellArgumentParserImpl(cell);

		try {
			parser.getExtractedCommandParameters();
			fail("could parse invalid input");
		} catch (IllegalArgumentException e) {
		}
	}
}
