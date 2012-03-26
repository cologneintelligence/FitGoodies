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


package de.cologneintelligence.fitgoodies.references.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import de.cologneintelligence.fitgoodies.references.processors.AbstractCrossReferenceProcessor;
import de.cologneintelligence.fitgoodies.references.processors.EmptyCrossReferenceProcessor;


/**
 * $Id$
 * @author jwierum
 */

public class EmptyCrossReferenceProcessorTest extends FitGoodiesTestCase {
    private AbstractCrossReferenceProcessor processor;

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        processor = new EmptyCrossReferenceProcessor();
    }

    public final void testPattern() {
        String pattern = processor.getPattern();
        pattern = "^\\$\\{" + pattern + "\\}$";

        Pattern regex = Pattern.compile(pattern);
        Matcher m;

        m = regex.matcher("${empty()}");
        assertTrue(m.find());

        m = regex.matcher("${nonEmpty()}");
        assertTrue(m.find());

        m = regex.matcher("${testEmpty()}");
        assertFalse(m.find());
    }

    private void checkCr(final CrossReference cr, final String cmd) {
        assertNull(cr.getParameter());
        assertNull(cr.getNamespace());
        assertEquals(cmd, cr.getCommand());
        assertSame(processor, cr.getProcessor());
    }

    public final void testExtraction() {
        CrossReference cr;

        cr = processor.extractCrossReference("empty()");
        checkCr(cr, "empty");

        cr = processor.extractCrossReference("empty2()");
        assertNull(cr);

        cr = processor.extractCrossReference("nonEmpty()");
        checkCr(cr, "nonEmpty");
    }

    private void checkProcessMatch(final String command, final Object input,
            final String expected) {

        CrossReference cr = new CrossReference(command, null, null, null);
        String actual = null;
        try {
            processor.processMatch(cr, input);
            fail("Expected CrossReferenceProcessorException");
        } catch (CrossReferenceProcessorShortcutException e) {
            assertTrue(e.isOk());
            actual = e.getMessage();
            assertEquals(expected, actual);
        }
    }

    private void checkProcessMatchWithError(final String command, final Object input,
            final String expected) {

        CrossReference cr = new CrossReference(command, null, null, null);
        String actual = null;
        try {
            processor.processMatch(cr, input);
            fail("Expected CrossReferenceProcessorException");
        } catch (CrossReferenceProcessorShortcutException e) {
            assertFalse(e.isOk());
            actual = e.getMessage();
            assertEquals(expected, actual);
        }
    }

    public final void testReplacement() throws CrossReferenceProcessorShortcutException {
        checkProcessMatch("empty", "", "value is empty");
        checkProcessMatch("nonEmpty", "x", "value is non-empty");
        checkProcessMatch("nonEmpty", "y", "value is non-empty");

        checkProcessMatchWithError("empty", "full", "value must be empty or null!");
        checkProcessMatchWithError("empty", "x", "value must be empty or null!");
        checkProcessMatchWithError("nonEmpty", "", "value must not be empty!");
    }

    public final void testWrongMatch() throws CrossReferenceProcessorShortcutException {
        CrossReference cr = new CrossReference("wrongCommand", null, null, null);
        assertNull(processor.processMatch(cr, "nothing"));
    }
}
