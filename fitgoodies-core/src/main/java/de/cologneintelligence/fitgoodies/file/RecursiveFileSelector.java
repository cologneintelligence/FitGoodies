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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Browses a directory recursively for files which match a given filter.
 */
public class RecursiveFileSelector implements Iterator<File> {
	private final List<File> dirs = new LinkedList<>();
	private final String pattern;
	private File[] files;
	private int fileIndex = 0;

	/**
	 * Creates a new iterator.
	 *
	 * @param directory       directory to browse
	 * @param filenamePattern filter to use. Must be a regular expression.
	 */
	public RecursiveFileSelector(final File directory, final String filenamePattern) {
		this.pattern = filenamePattern;
		dirs.add(directory);
		cacheNext();
	}

	private boolean cacheNext() {
		fileIndex = 0;
		do {
			if (dirs.size() > 0) {
				File dir = dirs.remove(0);

				final File[] children = dir.listFiles();
				if (children != null) {
					for (File child : children) {
						if (child.isDirectory()) {
							dirs.add(child);
						}
					}
				}

				files = dir.listFiles(new SimpleRegexFilter(pattern));
			} else {
				return false;
			}
		} while (files == null || files.length == 0);

		return true;
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
		} else if (fileIndex < files.length) {
			return true;
		} else {
			return cacheNext();
		}
	}

	/**
	 * Returns the next matching file.
	 *
	 * @return the next element in the iteration.
	 * @throws NoSuchElementException iteration has no more elements
	 */
	@Override
	public final File next() {
		if (files == null || fileIndex >= files.length) {
			if (!cacheNext()) {
				throw new NoSuchElementException();
			}
		}
		return files[fileIndex++];
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
