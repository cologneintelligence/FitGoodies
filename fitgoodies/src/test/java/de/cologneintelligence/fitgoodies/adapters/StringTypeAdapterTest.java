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

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import fit.Fixture;
import fit.TypeAdapter;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringTypeAdapterTest extends FitGoodiesTestCase {
	public class StringContainer {
		public String string = "";
	}

	private AbstractTypeAdapter<?> ta1;
	private AbstractTypeAdapter<?> ta2;
	private StringContainer container1;
	private StringContainer container2;

	@Before
	public void setUp() throws Exception {
		container1 = new StringContainer();
		container1.string = "Hello World";

		container2 = new StringContainer();
		container2.string = "Hello World";

		TypeAdapter ta = TypeAdapter.on(container1, new Fixture(),
				StringContainer.class.getField("string"));
		ta1 = new StringTypeAdapter(ta, null);

		ta = TypeAdapter.on(container2, new Fixture(),
				StringContainer.class.getField("string"));
		ta2 = new StringTypeAdapter(ta, null);
	}

	@Test
	public void testEquals() throws Exception {
		assertThat(ta1.equals(ta1.get(), ta2.get()), is(true));
		assertThat(ta1.equals(ta2.get(), ta1.get()), is(true));
		assertThat(ta2.equals(ta1.get(), ta2.get()), is(true));

		container1.string = "x";
		assertThat(ta1.equals(ta1.get(), ta2.get()), is(false));

		container1.string = null;
		assertThat(ta1.equals(ta1.get(), ta2.get()), is(false));

		assertThat(ta1.equals(ta2.get(), ta1.get()), is(false));

		container2.string = null;
		assertThat(ta1.equals(ta1.get(), ta2.get()), is(true));
	}

	@Test
	public void testEqualsWithWhitespaces() throws Exception {
		container1.string += "  ";
		container2.string +=  "  ";
		assertThat(ta1.equals(ta1.get(), ta2.get()), is(true));
		assertThat(ta1.equals(ta2.get(), ta1.get()), is(true));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(ta1.toString(ta1.get()), is(equalTo("Hello World")));
		container1.string += "xy";
		assertThat(ta1.toString(ta1.get()), is(equalTo("Hello Worldxy")));
		container1.string = null;
		assertThat(ta1.toString(ta1.get()), is(equalTo("null")));
	}

	@Test
	public void testType() {
		assertThat(ta1.getType(), (Matcher) is(equalTo(String.class)));
	}
}
