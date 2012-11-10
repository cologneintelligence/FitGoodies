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


package de.cologneintelligence.fitgoodies.adapters;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.adapters.AbstractTypeAdapter;
import de.cologneintelligence.fitgoodies.adapters.StringBuilderTypeAdapter;
import fit.Fixture;
import fit.TypeAdapter;

/**
 *
 * @author jwierum
 */
public class StringBuilderTypeAdapterTest extends FitGoodiesTestCase {
	public class StringBuilderContainer extends Fixture {
		public StringBuilder builder = new StringBuilder();
	}

	private AbstractTypeAdapter<?> ta1;
	private AbstractTypeAdapter<?> ta2;
	private StringBuilderContainer container1;
	private StringBuilderContainer container2;

	@Override
	public final void setUp() throws Exception {
		super.setUp();

		container1 = new StringBuilderContainer();
		container1.builder.append("Hello World");

		container2 = new StringBuilderContainer();
		container2.builder.append("Hello World");

		TypeAdapter ta = TypeAdapter.on(container1,
				StringBuilderContainer.class.getField("builder"));
		ta1 = new StringBuilderTypeAdapter(ta, null);

		ta = TypeAdapter.on(container2,
				StringBuilderContainer.class.getField("builder"));
		ta2 = new StringBuilderTypeAdapter(ta, null);
	}

	public final void testEquals() throws Exception {
		assertTrue(ta1.equals(ta1.get(), ta2.get()));
		assertTrue(ta1.equals(ta2.get(), ta1.get()));
		assertTrue(ta2.equals(ta1.get(), ta2.get()));

		container1.builder.append("x");
		assertFalse(ta1.equals(ta1.get(), ta2.get()));

		container1.builder = null;
		assertFalse(ta1.equals(ta1.get(), ta2.get()));
		assertFalse(ta1.equals(ta2.get(), ta1.get()));

		container2.builder = null;
		assertTrue(ta1.equals(ta1.get(), ta2.get()));
	}

	public final void testEqualsWithWhitespaces() throws Exception {
		container1.builder.append("  ");
		container1.builder.insert(0, "  ");
		assertTrue(ta1.equals(ta1.get(), ta2.get()));
		assertTrue(ta1.equals(ta2.get(), ta1.get()));
	}

	public final void testToString() throws Exception {
		assertEquals("Hello World", ta1.toString(ta1.get()));
		container1.builder.append("xy");
		assertEquals("Hello Worldxy", ta1.toString(ta1.get()));
		container1.builder = null;
		assertEquals("null", ta1.toString(ta1.get()));
	}

	public final void testType() {
		assertEquals(StringBuilder.class, ta1.getType());
	}

	public final void testParse() throws Exception {
		assertEquals("test", ((StringBuilder) ta1.parse("test")).toString());
		assertEquals("another test", ((StringBuilder)
				ta1.parse("another test")).toString());
	}
}
