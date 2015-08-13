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

import de.cologneintelligence.fitgoodies.file.FileFixtureHelper;
import de.cologneintelligence.fitgoodies.file.FileSelector;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.FileNotFoundException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class FileFixtureReferenceProcessorProviderTest extends FitGoodiesTestCase {
	@Mock
	private FileFixtureHelper fileFixtureHelper;
	private FileFixtureReferenceProcessorProvider provider;

	@Before
	public void setUp() throws Exception {
		DependencyManager.inject(FileFixtureHelper.class, fileFixtureHelper);
		provider = new FileFixtureReferenceProcessorProvider();
	}

	@Test
	public void testPattern() {
		assertThat(provider.canProcess("${selectedFile()}"), is(true));
		assertThat(provider.canProcess("${selectedFILE}"), is(true));
		assertThat(provider.canProcess("${selectedEncoding()}"), is(true));
		assertThat(provider.canProcess("${selectedEncoding}"), is(true));
		assertThat(provider.canProcess("selectedXFile()"), is(false));
	}

	@Test
	public void testEncodingReplacement() {
		when(fileFixtureHelper.getEncoding()).thenReturn("UTF-8", "latin-1");

		assertThat(provider.create("${selectedencoding}").preprocess(),
				is(equalTo("UTF-8")));

		assertThat(provider.create("x${selectedEncoding()}y").preprocess(),
				is(equalTo("xlatin-1y")));
	}

	@Test
	public void testFilenameReplacement() throws FileNotFoundException {
		FileSelector selector = mock(FileSelector.class);
		File file1 = new File("myFile");
		File file2 = new File("another file.txt");
		when(fileFixtureHelper.getSelector()).thenReturn(selector);
		when(selector.getFirstFile()).thenReturn(file1, file2);

		assertThat(provider.create("${selectedfile}").preprocess(),
				is(equalTo("myFile")));

		assertThat(provider.create("x${selectedFILE()}y").preprocess(),
				is(equalTo("xanother file.txty")));
	}

	@Test
	public void testFilenameReplacementWithError() throws FileNotFoundException {
		FileSelector selector = mock(FileSelector.class);
		when(fileFixtureHelper.getSelector()).thenReturn(selector);
		when(selector.getFirstFile()).thenThrow(new FileNotFoundException(""));

		assertThat(provider.create("${selectedfile}").preprocess(),
				allOf(containsString("error"), containsString("no file selected")));
	}
}
