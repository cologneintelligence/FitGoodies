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


package de.cologneintelligence.fitgoodies.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FilterDirectoryIterator;


/**
 *
 * @author jwierum
 */
public class FilterDirectoryIteratorTest extends FitGoodiesTestCase {
	private Iterator<FileInformation> dummy;

	@Override
	public final void setUp() throws Exception {
		super.setUp();
		dummy = (new DirectoryProviderMock()).getFiles();
	}

	public final void testBlankFilter() {
		FilterDirectoryIterator files;
		files = new FilterDirectoryIterator(dummy, new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return true;
			}
		});
		assertStringIterator(files, new String[] {
				"/test/file1.txt",
				"/test/file2.txt",
				"/test/file3.txt",
				"/f.txt.bat",
				"/dir/dir2/noext"});
	}

	public final void testFilenameNameFilter() {
		FilterDirectoryIterator files;
		files = new FilterDirectoryIterator(dummy, new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return name.matches("file.*|.*\\.bat");
			}
		});
		assertStringIterator(files, new String[] {
				"/test/file1.txt",
				"/test/file2.txt",
				"/test/file3.txt",
				"/f.txt.bat"});
	}

	public final void testFilenameDirFilter() {
		FilterDirectoryIterator files;
		files = new FilterDirectoryIterator(dummy, new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return dir.getAbsolutePath().indexOf("test") >= 0;
			}
		});
		assertStringIterator(files, new String[] {
				"/test/file1.txt",
				"/test/file2.txt",
				"/test/file3.txt"});
	}
}
