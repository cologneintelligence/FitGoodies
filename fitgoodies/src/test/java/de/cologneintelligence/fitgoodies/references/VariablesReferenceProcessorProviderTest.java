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


package de.cologneintelligence.fitgoodies.references;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


public class VariablesReferenceProcessorProviderTest extends FitGoodiesTestCase {
    private CellProcessorProvider provider;

    @Mock
    TypeHandler typeHandler;

    @Before
    public void setUp() throws Exception {
        provider = new VariablesReferenceProcessorProvider();
    }

    @Test
    public void testPattern() {
        assertThat(provider.canProcess("${x${ns.get(one)}"), is(true));
        assertThat(provider.canProcess("${ns.get(two)}y"), is(true));
        assertThat(provider.canProcess("${x.get(three)}"), is(true));
        assertThat(provider.canProcess("x${xy.put(four)}"), is(true));
        assertThat(provider.canProcess("${six.containsValue(five)}"), is(true));
        assertThat(provider.canProcess("${six.put(five, /a\\/(b)\\/c/)}}"), is(true));
        assertThat(provider.canProcess("${put(a, /[a-z]*/)}"), is(true));

        assertThat(provider.canProcess("other"), is(false));
        assertThat(provider.canProcess("${other}"), is(false));
        assertThat(provider.canProcess("${ns.op(bla)}"), is(false));
    }

    @Test
    public void testGetPut() throws Exception {
        CellProcessor processorPre1 = provider.create("${ns.put(x)}");
        CellProcessor processorPre2 = provider.create("${put(x)}a");
        CellProcessor processorPre3 = provider.create("${ns2.put(x)}");
        CellProcessor processorPre4 = provider.create("b${ns.put(y)}");

        when(typeHandler.toString("v1")).thenReturn("a", "e");
        when(typeHandler.toString("v2")).thenReturn("b");
        when(typeHandler.toString("v3")).thenReturn("c");
        when(typeHandler.toString("v4")).thenReturn("d");

        processorPre1.postprocess("v1", typeHandler);
        processorPre2.postprocess("v2", typeHandler);
        processorPre3.postprocess("v3", typeHandler);
        processorPre4.postprocess("v4", typeHandler);
        processorPre1.postprocess("v1", typeHandler);

        CellProcessor processorPost1 = provider.create("x${ns.get(x)}y");
        CellProcessor processorPost2 = provider.create("${get(x)}");
        CellProcessor processorPost3 = provider.create("${ns2.get(x)}");
        CellProcessor processorPost4 = provider.create("${ns.get(y)}");

        assertThat(processorPost1.preprocess(), is(equalTo("xey")));
        assertThat(processorPost2.preprocess(), is(equalTo("b")));
        assertThat(processorPost3.preprocess(), is(equalTo("c")));
        assertThat(processorPost4.preprocess(), is(equalTo("d")));
    }

    @Test
    public void unknownVariableYieldsErrorString() {
        assertThat(provider.create("${get(x)}").preprocess(), is(equalTo("Unknown variable: x")));
        assertThat(provider.create("${ns.get(y)}").preprocess(), is(equalTo("Unknown variable: ns.y")));
    }

    @Test
    public void testRegexExtraction() {
        when(typeHandler.toString("good")).thenReturn("123testABC");

        provider.create("${put(a, /([a-z]+)/)}").postprocess("good", typeHandler);

        assertThat(provider.create("${get(a)}").preprocess(), is("test"));
    }

    @Test
    public void testFailedRegexExtraction() {
        when(typeHandler.toString("good")).thenReturn("01234");

        provider.create("${put(a, /([a-z]+)/)}").postprocess("good", typeHandler);

        assertThat(provider.create("${get(a)}").preprocess(), is(""));
    }
}
