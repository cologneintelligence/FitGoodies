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

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.adapters.ArrayTypeAdapter;
import fit.Fixture;
import fit.TypeAdapter;

/**
 *
 * @author jwierum
 */
public class ArrayTypeAdapterTest extends FitGoodiesTestCase {
    public class StringBuilderContainer extends Fixture {
        public StringBuilder[] builder = new StringBuilder[10];
    }

    private TypeAdapter ta1;
    private TypeAdapter ta2;
    private StringBuilderContainer container1;
    private StringBuilderContainer container2;

    @Override
    public final void setUp() throws Exception {
        super.setUp();

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

    public final void testEquals() throws Exception {
        assertTrue(ta1.equals(ta1.get(), ta2.get()));
        assertTrue(ta1.equals(ta2.get(), ta1.get()));
        assertTrue(ta2.equals(ta1.get(), ta2.get()));

        container1.builder[0].append("x");
        assertFalse(ta1.equals(ta1.get(), ta2.get()));

        container1.builder[0] = null;
        assertFalse(ta1.equals(ta1.get(), ta2.get()));
        assertFalse(ta1.equals(ta2.get(), ta1.get()));

        container2.builder[0] = null;
        assertTrue(ta1.equals(ta1.get(), ta2.get()));

        container1.builder[1] = new StringBuilder("test");
        container2.builder[1] = new StringBuilder("test2");
        assertFalse(ta1.equals(ta1.get(), ta2.get()));

        container1.builder[1].append("2");
        assertTrue(ta1.equals(ta1.get(), ta2.get()));
    }

    public final void testEqualsWithLength() throws Exception {
        container1.builder = new StringBuilder[9];
        container2.builder = new StringBuilder[9];
        assertTrue(ta1.equals(ta1.get(), ta2.get()));

        container1.builder = new StringBuilder[7];
        container2.builder = new StringBuilder[9];
        assertFalse(ta1.equals(ta1.get(), ta2.get()));

        container1.builder = new StringBuilder[9];
        container2.builder = new StringBuilder[7];
        assertFalse(ta1.equals(ta1.get(), ta2.get()));

        container1.builder = new StringBuilder[7];
        container2.builder = new StringBuilder[7];
        assertTrue(ta1.equals(ta1.get(), ta2.get()));
    }

    public final void testToString() throws Exception {
        container1.builder[1] = new StringBuilder("a test");
        assertEquals("Hello World, a test, null, null, null, null, null, null, null, null",
                ta1.toString(ta1.get()));

        container1.builder[1].append("x");
        container1.builder[2] = new StringBuilder("a");
        container1.builder[3] = new StringBuilder("b");
        container1.builder[4] = new StringBuilder("c");
        assertEquals("Hello World, a testx, a, b, c, null, null, null, null, null",
                ta1.toString(ta1.get()));

        container1.builder = null;
        assertEquals("", ta1.toString(ta1.get()));
    }

    public final void testParse() throws Exception {
        String toParse = "this, is, a, test";
        StringBuilder[] array = (StringBuilder[]) ta1.parse(toParse);

        assertEquals(4, array.length);
        assertEquals(array[0].toString(), "this");
        assertEquals(array[1].toString(), "is");
        assertEquals(array[2].toString(), "a");
        assertEquals(array[3].toString(), "test");

        toParse = "single";
        array = (StringBuilder[]) ta1.parse(toParse);

        assertEquals(1, array.length);
        assertEquals(array[0].toString(), "single");
    }
}
