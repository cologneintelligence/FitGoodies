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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Converts a list of {@link FileInformation}.
 *
 * @author jwierum
 * @version $Id$
 */
public class FileIterator implements Iterator<FileInformation> {
	private final FileInformation[] files;
	private int pos;

	/**
	 * Creates a new iterator.
	 * @param fileInformation source array
	 */
	public FileIterator(final FileInformation[] fileInformation) {
		this.files = fileInformation;
		pos = -1;
	}

	/**
	 * Returns <code>true</code> if the iteration has more elements.
	 * (In other words, returns <code>true</code> if <code>next</code> would
	 * return an element rather than throwing an exception.)
	 *
	 * @return true if the iterator has more elements.
	 */
	public final boolean hasNext() {
		return pos < files.length - 1;
	}

	/**
	 * Returns the next file.
	 * @return the next element in the iteration.
     * @throws NoSuchElementException iteration has no more elements
	 */
	@Override
	public final FileInformation next() {
		return files[++pos];
	}

	/**
	 * Not implemented.
	 */
	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
	}

}
