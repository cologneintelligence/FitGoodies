/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;



public class NamespaceHashMapTest extends FitGoodiesTestCase {
	private final NamespaceHashMap<String> hm = new NamespaceHashMap<String>();

	@Before
	public void setUp() throws Exception {
		hm.put("ns1", "key1", "val1");
		hm.put("ns1", "key2", "val2");
		hm.put("ns1", "key1", "val3");

		hm.put("ns2", "key1", "val7");
	}

	@Test
	public void testSize() {
		assertThat(hm.size(), is(equalTo((Object) 3)));

		hm.put("ns1", "key3", "val4");
		assertThat(hm.size(), is(equalTo((Object) 4)));
	}

	@Test
	public void testDelete() {
		String actual = hm.delete("ns1", "key1");
		assertThat(actual, is(equalTo("val3")));
		assertThat(hm.size(), is(equalTo((Object) 2)));

		actual = hm.delete("neverbeenhere", "neverbeenthere");
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void testGet() {
		String actual = hm.get("ns1", "key1");
		assertThat(actual, is(equalTo("val3")));

		actual = hm.get("ns2", "key1");
		assertThat(actual, is(equalTo("val7")));

		actual = hm.get("ns9", "key99");
		assertThat(actual, is(nullValue()));
	}
}
