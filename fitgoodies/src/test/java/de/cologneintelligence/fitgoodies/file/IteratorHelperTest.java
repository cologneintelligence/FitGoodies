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

import java.util.ArrayList;
import java.util.Iterator;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.IteratorHelper;


/**
 *
 * @author jwierum
 */
public class IteratorHelperTest extends FitGoodiesTestCase {
	public final void testIterator() {
		ArrayList<Object> al = new ArrayList<Object>();
		Iterator<Object> iterator = al.iterator();
		Iterable<Object> iterable = new IteratorHelper<Object>(iterator);

		assertSame(iterator, iterable.iterator());
	}
}
