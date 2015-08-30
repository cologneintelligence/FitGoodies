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

package de.cologneintelligence.fitgoodies.valuereceivers;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ValueReceiverTest {

	@SuppressWarnings("unused")
	class TestObject { // used in testTypeAdapter
		public String name;
		public int age;

		public String getString() {
			return "text";
		}

		public int getInt() {
			return 7;
		}
	}

	private TestObject target;

	@Before
	public void setup() {
		this.target = new TestObject();
	}

	@Test
	public void canReadFields() throws Exception {
		target.name = "bla";
		assertThat(bindField("name").get(), (Matcher) (equalTo("bla")));

		target.age = 12;
		assertThat(bindField("age").get(), (Matcher) is(equalTo(12)));
	}

	@Test
	public void canCallMethods() throws Exception {
		assertThat(bindMethod("getString").get(), (Matcher) (equalTo("text")));
		assertThat(bindMethod("getInt").get(), (Matcher) is(equalTo(7)));
	}

	@Test
	public void canSetFields() throws Exception {
		ValueReceiver field = bindField("name");
		assertThat(field.canSet(), is(true));

		String text = "test";
		field.set(target, text);
		assertThat(target.name, is(equalTo(text)));

		text = "another value";
		field.set(target, text);
		assertThat(target.name, is(equalTo(text)));
	}

	@Test
	public void cantSetMethods() throws NoSuchMethodException {
		assertThat(bindMethod("getString").canSet(), is(false));
	}

	public ValueReceiver bindField(String name) throws NoSuchFieldException {
		return new FieldValueReceiver(target.getClass().getField(name), target);
	}

	private ValueReceiver bindMethod(String name) throws NoSuchMethodException {
		return new MethodValueReceiver(target.getClass().getMethod(name), target);
	}

}
