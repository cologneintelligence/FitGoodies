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


package fitgoodies;

/**
 * $Id: ScientificDoubleTest.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public final class ScientificDoubleTest extends FitGoodiesTestCase {
	public void testEquals() {
		ScientificDouble two = new ScientificDouble(2.0f);
		ScientificDouble twofour = new ScientificDouble(2.4f);
		ScientificDouble seven = new ScientificDouble(7.0f);

		assertTrue(two.equals(new ScientificDouble(2.0f)));
		assertTrue(seven.equals(new ScientificDouble(7.0f)));
		assertTrue(twofour.equals(new ScientificDouble(2.4f)));

		assertFalse(seven.equals(new ScientificDouble(2.0f)));
		assertFalse(twofour.equals(new ScientificDouble(7.0f)));
		assertFalse(two.equals(new ScientificDouble(2.4f)));

		assertEquals(two.hashCode(), new ScientificDouble(2.0f).hashCode());
		assertEquals(twofour.hashCode(), new ScientificDouble(2.4f).hashCode());
		assertEquals(seven.hashCode(), new ScientificDouble(7.0f).hashCode());

		// This would be great!
		// But how to achieve this while supporting precisions?
		/*
		assertNotEquals(two.hashCode(), seven.hashCode());
		assertNotEquals(twofour.hashCode(), two.hashCode());
		assertNotEquals(seven.hashCode(), two.hashCode());
		*/
	}

	public void testConstructor() {
		assertTrue(new ScientificDouble(2, 1).equals(new ScientificDouble(2.9)));
		assertFalse(new ScientificDouble(2, 0.3).equals(new ScientificDouble(2.9)));

		assertTrue(new ScientificDouble(7, 0.5).equals(new ScientificDouble(7.2)));
		assertFalse(new ScientificDouble(7, 0.3).equals(new ScientificDouble(7.6)));
	}
}
