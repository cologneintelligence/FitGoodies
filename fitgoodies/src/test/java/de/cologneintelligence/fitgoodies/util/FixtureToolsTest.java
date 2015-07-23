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


package de.cologneintelligence.fitgoodies.util;

import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.TypeAdapter;
import de.cologneintelligence.fitgoodies.adapters.AbstractTypeAdapter;
import de.cologneintelligence.fitgoodies.adapters.DummyTypeAdapter;
import de.cologneintelligence.fitgoodies.adapters.TypeAdapterHelper;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Test;

import java.math.BigInteger;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class FixtureToolsTest extends FitGoodiesTestCase {
    public static class DummyValueFixture extends Fixture {
        public int x;
        public String y;

        public int a;
        public String b;

        public BigInteger[] arr = new BigInteger[]{
                new BigInteger("1"),
                new BigInteger("2"),
                new BigInteger("3")};
    }


    @Test
    public void testRebindTypeAdapterWithParameter() throws Exception {
        final TypeAdapter ta = new TypeAdapter();
        AbstractTypeAdapter<?> actual;

        ta.type = BigInteger.class;
        final TypeAdapterHelper helper = DependencyManager.getOrCreate(TypeAdapterHelper.class);
        helper.register(DummyTypeAdapter.class);

        actual = (AbstractTypeAdapter<?>) helper.getAdapter(ta, "test");
        assertThat(actual.getParameter(), is(equalTo("test")));

        actual = (AbstractTypeAdapter<?>) helper.getAdapter(ta, "parameter");
        assertThat(actual.getParameter(), is(equalTo("parameter")));
    }

}
