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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fitgoodies.FitGoodiesTestCase;
import fitgoodies.references.CrossReference;

/**
 * $Id: PropertyCrossReferenceProcessorTest.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */

public class PropertyCrossReferenceProcessorTest extends FitGoodiesTestCase {
	private PropertyCrossReferenceProcessor processor;

	@Override
	public final void setUp() throws Exception {
		super.setUp();
		processor = new PropertyCrossReferenceProcessor();
	}

	public final void testPattern() {
		String pattern = processor.getPattern();
		pattern = "^\\$\\{" + pattern + "\\}$";

		Pattern regex = Pattern.compile(pattern);
		Matcher m;

		m = regex.matcher("${ns.getValue(one)}");
		assertTrue(m.find());
	}

	public final void testExtraction() {
	    CrossReference cr =
	    	processor.extractCrossReference("test-suite.getValue(fit.propertyName)");

        assertNotNull(cr);
        assertEquals("getValue", cr.getCommand());
        assertEquals("test-suite", cr.getNamespace());
        assertEquals("fit.propertyName", cr.getParameter());
        assertSame(processor, cr.getProcessor());
	}

	public final void testProcessingIgnoringNamespace() throws Exception {
	    CrossReference cr =
	    	processor.extractCrossReference("test-suite.getValue(fit.propertyName)");
	    InputStream inputStream =
	    	new ByteArrayInputStream("fit.propertyName=propertyValue\n".getBytes());
	    ResourceBundle mockResourceBundle =	new PropertyResourceBundle(inputStream);
	    processor.setResourceBundle("test-suite", mockResourceBundle);
		assertEquals("propertyValue", processor.processMatch(cr, null));
	}

    public final void testProcessingWithFilename() throws Exception {
        CrossReference cr1 =
        	processor.extractCrossReference("test-suite.getValue(fit.propertyName)");
        try {
            processor.processMatch(cr1, null);
            fail("should have thrown some exception, because the resource "
            		+ "test-suite.properties can not be found");
        } catch (MissingResourceException e) {
            assertTrue("test-suite should be missing", e.getMessage().contains("test-suite"));
        }
        InputStream inputStream =
        	new ByteArrayInputStream("fit.propertyName=propertyValue\n".getBytes());
        ResourceBundle mockResourceBundle = new PropertyResourceBundle(inputStream);
        processor.setResourceBundle("test-suite", mockResourceBundle);
        CrossReference cr2 =
        	processor.extractCrossReference("test-1648.getValue(fit.propertyName)");
        try {
            processor.processMatch(cr2, null);
            fail("should have thrown some exception, because the resource"
            		+ "test-1648.properties can not be found");
        } catch (MissingResourceException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("test-1648"));
        }
    }

    public final void testInfo() {
    	assertNotNull(processor.info());
    }
}
