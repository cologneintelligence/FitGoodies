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

package de.cologneintelligence.fitgoodies.typehandler;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BooleanTypeHandlerTest {

	private BooleanTypeHandler parser;

	@Before
	public void setUp() {
		parser = new BooleanTypeHandler(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseError1() throws Exception {
		parser.unsafeParse("oui");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseError2() throws Exception {
		parser.unsafeParse("non");
	}

	@Test
	public void testParse() throws Exception {
		assertThat(parser.unsafeParse("yes"), (Matcher) is(true));
		assertThat(parser.unsafeParse("yEs"), (Matcher) is(true));
		assertThat(parser.unsafeParse("true"), (Matcher) is(true));
		assertThat(parser.unsafeParse("1"), (Matcher) is(true));
		assertThat(parser.unsafeParse("on"), (Matcher) is(true));

		assertThat(parser.unsafeParse("NO"), (Matcher) is(false));
		assertThat(parser.unsafeParse("FALSE"), (Matcher) is(false));
		assertThat(parser.unsafeParse("0"), (Matcher) is(false));
		assertThat(parser.unsafeParse("off"), (Matcher) is(false));
	}

	@Test
	public void testGetType() throws Exception {
		assertThat(parser.getType(), is(equalTo(Boolean.class)));
	}
}

