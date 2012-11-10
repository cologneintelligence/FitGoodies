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

import java.util.regex.Pattern;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.DirectoryProviderMock;
import de.cologneintelligence.fitgoodies.file.FileFixtureHelper;
import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;


/**
 * @author jwierum
 */

public class FileFixtureCrossReferenceProcessorTest extends FitGoodiesTestCase {
    private AbstractCrossReferenceProcessor processor;
    private FileFixtureHelper helper;

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        helper = new FileFixtureHelper();
        processor = new FileFixtureCrossReferenceProcessor(helper);
    }

    public final void testInfo() {
        assertNotNull(processor.info());
    }

    public final void testPattern() {
        final Pattern pattern = Pattern.compile(processor.getPattern());

        assertTrue(pattern.matcher("selectedFile()").find());
        assertTrue(pattern.matcher("selectedEncoding()").find());
        assertFalse(pattern.matcher("selectedXFile()").find());
        assertFalse(pattern.matcher("selectedEncoding(Y)").find());
    }

    public final void testExtraction() {
        CrossReference cr;
        cr = processor.extractCrossReference("selectedFile()");
        assertEquals("selectedFile", cr.getCommand());

        cr = processor.extractCrossReference("selectedEncoding()");
        assertEquals("selectedEncoding", cr.getCommand());

        cr = processor.extractCrossReference("someText");
        assertNull(cr);
    }

    public final void testEncodingReplacement()
            throws CrossReferenceProcessorShortcutException {
        CrossReference cr;
        cr = new CrossReference("selectedEncoding", null, null, processor);

        helper.setEncoding("xy");
        assertEquals("xy", processor.processMatch(cr, "match"));

        helper.setEncoding("latin-1");
        assertEquals("latin-1", processor.processMatch(cr, "match"));
    }

    public final void testFilenameReplacement()
            throws CrossReferenceProcessorShortcutException {
        CrossReference cr;
        cr = new CrossReference("selectedFile", null, null, processor);
        helper.setProvider(new DirectoryProviderMock());
        helper.setPattern(".*\\.txt");

        assertEquals("file1.txt", processor.processMatch(cr, "match"));
    }

    public final void testFinalReplacementException()
            throws CrossReferenceProcessorShortcutException {

        CrossReference cr;
        cr = new CrossReference("selectedFile", null, null, processor);
        helper.setProvider(new DirectoryProviderMock());
        helper.setPattern(".*\\.error");

        try {
            processor.processMatch(cr, "match");
            fail("could read non-existend file");
        } catch (final RuntimeException e) {
            assertTrue(e.getMessage().contains("no file found"));
        }
    }
}
