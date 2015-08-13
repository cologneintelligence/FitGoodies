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

package de.cologneintelligence.fitgoodies.types;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class TestableStringTest {

	@Test
	public void testEquals() {
		assertThat(new TestableString("test"), is(equalTo(new TestableString("test"))));
		assertThat(new TestableString("test"), is(not(equalTo(new TestableString("other text")))));
		assertThat(new TestableString(12), (Matcher) is(not(equalTo(12))));

		assertThat(new TestableString(null), is(equalTo(new TestableString(null))));

		assertThat(new TestableString("test"),
				(Matcher) is(equalTo(new InternalTestableString("t", InternalTestableString.TestType.STARTSWITH))));
	}

	@Test
	public void testHashCode() {
		assertThat(new TestableString("abc").hashCode(), is(new TestableString("abc").hashCode()));
		assertThat(new TestableString("123").hashCode(), is(new TestableString("123").hashCode()));
		assertThat(new TestableString("123").hashCode(), is(not(new TestableString("abc").hashCode())));
		assertThat(new TestableString(null).hashCode(), is(new TestableString(null).hashCode()));
	}
}
