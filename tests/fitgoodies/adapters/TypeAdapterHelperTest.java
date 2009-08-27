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


package fitgoodies.adapters;

import java.math.BigInteger;

import fit.Fixture;
import fit.TypeAdapter;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.util.FixtureToolsTest.DummyValueFixture;

/**
 * $Id$
 * @author jwierum
 */
public final class TypeAdapterHelperTest extends FitGoodiesTestCase {
	public void testSingleton() {
		TypeAdapterHelper expected = TypeAdapterHelper.instance();

		assertNotNull(expected);
		assertSame(expected, TypeAdapterHelper.instance());

		TypeAdapterHelper.reset();
		assertNotSame(expected, TypeAdapterHelper.instance());
	}

	public void testRegister() throws Exception {
		Object target = new Object();
		TypeAdapter typeAdapter = new TypeAdapter();
		typeAdapter.field = typeAdapter.getClass().getField("field");
		typeAdapter.fixture = new Fixture();
		typeAdapter.method = typeAdapter.getClass().getMethod("get", new Class<?>[]{});
		typeAdapter.target = target;
		typeAdapter.type = BigInteger.class;

		TypeAdapter actual = TypeAdapterHelper.instance().getAdapter(typeAdapter, null);
		assertSame(typeAdapter, actual);

		TypeAdapterHelper.instance().register(DummyTypeAdapter.class);

		actual = TypeAdapterHelper.instance().getAdapter(typeAdapter, null);
		assertEquals(DummyTypeAdapter.class, actual.getClass());

		typeAdapter.type = java.math.BigDecimal.class;
		actual = TypeAdapterHelper.instance().getAdapter(typeAdapter, null);
		assertSame(typeAdapter, actual);
	}

	public void testDefaults() throws Exception {
		TypeAdapter typeAdapter = new TypeAdapter();
		typeAdapter.type = StringBuilder.class;

		AbstractTypeAdapter<?> actual = (AbstractTypeAdapter<?>)
				TypeAdapterHelper.instance().getAdapter(typeAdapter, null);
		assertEquals(StringBuilder.class, actual.getType());

		typeAdapter.type = StringBuffer.class;

		actual = (AbstractTypeAdapter<?>)
				TypeAdapterHelper.instance().getAdapter(typeAdapter, null);
		assertEquals(StringBuffer.class, actual.getType());

		typeAdapter.type = java.sql.Date.class;

		actual = (AbstractTypeAdapter<?>)
			TypeAdapterHelper.instance().getAdapter(typeAdapter, null);
		assertEquals(java.sql.Date.class, actual.getType());

		typeAdapter.type = java.util.Date.class;

		actual = (AbstractTypeAdapter<?>)
			TypeAdapterHelper.instance().getAdapter(typeAdapter, null);
		assertEquals(java.util.Date.class, actual.getType());
	}

	public void testParameter() throws Exception {
		Object target = new Object();
		TypeAdapter typeAdapter = new TypeAdapter();
		typeAdapter.field = typeAdapter.getClass().getField("field");
		typeAdapter.fixture = new Fixture();
		typeAdapter.method = typeAdapter.getClass().getMethod("get", new Class<?>[]{});
		typeAdapter.target = target;
		typeAdapter.type = BigInteger.class;

		TypeAdapterHelper.instance().register(DummyTypeAdapter.class);

		AbstractTypeAdapter<?> actual = (AbstractTypeAdapter<?>)
			TypeAdapterHelper.instance().getAdapter(typeAdapter, "hello");
		assertSame("hello", actual.getParameter());

		actual = (AbstractTypeAdapter<?>)
			TypeAdapterHelper.instance().getAdapter(typeAdapter, "test");
		assertSame("test", actual.getParameter());
	}

	public void testRebindTypeAdapterWithArray() throws Exception {
		TypeAdapter ta;
		TypeAdapter actual;
		TypeAdapterHelper.instance().register(DummyTypeAdapter.class);

		DummyValueFixture fixture = new DummyValueFixture();
		ta = TypeAdapter.on(fixture, DummyValueFixture.class.getField("arr"));

		actual = TypeAdapterHelper.instance().getAdapter(ta, null);

		assertEquals(ArrayTypeAdapter.class, actual.getClass());
		assertArray(new BigInteger[]{
				new BigInteger("1"), new BigInteger("2"), new BigInteger("3"),
		}, (BigInteger[]) actual.get());

		assertArray(new BigInteger[]{
				new BigInteger("42"), new BigInteger("42")
		}, (BigInteger[]) actual.parse("x, y"));

		actual = TypeAdapterHelper.instance().getAdapter(ta, "parameter");
		assertArray(new BigInteger[]{new BigInteger("23")},
				(BigInteger[]) actual.parse("z"));
	}
}
