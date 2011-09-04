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

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Browses a directory recursively for files which match a given filter.
 *
 * @author jwierum
 * @version $Id$
 */
public class RecursiveFileSelector implements Iterator<FileInformation> {
	private final List<DirectoryProvider> dirs = new LinkedList<DirectoryProvider>();
	private FilterDirectoryIterator files;
	private final String pattern;

	/**
	 * Creates a new iterator.
	 * @param directoryProvider directory to browse
	 * @param filenamePattern filter to use. Must be a regular expression.
	 */
	public RecursiveFileSelector(
			final DirectoryProvider directoryProvider,
			final String filenamePattern) {
		this.pattern = filenamePattern;
		dirs.add(directoryProvider);
		prepareList();
	}

	private boolean prepareList() {
		if (dirs.size() == 0) {
			return false;
		}

		while ((files == null || !files.hasNext()) && dirs.size() > 0) {
			DirectoryProvider dir = dirs.remove(0);
			try {
				for (DirectoryProvider dirprov
						: new IteratorHelper<DirectoryProvider>(dir.getDirectories())) {
					dirs.add(dirprov);
				}
			} catch (FileNotFoundException e) {
			}

			try {
				files = new FilterDirectoryIterator(dir.getFiles(),
						new SimpleRegexFilter(pattern));
			} catch (FileNotFoundException e) {
			}
		}

		if (files == null) {
			return false;
		} else {
			return files.hasNext();
		}
	}

	/**
	 * Returns <code>true</code> if the iteration has more elements.
	 * (In other words, returns <code>true</code> if <code>next</code> would
	 * return an element rather than throwing an exception.)
	 *
	 * @return true if the iterator has more elements.
	 */
	@Override
	public final boolean hasNext() {
		if (files == null) {
			return false;
		} else if (files.hasNext()) {
			return true;
		} else {
			return prepareList();
		}
	}

	/**
	 * Returns the next matching file.
	 * @return the next element in the iteration.
     * @throws NoSuchElementException iteration has no more elements
	 */
	@Override
	public final FileInformation next() {
		if (files.hasNext()) {
			return files.next();
		} else {
			prepareList();
			return files.next();
		}
	}

	/**
	 * Not implemented.
	 */
	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
	}
}
