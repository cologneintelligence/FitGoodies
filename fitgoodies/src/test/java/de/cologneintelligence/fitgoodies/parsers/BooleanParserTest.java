/*
 * Copyright (c) 2009-2015  Cologne Intelligence GmbH
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

package de.cologneintelligence.fitgoodies.parsers;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BooleanParserTest {

	private BooleanParser parser;

	@Before
	public void setUp() {
		parser = new BooleanParser();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseError1() throws Exception {
		parser.parse("oui", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseError2() throws Exception {
		parser.parse("non", null);
	}

	@Test
	public void testParse() throws Exception {
		assertThat(parser.parse("yes", null), (Matcher) is(true));
		assertThat(parser.parse("yEs", null), (Matcher) is(true));
		assertThat(parser.parse("true", null), (Matcher) is(true));
		assertThat(parser.parse("1", null), (Matcher) is(true));
		assertThat(parser.parse("on", null), (Matcher) is(true));

		assertThat(parser.parse("NO", null), (Matcher) is(false));
		assertThat(parser.parse("FALSE", null), (Matcher) is(false));
		assertThat(parser.parse("0", null), (Matcher) is(false));
		assertThat(parser.parse("off", null), (Matcher) is(false));
	}

	@Test
	public void testGetType() throws Exception {
		assertThat(parser.getType(), is(equalTo(Boolean.class)));
	}
}
