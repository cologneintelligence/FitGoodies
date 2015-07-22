/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import fit.Parse;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public final class CellArgumentParserImplTest extends FitGoodiesTestCase {

	@Test
	public void testRegularParsing() {
		Parse cell = parseTd("x[a=b, C=d , d =  e]");

		CellArgumentParserImpl parser = new CellArgumentParserImpl(cell);
		Map<String, String> parameters = parser.getExtractedCommandParameters();

		assertThat(parameters.keySet().size(), is(equalTo((Object) 3)));
		assertThat(parameters.get("a"), is(equalTo("b")));
		assertThat(parameters.get("c"), is(equalTo("d")));
		assertThat(parameters.get("d"), is(equalTo("e")));
		assertThat(cell.text(), is(equalTo("x")));
	}

	@Test
	public void testRegularParsingWithMultipleEqualSigns() {
		Parse cell = parseTd("y [some=crazy=command, 1=2=3]");

		CellArgumentParserImpl parser = new CellArgumentParserImpl(cell);
		Map<String, String> parameters = parser.getExtractedCommandParameters();

		assertThat(parameters.keySet().size(), is(equalTo((Object) 2)));
		assertThat(parameters.get("some"), is(equalTo("crazy=command")));
		assertThat(parameters.get("1"), is(equalTo("2=3")));
		assertThat(cell.text(), is(equalTo("y")));
	}

	@Test
	public void testError() {
		Parse cell = parseTd("command[oops...]");
		CellArgumentParserImpl parser = new CellArgumentParserImpl(cell);

		try {
			parser.getExtractedCommandParameters();
			Assert.fail("could parse invalid input");
		} catch (IllegalArgumentException e) {
		}

		cell = parseTd("command[a=b,,c=d]");
		parser = new CellArgumentParserImpl(cell);

		try {
			parser.getExtractedCommandParameters();
			Assert.fail("could parse invalid input");
		} catch (IllegalArgumentException e) {
		}
	}
}
