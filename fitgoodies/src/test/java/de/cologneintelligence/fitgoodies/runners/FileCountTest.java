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


package de.cologneintelligence.fitgoodies.runners;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.runners.FileCount;
import fit.Counts;

/**
 * $Id$
 * @author jwierum
 */
public class FileCountTest extends FitGoodiesTestCase {
	public final void testGetters() {
		Counts expectedCounts = new Counts();
		String expectedFile = "x";
		FileCount actual = new FileCount(expectedFile, expectedCounts);
		assertEquals(expectedFile, actual.getFile());
		assertSame(expectedCounts, actual.getCounts());

		expectedCounts = new Counts();
		expectedFile = "y";
		actual = new FileCount(expectedFile, expectedCounts);
		assertEquals(expectedFile, actual.getFile());
		assertSame(expectedCounts, actual.getCounts());
	}

	public final void testEquals() {
		Counts counts = new Counts();
		FileCount fc1 = new FileCount("asdf", counts);
		FileCount fc2 = new FileCount("fdsa", counts);

		assertFalse(fc1.equals(fc2));

		fc1 = new FileCount("asdf", counts);
		fc2 = new FileCount("asdf", counts);
		assertTrue(fc1.equals(fc2));

		fc1 = new FileCount("x", counts);
		fc2 = new FileCount("y", counts);
		assertFalse(fc1.equals(fc2));

		fc1 = new FileCount("a", counts);
		fc2 = new FileCount("a", counts);
		assertTrue(fc1.equals(fc2));

		fc1 = new FileCount("a", counts);
		assertFalse(fc1.equals("a"));
	}

	public final void testHash() {
		Counts counts = new Counts();
		FileCount fc1 = new FileCount("asdf", counts);
		FileCount fc2 = new FileCount("fdsa", counts);

		assertTrue(fc1.hashCode() != fc2.hashCode());

		fc1 = new FileCount("asdf", counts);
		fc2 = new FileCount("asdf", counts);
		assertTrue(fc1.hashCode() == fc2.hashCode());

		fc1 = new FileCount("x", counts);
		fc2 = new FileCount("y", counts);
		assertTrue(fc1.hashCode() != fc2.hashCode());

		fc1 = new FileCount("a", counts);
		fc2 = new FileCount("a", counts);
		assertTrue(fc1.hashCode() == fc2.hashCode());
	}
}
