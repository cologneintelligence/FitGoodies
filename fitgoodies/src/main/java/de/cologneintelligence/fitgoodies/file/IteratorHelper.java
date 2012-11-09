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

/**
 * Wrapper class which encapsulates an <code>Iterator</code> so that it can be
 * used as an <code>Iterable</code>.
 *
 * @param <T> type of elements returned by the iterator
 *
 * @version $Id$
 * @author jwierum
 */
public class IteratorHelper<T> implements Iterable<T> {
	private final Iterator<T> it;

	/**
	 * Constructs a new wrapper.
	 * @param iterator iterator to encapsulate
	 */
	public IteratorHelper(final Iterator<T> iterator) {
		this.it = iterator;
	}

	/**
	 * Returns the encapsulated iterator.
	 * @return the encapsulated iterator
	 */
	@Override
	public final Iterator<T> iterator() {
		return it;
	}
}
