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

import java.io.File;
import java.io.FileNotFoundException;

/**
 * This class takes a filter pattern and a File and returns
 * matching files.
 */
public class FileSelector {
	private final SimpleRegexFilter filter;
	private final File directory;

	/**
	 * Initializes a new selector.
	 *
	 * @param directory directory to use
	 * @param pattern   filename filter pattern
	 */
	public FileSelector(final File directory,
	                    final String pattern) {
		filter = new SimpleRegexFilter(pattern);
		this.directory = directory;
	}

	/**
	 * Returns the only file that matches the pattern. If no file matches, a
	 * <code>null</code>. If more than one file matches, a
	 * {@link FilenameNotUniqueException} is thrown.
	 *
	 * @return the matching file or <code>null</code> if no file matched
	 * @throws FilenameNotUniqueException more than one file matched the pattern
	 * @throws FileNotFoundException      thrown, if the directory does not exist.
	 */
	public File getUniqueFile()
			throws FilenameNotUniqueException, FileNotFoundException {
		File[] files = directory.listFiles(filter);

		if (files == null || files.length == 0) {
			throw new FileNotFoundException();
		}

		if (files.length > 1) {
			throw new FilenameNotUniqueException(filter.getPattern());
		}
		return files[0];
	}

	/**
	 * Returns the first matching file.
	 *
	 * @return the matching file or <code>null</code> if no file matches
	 * @throws FileNotFoundException thrown, if the directory does not exist
	 */
	public File getFirstFile() throws FileNotFoundException {
		File[] files = directory.listFiles(filter);

		if (files == null || files.length == 0) {
			throw new FileNotFoundException();
		}

		return files[0];
	}

	/**
	 * Returns a list of all matching files.
	 *
	 * @return all files which match the pattern
	 */
	public File[] getFiles() {
		final File[] files = directory.listFiles(filter);
		if (files == null) {
			return new File[0];
		} else {
			return files;
		}
	}

	/**
	 * Returns the last matching file.
	 *
	 * @return the matching file or <code>null</code> if no file matches
	 * @throws FileNotFoundException thrown, if the directory does not exist
	 */
	public File getLastFile() throws FileNotFoundException {
		File[] files = directory.listFiles(filter);
		if (files == null || files.length == 0) {
			throw new FileNotFoundException();
		}

		return files[files.length - 1];
	}
}
