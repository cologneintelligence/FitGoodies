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


package de.cologneintelligence.fitgoodies.adapters;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.adapters.AbstractTypeAdapter;
import de.cologneintelligence.fitgoodies.adapters.StringTypeAdapter;
import fit.Fixture;
import fit.TypeAdapter;

/**
 *
 * @author kmussawisade
 */
public class StringTypeAdapterTest extends FitGoodiesTestCase {
	public class StringContainer extends Fixture {
		public String string = "";
	}

	private AbstractTypeAdapter<?> ta1;
	private AbstractTypeAdapter<?> ta2;
	private StringContainer container1;
	private StringContainer container2;

	@Override
	public final void setUp() throws Exception {
		super.setUp();

		container1 = new StringContainer();
		container1.string = "Hello World";

		container2 = new StringContainer();
		container2.string = "Hello World";

		TypeAdapter ta = TypeAdapter.on(container1,
				StringContainer.class.getField("string"));
		ta1 = new StringTypeAdapter(ta, null);

		ta = TypeAdapter.on(container2,
				StringContainer.class.getField("string"));
		ta2 = new StringTypeAdapter(ta, null);
	}

	public final void testEquals() throws Exception {
		assertTrue(ta1.equals(ta1.get(), ta2.get()));
		assertTrue(ta1.equals(ta2.get(), ta1.get()));
		assertTrue(ta2.equals(ta1.get(), ta2.get()));

		container1.string = "x";
		assertFalse(ta1.equals(ta1.get(), ta2.get()));

		container1.string = null;
		assertFalse(ta1.equals(ta1.get(), ta2.get()));
		assertFalse(ta1.equals(ta2.get(), ta1.get()));

		container2.string = null;
		assertTrue(ta1.equals(ta1.get(), ta2.get()));
	}

	public final void testEqualsWithWhitespaces() throws Exception {
		container1.string += "  ";
		container2.string +=  "  ";
		assertTrue(ta1.equals(ta1.get(), ta2.get()));
		assertTrue(ta1.equals(ta2.get(), ta1.get()));
	}

	public final void testToString() throws Exception {
		assertEquals("Hello World", ta1.toString(ta1.get()));
		container1.string += "xy";
		assertEquals("Hello Worldxy", ta1.toString(ta1.get()));
		container1.string = null;
		assertEquals("null", ta1.toString(ta1.get()));
	}

	public final void testType() {
		assertEquals(String.class, ta1.getType());
	}
}
