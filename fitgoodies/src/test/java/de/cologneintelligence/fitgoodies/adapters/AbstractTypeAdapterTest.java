/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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


package de.cologneintelligence.fitgoodies.adapters;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.TypeAdapter;
import org.junit.Test;

import java.math.BigInteger;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class AbstractTypeAdapterTest extends FitGoodiesTestCase {
	@Test
	public void testConstructor() throws Exception {
		TypeAdapter expected = new TypeAdapter();
		Object target = new Object();

		expected.field = expected.getClass().getField("field");
		expected.fixture = new Fixture();
		expected.method = expected.getClass().getMethod("get");
		expected.target = target;
		expected.type = String.class;

		AbstractTypeAdapter<BigInteger> actual = new DummyTypeAdapter(expected, null);
		assertThat(actual.field, is(sameInstance(expected.field)));
		assertThat(actual.fixture, is(sameInstance(expected.fixture)));
		assertThat(actual.method, is(sameInstance(expected.method)));
		assertThat(actual.target, is(sameInstance(expected.target)));
		assertThat(actual.type, is(sameInstance(expected.type)));
	}

	@Test
	public void testParameters() {
		TypeAdapter ta = new TypeAdapter();
		AbstractTypeAdapter<BigInteger> actual = new DummyTypeAdapter(ta, "xy");
		assertThat(actual.getParameter(), is(equalTo("xy")));

		actual = new DummyTypeAdapter(ta, "testformat");
		assertThat(actual.getParameter(), is(equalTo("testformat")));
	}
}
