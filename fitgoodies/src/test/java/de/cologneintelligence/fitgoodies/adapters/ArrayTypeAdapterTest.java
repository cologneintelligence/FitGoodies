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
import fit.Fixture;
import fit.TypeAdapter;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class ArrayTypeAdapterTest extends FitGoodiesTestCase {
    public class StringBuilderContainer extends Fixture {
        public StringBuilder[] builder = new StringBuilder[10];
    }

    private TypeAdapter ta1;
    private TypeAdapter ta2;
    private StringBuilderContainer container1;
    private StringBuilderContainer container2;

    @Before
    public void setUp() throws Exception {
        container1 = new StringBuilderContainer();
        container1.builder[0] = new StringBuilder("Hello World");

        container2 = new StringBuilderContainer();
        container2.builder[0] = new StringBuilder("Hello World");

        TypeAdapter ta = TypeAdapter.on(container1,
                StringBuilderContainer.class.getField("builder"));
        ta1 = new ArrayTypeAdapter(ta, null, new TypeAdapterHelper());

        ta = TypeAdapter.on(container2,
                StringBuilderContainer.class.getField("builder"));
        ta2 = new ArrayTypeAdapter(ta, null, new TypeAdapterHelper());
    }

    @Test
    public void testEquals() throws Exception {
        assertThat(ta1.equals(ta1.get(), ta2.get()), is(true));
        assertThat(ta1.equals(ta2.get(), ta1.get()), is(true));
        assertThat(ta2.equals(ta1.get(), ta2.get()), is(true));

        container1.builder[0].append("x");
        assertThat(ta1.equals(ta1.get(), ta2.get()), is(false));

        container1.builder[0] = null;
        assertThat(ta1.equals(ta1.get(), ta2.get()), is(false));

        assertThat(ta1.equals(ta2.get(), ta1.get()), is(false));

        container2.builder[0] = null;
        assertThat(ta1.equals(ta1.get(), ta2.get()), is(true));

        container1.builder[1] = new StringBuilder("test");
        container2.builder[1] = new StringBuilder("test2");
        assertThat(ta1.equals(ta1.get(), ta2.get()), is(false));

        container1.builder[1].append("2");
        assertThat(ta1.equals(ta1.get(), ta2.get()), is(true));
    }

    @Test
    public void testEqualsWithLength() throws Exception {
        container1.builder = new StringBuilder[9];
        container2.builder = new StringBuilder[9];
        assertThat(ta1.equals(ta1.get(), ta2.get()), is(true));

        container1.builder = new StringBuilder[7];
        container2.builder = new StringBuilder[9];
        assertThat(ta1.equals(ta1.get(), ta2.get()), is(false));

        container1.builder = new StringBuilder[9];
        container2.builder = new StringBuilder[7];
        assertThat(ta1.equals(ta1.get(), ta2.get()), is(false));

        container1.builder = new StringBuilder[7];
        container2.builder = new StringBuilder[7];
        assertThat(ta1.equals(ta1.get(), ta2.get()), is(true));
    }

    @Test
    public void testToString() throws Exception {
        container1.builder[1] = new StringBuilder("a test");
        assertThat(ta1.toString(ta1.get()), is(equalTo("Hello World, a test, null, null, null, null, null, null, null, null")));

        container1.builder[1].append("x");
        container1.builder[2] = new StringBuilder("a");
        container1.builder[3] = new StringBuilder("b");
        container1.builder[4] = new StringBuilder("c");
        assertThat(ta1.toString(ta1.get()), is(equalTo("Hello World, a testx, a, b, c, null, null, null, null, null")));

        container1.builder = null;
        assertThat(ta1.toString(ta1.get()), is(equalTo("")));
    }

    @Test
    public void testParse() throws Exception {
        String toParse = "this, is, a, test";
        StringBuilder[] array = (StringBuilder[]) ta1.parse(toParse);

        assertThat(array.length, is(equalTo((Object) 4)));
        assertThat("this", is(equalTo(array[0].toString())));
        assertThat("is", is(equalTo(array[1].toString())));
        assertThat("a", is(equalTo(array[2].toString())));
        assertThat("test", is(equalTo(array[3].toString())));

        toParse = "single";
        array = (StringBuilder[]) ta1.parse(toParse);

        assertThat(array.length, is(equalTo((Object) 1)));
        assertThat("single", is(equalTo(array[0].toString())));
    }
}
