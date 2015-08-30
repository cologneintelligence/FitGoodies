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


package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;


public class FileSelectorTest extends FitGoodiesTestCase {

	@Test
	public void testFindFiles() throws Exception {
		File directory = mockDirectory("file1\\.txt", "file1.txt");
		FileSelector fs = new FileSelector(directory, "file1\\.txt");
		assertThat(fs.getUniqueFile().getName(), is(equalTo("file1.txt")));
	}

	@Test
	public void testFindFilesRegex() throws Exception {
		File directory = mockDirectory("file[26]\\.txt", "file2.txt");
		FileSelector fs = new FileSelector(directory, "file[26]\\.txt");

		assertThat(fs.getUniqueFile().getName(), is(equalTo("file2.txt")));
	}

	@Test(expected = FilenameNotUniqueException.class)
	public void testUniqueNameErrorHandling1() throws Exception {
		File directory = mockDirectory("file[23]\\.txt",
				"file2.txt",
				"file3.txt");
		FileSelector fs = new FileSelector(directory, "file[23]\\.txt");
		fs.getUniqueFile();
	}

	@Test(expected = FilenameNotUniqueException.class)
	public void testUniqueNameErrorHandling2() throws Exception {
		File directory = mockDirectory(".*", "a", "b");
		FileSelector fs = new FileSelector(directory, ".*");
		fs.getUniqueFile();
	}

	@Test(expected = FileNotFoundException.class)
	public void testNotFoundErrorHandling1() throws Exception {
		File directory = mockDirectory("xyz");
		FileSelector fs = new FileSelector(directory, "xyz");
		fs.getFirstFile();
	}

	@Test(expected = FileNotFoundException.class)
	public void testNotFoundErrorHandling2() throws Exception {
		File directory = mockDirectory("xyz");
		FileSelector fs = new FileSelector(directory, "xyz");
		fs.getUniqueFile();
	}

	@Test
	public void testNotFoundErrorHandling3() throws Exception {
		File directory = mockDirectory("");
		when(directory.listFiles(argThat(any(FilenameFilter.class)))).thenReturn(null);
		FileSelector fs = new FileSelector(directory, "");

		assertThat(fs.getFiles().length, is(0));
	}


	@Test
	public void testGetLastFile() throws FileNotFoundException {
		File directory = mockDirectory(".*",
				"file1.txt",
				"file2.txt",
				"noext",
				"subdir/ignored");
		FileFixtureHelper helper = DependencyManager.getOrCreate(FileFixtureHelper.class);
		helper.setEncoding("utf-8");
		helper.setPattern(".*");
		helper.setDirectory(directory);

		FileSelector fs = helper.getSelector();
		assertThat(fs.getLastFile().getName(), is(equalTo("noext")));
	}

	@Test(expected = FileNotFoundException.class)
	public void testGetLastFileWithErrors() throws FileNotFoundException {
		File directory = mockDirectory("nofile");
		FileFixtureHelper helper = DependencyManager.getOrCreate(FileFixtureHelper.class);
		helper.setEncoding("utf-8");
		helper.setPattern("nofile");
		helper.setDirectory(directory);

		FileSelector fs = helper.getSelector();
		fs.getLastFile();
	}

	@Test
	public void testFirstFile1() throws FileNotFoundException {
		File directory = mockDirectory(".*",
				"file1.txt",
				"file2.txt");
		FileSelector fs = new FileSelector(directory, ".*");
		assertThat(fs.getFirstFile().getName(), is(equalTo("file1.txt")));
		assertThat(fs.getFirstFile().getName(), is(equalTo("file1.txt")));
	}

	@Test
	public void testFirstFile2() throws FileNotFoundException {
		File directory = mockDirectory(".*\\.bat",
				"f.txt.bat",
				"f2.txt.bat");
		FileSelector fs = new FileSelector(directory, ".*\\.bat");
		assertThat(fs.getFirstFile().getName(), is(equalTo("f.txt.bat")));
		assertThat(fs.getFirstFile().getName(), is(equalTo("f.txt.bat")));
	}

	@Test
	public void testFiles() throws FileNotFoundException {
		final String pattern = ".*\\.txt";

		File directory = mockDirectory(pattern,
				"file1.txt",
				"file2.txt",
				"file3.txt");

		FileSelector fs = new FileSelector(directory, pattern);
		File[] f = fs.getFiles();

		assertThat(f.length, is(equalTo(3)));
		assertThat(f[0].getName(), is(equalTo("file1.txt")));
		assertThat(f[2].getName(), is(equalTo("file3.txt")));
	}
}
