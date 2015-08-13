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


package de.cologneintelligence.fitgoodies.typehandler;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public class ArrayValueTypeHandlerTest extends FitGoodiesTestCase {
    @Mock
    private TypeHandler innerHandler;

    private ArrayTypeHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new ArrayTypeHandler(null, innerHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEqualsTrue() throws Exception {
        String[] arr1 = new String[] {"a", "b", "c"};
        String[] arr2 = new String[] {"a", "b", "c"};
        when(innerHandler.unsafeEquals("a", "a")).thenReturn(true);
        when(innerHandler.unsafeEquals("b", "b")).thenReturn(true);
        when(innerHandler.unsafeEquals("c", "c")).thenReturn(true);

        assertThat(handler.unsafeEquals(arr1, arr2), is(true));

        verify(innerHandler).unsafeEquals("a", "a");
        verify(innerHandler).unsafeEquals("b", "b");
        verify(innerHandler).unsafeEquals("c", "c");
    }

    @Test
    public void testEqualsFalse() throws Exception {
        String[] arr1 = new String[] {"a", "b", "c"};
        String[] arr2 = new String[] {"1", "2", "3"};
        when(innerHandler.unsafeEquals("a", "1")).thenReturn(true);
        when(innerHandler.unsafeEquals("b", "2")).thenReturn(false);

        assertThat(handler.unsafeEquals(arr1, arr2), is(false));

        verify(innerHandler).unsafeEquals("a", "1");
        verify(innerHandler).unsafeEquals("b", "2");
        verify(innerHandler, never()).unsafeEquals("c", "3");
    }

    @Test
    public void testEqualsWithLength() throws Exception {
        String[] arr1 = new String[9];
        String[] arr2 = new String[8];
        assertThat(handler.unsafeEquals(arr1, arr2), is(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testToString() throws Exception {
        String s[] = new String[10];
        s[0] = "Hello World";
        s[1] = "a test";

        when(innerHandler.toString("Hello World")).thenReturn("a1");
        when(innerHandler.toString("a test")).thenReturn("a2");
        when(innerHandler.toString(null)).thenReturn("null");

        assertThat(handler.toString(s), is(equalTo("a1, a2, null, null, null, null, null, null, null, null")));

        verify(innerHandler).toString("Hello World");
        verify(innerHandler, times(8)).toString(null);

        s[0] = "4";
        s[2] = "3";
        when(innerHandler.toString("4")).thenReturn("a3");
        when(innerHandler.toString("3")).thenReturn("a4");

        assertThat(handler.toString(s), is(equalTo("a3, a2, a4, null, null, null, null, null, null, null")));
    }

    @Test
    public void testParse() throws Exception {
        String toParse = "this, is,   a  , test";

        when(innerHandler.getType()).thenReturn(String.class);
        when(innerHandler.parse("this")).thenReturn("x");
        when(innerHandler.parse("is")).thenReturn("a");
        when(innerHandler.parse("a")).thenReturn("1");
        when(innerHandler.parse("test")).thenReturn("2");

        String[] array = (String[]) handler.parse(toParse);

        assertThat(array.length, is(equalTo((Object) 4)));
        assertThat("x", is(equalTo(array[0])));
        assertThat("a", is(equalTo(array[1])));
        assertThat("1", is(equalTo(array[2])));
        assertThat("2", is(equalTo(array[3])));
    }

    @Test
    public void testParseSingleValue() throws Exception {
        String toParse = "2";

        when(innerHandler.getType()).thenReturn(Integer.TYPE);
        when(innerHandler.parse("2")).thenReturn(4);

        int[] array = (int[]) handler.unsafeParse(toParse);

        assertThat(array.length, is(equalTo((Object) 1)));
        assertThat(array[0], is(4));
    }
}
