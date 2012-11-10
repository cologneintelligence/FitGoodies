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
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FixtureToolsTest.DummyValueFixture;
import fit.Fixture;
import fit.TypeAdapter;

/**
 * $Id$
 * @author jwierum
 */
public final class TypeAdapterHelperTest extends FitGoodiesTestCase {
    private TypeAdapterHelper helper;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        helper = DependencyManager.getOrCreate(TypeAdapterHelper.class);
    }

    public void testRegister() throws Exception {
        final Object target = new Object();
        final TypeAdapter typeAdapter = new TypeAdapter();
        typeAdapter.field = typeAdapter.getClass().getField("field");
        typeAdapter.fixture = new Fixture();
        typeAdapter.method = typeAdapter.getClass().getMethod("get", new Class<?>[]{});
        typeAdapter.target = target;
        typeAdapter.type = BigInteger.class;

        TypeAdapter actual = helper.getAdapter(typeAdapter, null);
        assertSame(typeAdapter, actual);

        helper.register(DummyTypeAdapter.class);

        actual = helper.getAdapter(typeAdapter, null);
        assertEquals(DummyTypeAdapter.class, actual.getClass());

        typeAdapter.type = java.math.BigDecimal.class;
        actual = helper.getAdapter(typeAdapter, null);
        assertSame(typeAdapter, actual);
    }

    public void testDefaults() throws Exception {
        final TypeAdapter typeAdapter = new TypeAdapter();
        typeAdapter.type = StringBuilder.class;

        AbstractTypeAdapter<?> actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, null);
        assertEquals(StringBuilder.class, actual.getType());

        typeAdapter.type = StringBuffer.class;

        actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, null);
        assertEquals(StringBuffer.class, actual.getType());

        typeAdapter.type = java.sql.Date.class;

        actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, null);
        assertEquals(java.sql.Date.class, actual.getType());

        typeAdapter.type = java.util.Date.class;

        actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, null);
        assertEquals(java.util.Date.class, actual.getType());

        typeAdapter.type = java.sql.Timestamp.class;

        actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, null);
        assertEquals(java.sql.Timestamp.class, actual.getType());
    }

    public void testParameter() throws Exception {
        final Object target = new Object();
        final TypeAdapter typeAdapter = new TypeAdapter();
        typeAdapter.field = typeAdapter.getClass().getField("field");
        typeAdapter.fixture = new Fixture();
        typeAdapter.method = typeAdapter.getClass().getMethod("get", new Class<?>[]{});
        typeAdapter.target = target;
        typeAdapter.type = BigInteger.class;

        helper.register(DummyTypeAdapter.class);

        AbstractTypeAdapter<?> actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, "hello");
        assertSame("hello", actual.getParameter());

        actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, "test");
        assertSame("test", actual.getParameter());
    }

    public void testRebindTypeAdapterWithArray() throws Exception {
        TypeAdapter ta;
        TypeAdapter actual;
        helper.register(DummyTypeAdapter.class);

        final DummyValueFixture fixture = new DummyValueFixture();
        ta = TypeAdapter.on(fixture, DummyValueFixture.class.getField("arr"));

        actual = helper.getAdapter(ta, null);

        assertEquals(ArrayTypeAdapter.class, actual.getClass());
        assertArray(new BigInteger[]{
                new BigInteger("1"), new BigInteger("2"), new BigInteger("3"),
        }, (BigInteger[]) actual.get());

        assertArray(new BigInteger[]{
                new BigInteger("42"), new BigInteger("42")
        }, (BigInteger[]) actual.parse("x, y"));

        actual = helper.getAdapter(ta, "parameter");
        assertArray(new BigInteger[]{new BigInteger("23")},
                (BigInteger[]) actual.parse("z"));
    }
}
