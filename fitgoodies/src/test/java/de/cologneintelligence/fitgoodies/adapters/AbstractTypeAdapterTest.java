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


package de.cologneintelligence.fitgoodies.adapters;

import java.math.BigInteger;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.adapters.AbstractTypeAdapter;

import fit.Fixture;
import fit.TypeAdapter;

/**
 * $Id$
 * @author jwierum
 */
public class AbstractTypeAdapterTest extends FitGoodiesTestCase {
	public final void testConstructor() throws Exception {
		TypeAdapter expected = new TypeAdapter();
		Object target = new Object();

		expected.field = expected.getClass().getField("field");
		expected.fixture = new Fixture();
		expected.method = expected.getClass().getMethod("get", new Class<?>[]{});
		expected.target = target;
		expected.type = String.class;

		AbstractTypeAdapter<BigInteger> actual = new DummyTypeAdapter(expected, null);
		assertSame(expected.field, actual.field);
		assertSame(expected.fixture, actual.fixture);
		assertSame(expected.method, actual.method);
		assertSame(expected.target, actual.target);
		assertSame(expected.type, actual.type);
	}

	public final void testParameters() {
		TypeAdapter ta = new TypeAdapter();
		AbstractTypeAdapter<BigInteger> actual = new DummyTypeAdapter(ta, "xy");
		assertEquals("xy", actual.getParameter());

		actual = new DummyTypeAdapter(ta, "testformat");
		assertEquals("testformat", actual.getParameter());
	}
}
