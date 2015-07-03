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


package de.cologneintelligence.fitgoodies.references.processors;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;


public class EmptyCrossReferenceProcessorTest extends FitGoodiesTestCase {
    private AbstractCrossReferenceProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new EmptyCrossReferenceProcessor();
    }

    @Test
    public void testPattern() {
        String pattern = processor.getPattern();
        pattern = "^\\$\\{" + pattern + "\\}$";

        Pattern regex = Pattern.compile(pattern);
        Matcher m;

        m = regex.matcher("${empty()}");
        assertThat(m.find(), is(true));

        m = regex.matcher("${nonEmpty()}");
        assertThat(m.find(), is(true));

        m = regex.matcher("${testEmpty()}");
        assertThat(m.find(), is(false));

    }

    private void checkCr(final CrossReference cr, final String cmd) {
        assertThat(cr.getParameter(), is(nullValue()));
        assertThat(cr.getNamespace(), is(nullValue()));
        assertThat(cr.getCommand(), is(equalTo(cmd)));
        assertThat(cr.getProcessor(), is(sameInstance(processor)));
    }

    @Test
    public void testExtraction() {
        CrossReference cr;

        cr = processor.extractCrossReference("empty()");
        checkCr(cr, "empty");

        cr = processor.extractCrossReference("empty2()");
        assertThat(cr, is(nullValue()));

        cr = processor.extractCrossReference("nonEmpty()");
        checkCr(cr, "nonEmpty");
    }

    private void checkProcessMatch(final String command, final Object input,
            final String expected) {

        CrossReference cr = new CrossReference(command, null, null, null);
        String actual;
        try {
            processor.processMatch(cr, input);
            Assert.fail("Expected CrossReferenceProcessorException");
        } catch (CrossReferenceProcessorShortcutException e) {
            assertThat(e.isOk(), is(true));
            actual = e.getMessage();
            assertThat(actual, is(equalTo(expected)));
        }
    }

    private void checkProcessMatchWithError(final String command, final Object input,
            final String expected) {

        CrossReference cr = new CrossReference(command, null, null, null);
        String actual;
        try {
            processor.processMatch(cr, input);
            Assert.fail("Expected CrossReferenceProcessorException");
        } catch (CrossReferenceProcessorShortcutException e) {
            assertThat(e.isOk(), is(false));

            actual = e.getMessage();
            assertThat(actual, is(equalTo(expected)));
        }
    }

    @Test
    public void testReplacement() throws CrossReferenceProcessorShortcutException {
        checkProcessMatch("empty", "", "value is empty");
        checkProcessMatch("nonEmpty", "x", "value is non-empty");
        checkProcessMatch("nonEmpty", "y", "value is non-empty");

        checkProcessMatchWithError("empty", "full", "value must be empty or null!");
        checkProcessMatchWithError("empty", "x", "value must be empty or null!");
        checkProcessMatchWithError("nonEmpty", "", "value must not be empty!");
    }

    @Test
    public void testWrongMatch() throws CrossReferenceProcessorShortcutException {
        CrossReference cr = new CrossReference("wrongCommand", null, null, null);
        assertThat(processor.processMatch(cr, "nothing"), is(nullValue()));
    }
}
