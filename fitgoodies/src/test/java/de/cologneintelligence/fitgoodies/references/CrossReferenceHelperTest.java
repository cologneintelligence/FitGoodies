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


package de.cologneintelligence.fitgoodies.references;
import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import de.cologneintelligence.fitgoodies.references.processors.CrossReferenceProcessorMock;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

/**
 * @author jwierum
 */

public class CrossReferenceHelperTest extends FitGoodiesTestCase {
    private CrossReferenceHelper helper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
    }

    public final void testCrossReferenceRecognitionWithoutParams() {
        assertFalse(helper.containsCrossReference("test"));
        assertFalse(helper.containsCrossReference("${cr()}"));

        assertTrue(helper.containsCrossReference("${empty()}"));
        assertTrue(helper.containsCrossReference("${nonEmpty()}"));

        assertTrue(helper.containsCrossReference("x${empty()}y"));
    }

    public final void testCrossReferenceReognitionWithParameters() {
        assertTrue(helper.containsCrossReference("${x.put(y)}"));
        assertTrue(helper.containsCrossReference(
                "${namespace.get(param)}"));
        assertTrue(helper.containsCrossReference("${n.put(p)}"));

        assertTrue(helper.containsCrossReference("${n.containsValue(p)}"));

        assertFalse(helper.containsCrossReference("${.get(param)}"));
        assertFalse(helper.containsCrossReference("${namespace.get()}"));
    }

    public final void testCrossReferences() {
        StringBuilder actually = new StringBuilder("a ${nonEmpty()} string");
        CrossReference cr = helper.getCrossReference(actually);
        assertNull(cr);
        assertEquals("a ${nonEmpty()} string", actually.toString());

        actually = new StringBuilder("${nonEmpty()} string");
        cr = helper.getCrossReference(actually);
        assertEquals("nonEmpty", cr.getCommand());
        assertEquals(" string", actually.toString());

        actually = new StringBuilder("${x.put(y)} ${empty()}");
        cr = helper.getCrossReference(actually);
        assertEquals("put", cr.getCommand());
        assertEquals("x", cr.getNamespace());
        assertEquals("y", cr.getParameter());
        assertEquals(" ${empty()}", actually.toString());

        actually = new StringBuilder("${x.containsValue(y)} b");
        cr = helper.getCrossReference(actually);
        assertEquals("x", cr.getNamespace());
        assertEquals("containsValue", cr.getCommand());
        assertEquals("y", cr.getParameter());

        actually = new StringBuilder("${System.getProperty(key)} b");
        cr = helper.getCrossReference(actually);
        assertEquals("System", cr.getNamespace());
        assertEquals("getProperty", cr.getCommand());
        assertEquals("key", cr.getParameter());
    }

    private void checkParseBody(final String input, final Object param, final String expected)
            throws CrossReferenceProcessorShortcutException {
        String actual;
        actual = helper.parseBody(input, param);
        assertEquals(expected, actual);
    }

    public final void testParseBody()
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
            fail("missing exception");
        } catch (CrossReferenceProcessorShortcutException e) {
            assertTrue(e.isOk());
        }
    }

    public final void testComplexBody() throws CrossReferenceProcessorShortcutException {
        checkParseBody("put ${ns.put(word)} it ${ns.get(word)}", "in",
                "put in it in");

        try {
            helper.parseBody("x$${empty()}x", null);
            fail("missing exception");
        } catch (CrossReferenceProcessorShortcutException e) {
            assertTrue(e.isOk());
        }
    }


    public final void testParseBodyExceptions() {
        try {
            helper.parseBody("${empty()}", "x");
            fail("missing exception");
        } catch (CrossReferenceProcessorShortcutException e) {
        }

        try {
            helper.parseBody("${nonEmpty()}", "");
            fail("missing exception");
        } catch (CrossReferenceProcessorShortcutException e) {
        }
    }

    public final void testProcessors() throws CrossReferenceProcessorShortcutException {
        CrossReferenceProcessorMock mock = new CrossReferenceProcessorMock("x");

        helper.getProcessors().add(mock);
        assertTrue(mock.isCalledPattern());

        helper.parseBody("x ${y} y", null);
        assertFalse(mock.isCalledProcess());

        mock.reset();
        String actual = helper.parseBody("x ${x} y", null);
        assertTrue(mock.isCalledExtract());
        assertTrue(mock.isCalledProcess());
        assertEquals("x matched y", actual);
    }
}
