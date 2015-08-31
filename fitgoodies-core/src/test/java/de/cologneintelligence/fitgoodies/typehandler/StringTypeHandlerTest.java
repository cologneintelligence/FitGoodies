/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
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

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringTypeHandlerTest extends FitGoodiesTestCase {

	private StringTypeHandler ta;

	@Before
	public void setUp() throws Exception {
		ta = new StringTypeHandler(null);
	}

	@Test
	public void testEquals() throws Exception {
		assertThat(ta.equals("x", "x"), is(true));
		assertThat(ta.equals("y", "y"), is(true));
		assertThat(ta.equals(null, null), is(true));

		assertThat(ta.equals("y", "x"), is(false));
		assertThat(ta.equals(null, "x"), is(false));
		assertThat(ta.equals("x", null), is(false));
	}

	@Test
	public void testEqualsWithWhitespaces() throws Exception {
		assertThat(ta.equals("  x", "x  "), is(true));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(ta.toString("Hello World"), is(equalTo("Hello World")));
		assertThat(ta.toString("Hello Worldxy"), is(equalTo("Hello Worldxy")));
		assertThat(ta.toString(null), is(equalTo("null")));
	}

	@Test
	public void testType() {
		assertThat(ta.getType(), (Matcher) is(equalTo(String.class)));
	}
}
