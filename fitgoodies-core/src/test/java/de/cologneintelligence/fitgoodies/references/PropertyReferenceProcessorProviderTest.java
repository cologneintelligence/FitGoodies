/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
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

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class PropertyReferenceProcessorProviderTest extends FitGoodiesTestCase {
	private PropertyReferenceProcessorProvider provider;

	@Before
	public void setUp() throws Exception {
		provider = new PropertyReferenceProcessorProvider();
	}

	@Test
	public void testPattern() {
		assertThat(provider.canProcess("${file.getValue(one)}"), is(true));
		assertThat(provider.canProcess("${myfile.getValue(user.name)}"), is(true));
		assertThat(provider.canProcess("pre${myfile.getValue(user.name)}post"), is(true));
		assertThat(provider.canProcess("${getValue(one)}"), is(false));
		assertThat(provider.canProcess("${.getValue(one)}"), is(false));
		assertThat(provider.canProcess("${file.getValue()}"), is(false));
	}

	@Test
	public void testProcessing() throws Exception {
		InputStream inputStream = new ByteArrayInputStream("fit.propertyName=propertyValue\n".getBytes());
		ResourceBundle mockResourceBundle = new PropertyResourceBundle(inputStream);

		CellProcessor processor = provider.create("${test-suite.getValue(fit.propertyName)}");
		provider.setResourceBundle("test-suite", mockResourceBundle);
		assertThat(processor.preprocess(), is(equalTo("propertyValue")));

		processor = provider.create("abc${test-suite.getValue(fit.propertyName)}123");
		provider.setResourceBundle("test-suite", mockResourceBundle);
		assertThat(processor.preprocess(), is(equalTo("abcpropertyValue123")));
	}

	@Test
	public void testMissingFile() throws Exception {
		CellProcessor processor = provider.create("${test-suite.getValue(fit.propertyName)}");
		assertThat(processor.preprocess(),
				allOf(containsString("error:"), Matchers.containsString("test-suite"),
						Matchers.containsString("missing")));
	}

	@Test
	public void testNamespaces() throws Exception {
		InputStream inputStream1 = new ByteArrayInputStream("fit.propertyName=result\n".getBytes());
		InputStream inputStream2 = new ByteArrayInputStream("fit.propertyName=other\n".getBytes());
		ResourceBundle mockResourceBundle1 = new PropertyResourceBundle(inputStream1);
		ResourceBundle mockResourceBundle2 = new PropertyResourceBundle(inputStream2);
		provider.setResourceBundle("test-1648", mockResourceBundle1);
		provider.setResourceBundle("test-suite", mockResourceBundle2);
		final CellProcessor processor = provider.create("${test-1648.getValue(fit.propertyName)}");
		assertThat(processor.preprocess(), is(equalTo("result")));
	}
}
