/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
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


package de.cologneintelligence.fitgoodies.dynamic;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class DynamicObjectFactoryTest extends FitGoodiesTestCase {
	private DynamicObjectFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new DynamicObjectFactory();
	}

	@Test
	public void testCreation() throws Exception {
		factory.add(String.class, "value");
		factory.add(Integer.TYPE, "number");
		Class<?> c = factory.compile();

		Object o = c.newInstance();
		assertThat(o.getClass().getField("number").getType(), (Matcher) is(equalTo(Integer.TYPE)));
		assertThat(o.getClass().getField("value").getType(), (Matcher) is(equalTo(String.class)));
	}

	@Test
	public void testComplexCreationTest() throws Exception {
		factory.add(StringBuilder.class, "text");
		Object o = factory.compile().newInstance();

		Field sbField = o.getClass().getField("text");
		sbField.set(o, new StringBuilder());
		StringBuilder sb = (StringBuilder) sbField.get(o);

		sb.append("Hello ");
		sb.append("World!");

		assertThat(sbField.get(o).toString(), is(equalTo("Hello World!")));
	}

	@Test
	public void testMultipleCreations() throws Exception {
		// no tests here, it just must not fail
		factory.add(Integer.TYPE, "number");
		factory.compile();

		try {
			factory.add(Integer.TYPE, "count");
			Assert.fail("Could modify compiled class");
		} catch (IllegalStateException ignored) {
		}

		factory = new DynamicObjectFactory();
		factory.add(Integer.TYPE, "count");
		factory.compile();
	}
}
