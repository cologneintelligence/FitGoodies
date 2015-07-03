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
import de.cologneintelligence.fitgoodies.file.FileFixtureHelper;
import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;




public class FileFixtureCrossReferenceProcessorTest extends FitGoodiesTestCase {
    private AbstractCrossReferenceProcessor processor;
    private FileFixtureHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new FileFixtureHelper();
        processor = new FileFixtureCrossReferenceProcessor(helper);
    }

    @Test
    public void testInfo() {
        assertThat(processor.info(), not(CoreMatchers.is(nullValue())));
    }

    @Test
    public void testPattern() {
        final Pattern pattern = Pattern.compile(processor.getPattern());

        assertThat(pattern.matcher("selectedFile()").find(), is(true));
        assertThat(pattern.matcher("selectedEncoding()").find(), is(true));
        assertThat(pattern.matcher("selectedXFile()").find(), is(false));

        assertThat(pattern.matcher("selectedEncoding(Y)").find(), is(false));

    }

    @Test
    public void testExtraction() {
        CrossReference cr;
        cr = processor.extractCrossReference("selectedFile()");
        assertThat(cr.getCommand(), is(equalTo("selectedFile")));

        cr = processor.extractCrossReference("selectedEncoding()");
        assertThat(cr.getCommand(), is(equalTo("selectedEncoding")));

        cr = processor.extractCrossReference("someText");
        assertThat(cr, is(nullValue()));
    }

    @Test
    public void testEncodingReplacement()
            throws CrossReferenceProcessorShortcutException {
        CrossReference cr;
        cr = new CrossReference("selectedEncoding", null, null, processor);

        helper.setEncoding("xy");
        assertThat(processor.processMatch(cr, "match"), is(equalTo("xy")));

        helper.setEncoding("latin-1");
        assertThat(processor.processMatch(cr, "match"), is(equalTo("latin-1")));
    }

    @Test
    public void testFilenameReplacement()
            throws CrossReferenceProcessorShortcutException {
        CrossReference cr = new CrossReference("selectedFile", null, null, processor);
        String pattern = ".*\\.txt";

        helper.setPattern(pattern);
        helper.setDirectory(mockDirectory(pattern, "file1.txt"));

        assertThat(processor.processMatch(cr, "match"), is(equalTo("file1.txt")));
    }

    @Test
    public void testFinalReplacementException()
            throws CrossReferenceProcessorShortcutException {

        CrossReference cr;
        cr = new CrossReference("selectedFile", null, null, processor);
        helper.setDirectory(mockDirectory(".*\\.error"));
        helper.setPattern(".*\\.error");

        try {
            processor.processMatch(cr, "match");
            Assert.fail("could read non-existent file");
        } catch (final RuntimeException e) {
            assertThat(e.getMessage().contains("no file found"), is(true));
        }
    }
}
