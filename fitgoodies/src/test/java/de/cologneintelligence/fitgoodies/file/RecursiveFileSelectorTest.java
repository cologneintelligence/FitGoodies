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


package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RecursiveFileSelectorTest extends FitGoodiesTestCase {
	@Test
	public void testIterator() throws FileNotFoundException {
		final String pattern = ".*2.*";
		File dirMock = mockDirectory(pattern,
				"d/file2.txt",
				"d/s1/file2.bat",
				"d/s1/s2/src2.java");
		RecursiveFileSelector selector = new RecursiveFileSelector(dirMock, pattern);

		assertThat(selector.next().toString(), is(equalTo("d/file2.txt")));
		assertThat(selector.next().toString(), is(equalTo("d/s1/file2.bat")));
		assertThat(selector.next().toString(), is(equalTo("d/s1/s2/src2.java")));
		assertThat(selector.hasNext(), is(false));
	}

	@Test
	public void testIterator2() throws FileNotFoundException {
		final String pattern = ".*\\.java";
		File dirMock = mockDirectory(pattern,
				"d/src3.java",
				"d/s1/s2/src1.java",
				"d/s1/s2/src2.java",
				"d/a/src3.java");

		RecursiveFileSelector selector = new RecursiveFileSelector(dirMock, pattern);

		assertThat(selector.next().toString(), is(equalTo("d/src3.java")));
		assertThat(selector.next().toString(), is(equalTo("d/a/src3.java")));
		assertThat(selector.next().toString(), is(equalTo("d/s1/s2/src1.java")));
		assertThat(selector.next().toString(), is(equalTo("d/s1/s2/src2.java")));
		assertThat(selector.hasNext(), is(false));

	}

	@Test(expected = NoSuchElementException.class)
	public void testErrorHandling1() {
		File dirMock = mock(File.class);
		when(dirMock.listFiles(argThat(any(FilenameFilter.class)))).thenReturn(null);
		RecursiveFileSelector selector = new RecursiveFileSelector(dirMock, "");
		selector.next();
	}

	@Test(expected = NoSuchElementException.class)
	public void testErrorHandling2() {
		File dirMock = mock(File.class);
		when(dirMock.listFiles(argThat(any(FilenameFilter.class)))).thenReturn(new File[0]);
		RecursiveFileSelector selector = new RecursiveFileSelector(dirMock, "");
		selector.next();
	}
}
