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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;


public class PropertyCrossReferenceProcessorTest extends FitGoodiesTestCase {
	private PropertyCrossReferenceProcessor processor;

	@Before
	public void setUp() throws Exception {
		processor = new PropertyCrossReferenceProcessor();
	}

	@Test
	public void testPattern() {
		String pattern = processor.getPattern();
		pattern = "^\\$\\{" + pattern + "\\}$";

		Pattern regex = Pattern.compile(pattern);
		Matcher m;

		m = regex.matcher("${ns.getValue(one)}");
		assertThat(m.find(), is(true));
	}

	@Test
	public void testExtraction() {
	    CrossReference cr =
	    	processor.extractCrossReference("test-suite.getValue(fit.propertyName)");

		assertThat(cr, not(is(nullValue())));
		assertThat(cr.getCommand(), is(equalTo("getValue")));
		assertThat(cr.getNamespace(), is(equalTo("test-suite")));
		assertThat(cr.getParameter(), is(equalTo("fit.propertyName")));
		assertThat(cr.getProcessor(), (org.hamcrest.Matcher)is(sameInstance(processor)));
	}

	@Test
	public void testProcessingIgnoringNamespace() throws Exception {
	    CrossReference cr =
	    	processor.extractCrossReference("test-suite.getValue(fit.propertyName)");
	    InputStream inputStream =
	    	new ByteArrayInputStream("fit.propertyName=propertyValue\n".getBytes());
	    ResourceBundle mockResourceBundle =	new PropertyResourceBundle(inputStream);
	    processor.setResourceBundle("test-suite", mockResourceBundle);
		assertThat(processor.processMatch(cr, null), is(equalTo("propertyValue")));
	}

    @Test
	public void testProcessingWithFilename() throws Exception {
        CrossReference cr1 =
        	processor.extractCrossReference("test-suite.getValue(fit.propertyName)");
        try {
            processor.processMatch(cr1, null);
			Assert.fail("should have thrown some exception, because the resource "
                        + "test-suite.properties can not be found");
		} catch (MissingResourceException e) {
			assertThat(e.getMessage().contains("test-suite"), is(true));
		}
        InputStream inputStream =
        	new ByteArrayInputStream("fit.propertyName=propertyValue\n".getBytes());
        ResourceBundle mockResourceBundle = new PropertyResourceBundle(inputStream);
        processor.setResourceBundle("test-suite", mockResourceBundle);
        CrossReference cr2 =
        	processor.extractCrossReference("test-1648.getValue(fit.propertyName)");
        try {
            processor.processMatch(cr2, null);
			Assert.fail("should have thrown some exception, because the resource"
					+ "test-1648.properties can not be found");
		} catch (MissingResourceException e) {
			assertThat(e.getMessage().contains("test-1648"), is(true));
		}
    }

    @Test
	public void testInfo() {
		assertThat(processor.info(), not(CoreMatchers.is(nullValue())));
	}
}
