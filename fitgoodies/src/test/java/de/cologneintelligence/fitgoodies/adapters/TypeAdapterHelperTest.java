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
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FixtureToolsTest.DummyValueFixture;
import fit.Fixture;
import fit.TypeAdapter;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public final class TypeAdapterHelperTest extends FitGoodiesTestCase {
    private TypeAdapterHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = DependencyManager.getOrCreate(TypeAdapterHelper.class);
    }

    @Test
    public void testRegister() throws Exception {
        final Object target = new Object();
        final TypeAdapter typeAdapter = new TypeAdapter();
        typeAdapter.field = typeAdapter.getClass().getField("field");
        typeAdapter.fixture = new Fixture();
        typeAdapter.method = typeAdapter.getClass().getMethod("get");
        typeAdapter.target = target;
        typeAdapter.type = BigInteger.class;

        TypeAdapter actual = helper.getAdapter(typeAdapter, null);
        assertThat(actual, is(sameInstance(typeAdapter)));

        helper.register(DummyTypeAdapter.class);

        actual = helper.getAdapter(typeAdapter, null);
        assertThat(actual.getClass(), (Matcher) is(equalTo(DummyTypeAdapter.class)));

        typeAdapter.type = java.math.BigDecimal.class;
        actual = helper.getAdapter(typeAdapter, null);
        assertThat(actual, is(sameInstance(typeAdapter)));
    }

    @Test
    public void testDefaults() throws Exception {
        final TypeAdapter typeAdapter = new TypeAdapter();
        typeAdapter.type = StringBuilder.class;

        AbstractTypeAdapter<?> actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, null);
        assertThat(actual.getType(), (Matcher) is(equalTo(StringBuilder.class)));

        typeAdapter.type = StringBuffer.class;

        actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, null);
        assertThat(actual.getType(), (Matcher) is(equalTo(StringBuffer.class)));

        typeAdapter.type = java.sql.Date.class;

        actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, null);
        assertThat(actual.getType(), (Matcher)is(equalTo(java.sql.Date.class)));

        typeAdapter.type = java.util.Date.class;

        actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, null);
        assertThat(actual.getType(), (Matcher) is(equalTo(java.util.Date.class)));

        typeAdapter.type = java.sql.Timestamp.class;

        actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, null);
        assertThat(actual.getType(), (Matcher) is(equalTo(java.sql.Timestamp.class)));
    }

    @Test
    public void testParameter() throws Exception {
        final Object target = new Object();
        final TypeAdapter typeAdapter = new TypeAdapter();
        typeAdapter.field = typeAdapter.getClass().getField("field");
        typeAdapter.fixture = new Fixture();
        typeAdapter.method = typeAdapter.getClass().getMethod("get");
        typeAdapter.target = target;
        typeAdapter.type = BigInteger.class;

        helper.register(DummyTypeAdapter.class);

        AbstractTypeAdapter<?> actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, "hello");
        assertThat(actual.getParameter(), is(sameInstance("hello")));

        actual = (AbstractTypeAdapter<?>)
                helper.getAdapter(typeAdapter, "test");
        assertThat(actual.getParameter(), is(sameInstance("test")));
    }

    @Test
    public void testRebindTypeAdapterWithArray() throws Exception {
        TypeAdapter ta;
        TypeAdapter actual;
        helper.register(DummyTypeAdapter.class);

        final DummyValueFixture fixture = new DummyValueFixture();
        ta = TypeAdapter.on(fixture, DummyValueFixture.class.getField("arr"));

        actual = helper.getAdapter(ta, null);

        assertThat(actual.getClass(), (Matcher) is(equalTo(ArrayTypeAdapter.class)));
        assertThat(Arrays.asList(new BigInteger("1"), new BigInteger("2"), new BigInteger("3")), is(equalTo(Arrays.asList((BigInteger[]) actual.get()))));

        assertThat(Arrays.asList(new BigInteger("42"), new BigInteger("42")), is(equalTo(Arrays.asList((BigInteger[]) actual.parse("x, y")))));

        actual = helper.getAdapter(ta, "parameter");
        assertThat(Arrays.asList(new BigInteger("23")), is(equalTo(Arrays.asList((BigInteger[]) actual.parse("z")))));
    }
}
