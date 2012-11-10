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


package de.cologneintelligence.fitgoodies.references.processors;
import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.processors.NamespaceHashMap;

/**
 *
 * @author jwierum
 */

public class NamespaceHashMapTest extends FitGoodiesTestCase {
	private final NamespaceHashMap<String> hm = new NamespaceHashMap<String>();

	@Override
	public final void setUp() throws Exception {
		super.setUp();

		hm.put("ns1", "key1", "val1");
		hm.put("ns1", "key2", "val2");
		hm.put("ns1", "key1", "val3");

		hm.put("ns2", "key1", "val7");
	}

	public final void testSize() {
		assertEquals(3, hm.size());

		hm.put("ns1", "key3", "val4");
		assertEquals(4, hm.size());
	}

	public final void testDelete() {
		String actual = hm.delete("ns1", "key1");
		assertEquals("val3", actual);
		assertEquals(2, hm.size());

		actual = hm.delete("neverbeenhere", "neverbeenthere");
		assertNull(actual);
	}

	public final void testGet() {
		String actual = hm.get("ns1", "key1");
		assertEquals("val3", actual);

		actual = hm.get("ns2", "key1");
		assertEquals("val7", actual);

		actual = hm.get("ns9", "key99");
		assertNull(actual);
	}
}
