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


package fitgoodies.dynamic;

import java.lang.reflect.Field;

import fitgoodies.FitGoodiesTestCase;

/**
 * $Id: DynamicObjectFactoryTest.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public class DynamicObjectFactoryTest extends FitGoodiesTestCase {
	private DynamicObjectFactory factory;

	@Override
	public final void setUp() throws Exception {
		super.setUp();
		factory = new DynamicObjectFactory();
	}

	public final void testCreation() throws Exception {
		factory.add(String.class, "value");
		factory.add(Integer.TYPE, "number");
		Class<?> c = factory.compile();

		Object o = c.newInstance();
		assertEquals(Integer.TYPE, o.getClass().getField("number").getType());
		assertEquals(String.class, o.getClass().getField("value").getType());
	}

	public final void testComplexCreationTest() throws Exception {
		factory.add(StringBuilder.class, "text");
		Object o = factory.compile().newInstance();

		Field sbField = o.getClass().getField("text");
		sbField.set(o, new StringBuilder());
		StringBuilder sb = (StringBuilder) sbField.get(o);

		sb.append("Hello ");
		sb.append("World!");

		assertEquals("Hello World!", sbField.get(o).toString());
	}

	public final void testMultipleCreations() throws Exception {
		// no tests here, it just must not fail
		factory.add(Integer.TYPE, "number");
		factory.compile();

		try {
			factory.add(Integer.TYPE, "count");
			fail("Could modify compiled class");
		} catch (IllegalStateException e) {
		}

		factory = new DynamicObjectFactory();
		factory.add(Integer.TYPE, "count");
		factory.compile();
	}
}
