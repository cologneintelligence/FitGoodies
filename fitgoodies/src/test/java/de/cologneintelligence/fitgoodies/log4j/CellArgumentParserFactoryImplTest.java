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
import de.cologneintelligence.fitgoodies.Parse;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public final class CellArgumentParserFactoryImplTest extends FitGoodiesTestCase {
	private CellArgumentParserFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new CellArgumentParserFactoryImpl();
	}

	@Test
	public void testReturn() {
		Parse cell = parseTd("cell[x=y]");
		CellArgumentParser parser = factory.getParserFor(cell);

		assertThat(parser.getClass(), (Matcher) is(equalTo(CellArgumentParserImpl.class)));
	}

	@Test
	public void testParameterProcessing() {
		Parse cell = parseTd("cell[x=y]");
		CellArgumentParser parser = factory.getParserFor(cell);

		Map<String, String> parameters = parser.getExtractedCommandParameters();

		assertThat(cell.text(), is(equalTo("cell")));
		assertThat(parameters.get("x"), is(equalTo("y")));
	}
}
