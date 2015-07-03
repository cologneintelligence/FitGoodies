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
import de.cologneintelligence.fitgoodies.references.processors.CrossReferenceProcessorMock;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;



public class CrossReferenceHelperTest extends FitGoodiesTestCase {
    private CrossReferenceHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
    }

    @Test
    public void testCrossReferenceRecognitionWithoutParams() {
        assertThat(helper.containsCrossReference("test"), is(false));

        assertThat(helper.containsCrossReference("${cr()}"), is(false));

        assertThat(helper.containsCrossReference("${empty()}"), is(true));
        assertThat(helper.containsCrossReference("${nonEmpty()}"), is(true));

        assertThat(helper.containsCrossReference("x${empty()}y"), is(true));
    }

    @Test
    public void testCrossReferenceReognitionWithParameters() {
        assertThat(helper.containsCrossReference("${x.put(y)}"), is(true));
        assertThat(helper.containsCrossReference(
                "${namespace.get(param)}"), is(true));
        assertThat(helper.containsCrossReference("${n.put(p)}"), is(true));

        assertThat(helper.containsCrossReference("${n.containsValue(p)}"), is(true));

        assertThat(helper.containsCrossReference("${.get(param)}"), is(false));

        assertThat(helper.containsCrossReference("${namespace.get()}"), is(false));

    }

    @Test
    public void testCrossReferences() {
        StringBuilder actually = new StringBuilder("a ${nonEmpty()} string");
        CrossReference cr = helper.getCrossReference(actually);
        assertThat(cr, is(nullValue()));
        assertThat(actually.toString(), is(equalTo("a ${nonEmpty()} string")));

        actually = new StringBuilder("${nonEmpty()} string");
        cr = helper.getCrossReference(actually);
        assertThat(cr.getCommand(), is(equalTo("nonEmpty")));
        assertThat(actually.toString(), is(equalTo(" string")));

        actually = new StringBuilder("${x.put(y)} ${empty()}");
        cr = helper.getCrossReference(actually);
        assertThat(cr.getCommand(), is(equalTo("put")));
        assertThat(cr.getNamespace(), is(equalTo("x")));
        assertThat(cr.getParameter(), is(equalTo("y")));
        assertThat(actually.toString(), is(equalTo(" ${empty()}")));

        actually = new StringBuilder("${x.containsValue(y)} b");
        cr = helper.getCrossReference(actually);
        assertThat(cr.getNamespace(), is(equalTo("x")));
        assertThat(cr.getCommand(), is(equalTo("containsValue")));
        assertThat(cr.getParameter(), is(equalTo("y")));

        actually = new StringBuilder("${System.getProperty(key)} b");
        cr = helper.getCrossReference(actually);
        assertThat(cr.getNamespace(), is(equalTo("System")));
        assertThat(cr.getCommand(), is(equalTo("getProperty")));
        assertThat(cr.getParameter(), is(equalTo("key")));
    }

    private void checkParseBody(final String input, final Object param, final String expected)
            throws CrossReferenceProcessorShortcutException {
        String actual;
        actual = helper.parseBody(input, param);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void testParseBody()
            throws CrossReferenceProcessorShortcutException {
        checkParseBody("a test", null, "a test");

        checkParseBody("a ${x.put(y)} test", "little", "a little test");
        checkParseBody("${x.get(y)} house", "x", "little house");
        checkParseBody("${ns.get(error)}", null,
                "ns.error: cross reference could not be resolved!");
        checkParseBody("it ${x.containsValue(y)}",
                new StringBuffer("works"), "it works");

        try {
            helper.parseBody("${empty()}", "");
            Assert.fail("missing exception");
        } catch (CrossReferenceProcessorShortcutException e) {
            assertThat(e.isOk(), is(true));
        }
    }

    @Test
    public void testComplexBody() throws CrossReferenceProcessorShortcutException {
        checkParseBody("put ${ns.put(word)} it ${ns.get(word)}", "in",
                "put in it in");

        try {
            helper.parseBody("x$${empty()}x", null);
            Assert.fail("missing exception");
        } catch (CrossReferenceProcessorShortcutException e) {
            assertThat(e.isOk(), is(true));
        }
    }


    @Test(expected = CrossReferenceProcessorShortcutException.class)
    public void testParseBodyExceptions1() throws CrossReferenceProcessorShortcutException {
        helper.parseBody("${empty()}", "x");
    }

    @Test(expected = CrossReferenceProcessorShortcutException.class)
    public void testParseBodyExceptions2() throws CrossReferenceProcessorShortcutException {
        helper.parseBody("${nonEmpty()}", "");
    }

    @Test
    public void testProcessors() throws CrossReferenceProcessorShortcutException {
        CrossReferenceProcessorMock mock = new CrossReferenceProcessorMock("x");

        helper.getProcessors().add(mock);
        assertThat(mock.isCalledPattern(), is(true));

        helper.parseBody("x ${y} y", null);
        assertThat(mock.isCalledProcess(), is(false));

        mock.reset();
        String actual = helper.parseBody("x ${x} y", null);
        assertThat(mock.isCalledExtract(), is(true));
        assertThat(mock.isCalledProcess(), is(true));
        assertThat(actual, is(equalTo("x matched y")));
    }
}
