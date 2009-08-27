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


package fitgoodies.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <code>Iterator</code> that takes another iterator and filters out all files
 * which do not match a given <code>FilenameFilter</code>.
 *
 * @author jwierum
 * @version $Id: FilterDirectoryIterator.java 185 2009-08-17 13:47:24Z jwierum $
 */
public class FilterDirectoryIterator implements Iterator<FileInformation> {
	private final Iterator<FileInformation> provider;
	private final FilenameFilter filter;

	private FileInformation next;
	private boolean nextSelected;

	/**
	 * Constructs a new iterator.
	 * @param fileInformationIterator underlying iterator to filter
	 * @param filenameFilter filter to apply to all files
	 */
	public FilterDirectoryIterator(
			final Iterator<FileInformation> fileInformationIterator,
			final FilenameFilter filenameFilter) {
		this.provider = fileInformationIterator;
		this.filter = filenameFilter;

		nextSelected = false;
	}

	private boolean selectNext() {
		if (nextSelected) {
			return true;
		}

		while (provider.hasNext()) {
			next = provider.next();

			if (filter.accept(new File(next.pathname()), next.filename())) {
				nextSelected = true;
				return true;
			}
		}

		return false;
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
		return selectNext();
	}

	/**
	 * Returns the next matching file.
	 * @return the next element in the iteration.
     * @throws NoSuchElementException iteration has no more elements
	 */
	@Override
	public final FileInformation next() {
		if (selectNext()) {
			nextSelected = false;
			return next;
		} else {
			throw new NoSuchElementException();
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
