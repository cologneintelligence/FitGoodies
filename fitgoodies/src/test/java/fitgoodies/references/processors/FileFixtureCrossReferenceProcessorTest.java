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


package fitgoodies.references.processors;

import java.util.regex.Pattern;

import fitgoodies.FitGoodiesTestCase;
import fitgoodies.file.DirectoryProviderMock;
import fitgoodies.file.FileFixtureHelper;
import fitgoodies.references.CrossReference;
import fitgoodies.references.CrossReferenceProcessorShortcutException;

/**
 * $Id$
 * @author jwierum
 */

public class FileFixtureCrossReferenceProcessorTest extends FitGoodiesTestCase {
	private AbstractCrossReferenceProcessor processor;

	@Override
	public final void setUp() throws Exception {
		super.setUp();
		processor = new FileFixtureCrossReferenceProcessor();
	}

	public final void testInfo() {
		assertNotNull(processor.info());
	}

	public final void testPattern() {
		Pattern pattern = Pattern.compile(processor.getPattern());

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

		FileFixtureHelper.instance().setEncoding("xy");
		assertEquals("xy", processor.processMatch(cr, "match"));

		FileFixtureHelper.instance().setEncoding("latin-1");
		assertEquals("latin-1", processor.processMatch(cr, "match"));
	}

	public final void testFilenameReplacement()
			throws CrossReferenceProcessorShortcutException {
		CrossReference cr;
		cr = new CrossReference("selectedFile", null, null, processor);
		FileFixtureHelper.instance().setProvider(new DirectoryProviderMock());
		FileFixtureHelper.instance().setPattern(".*\\.txt");

		assertEquals("file1.txt", processor.processMatch(cr, "match"));
	}

	public final void testFinalReplacementException()
			throws CrossReferenceProcessorShortcutException {

		CrossReference cr;
		cr = new CrossReference("selectedFile", null, null, processor);
		FileFixtureHelper.instance().setProvider(new DirectoryProviderMock());
		FileFixtureHelper.instance().setPattern(".*\\.error");

		try {
			processor.processMatch(cr, "match");
			fail("could read non-existend file");
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("no file found"));
		}
	}
}
