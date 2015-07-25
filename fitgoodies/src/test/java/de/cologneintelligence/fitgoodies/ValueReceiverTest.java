package de.cologneintelligence.fitgoodies;

// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

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

		public String getString() { return "text"; }
		public int getInt() { return 7; }
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
		return ValueReceiver.on(target, target.getClass().getField(name));
	}

	private ValueReceiver bindMethod(String name) throws NoSuchMethodException {
		return ValueReceiver.on(target, target.getClass().getMethod(name));
	}

}
