package de.cologneintelligence.fitgoodies;

// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.TypeAdapter;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class TypeAdapterTest {

	@SuppressWarnings("unused")
	class TestObject { // used in testTypeAdapter
		public byte sampleByte;
		public short sampleShort;
		public int sampleInt;
		public Integer sampleInteger;
		public float sampleFloat;
		public long sampleLong;
		public Long sampleLongObject;
		public double sampleDouble;
		public Double sampleDoubleObject;
		public Float sampleFloatObject;

		public double pi() {
			return 3.14159862;
		}

		public char ch;
		public String name;
		public int[] sampleArray;
		public Date sampleDate;
		public boolean sampleBool;

		public SimpleDateFormat error;
	}

	private TestObject f;

	@Before
	public void setup() {
		this.f = new TestObject();
	}

	@Test
	public void intTypeAdapter() throws Exception {
		setFieldInF("sampleInt", "123456");
		assertThat(f.sampleInt, is(equalTo(123456)));

		setFieldInF("sampleInteger", "54321");
		assertThat(f.sampleInteger, is(equalTo(54321)));
	}

	@Test
	public void longTypeAdapter() throws Exception {
		setFieldInF("sampleLong", "123456");
		assertThat(f.sampleLong, is(equalTo(123456L)));

		setFieldInF("sampleLongObject", "54321");
		assertThat(f.sampleLongObject, is(equalTo(54321L)));
	}

	@Test
	public void charTypeAdapter() throws Exception {
		setFieldInF("ch", "abc");
		assertThat(f.ch, is(equalTo((Object) 'a')));
	}

	@Test
	public void stringTypeAdapter() throws Exception {
		setFieldInF("name", "xyzzy");
		assertThat(f.name, is(equalTo("xyzzy")));
	}

	@Test
	public void floatTypeAdapter() throws Exception {
		setFieldInF("sampleFloat", "6.02e23");
		assertThat((double) f.sampleFloat, is(closeTo(6.02e23, 1e17)));

		setFieldInF("sampleFloatObject", "1.5");
		assertThat((double) f.sampleFloatObject, is(closeTo(1.5, 0.1)));
	}

	@Test
	public void doubleTypeAdapter() throws Exception {
		setFieldInF("sampleDouble", "6.02e23");
		assertThat(f.sampleDouble, is(closeTo(6.02e23, 1e17)));

		setFieldInF("sampleDoubleObject", "1.5");
		assertThat(f.sampleDoubleObject, is(closeTo(1.5, 0.1)));

		TypeAdapter a = TypeAdapter.on(f, new Fixture(), f.getClass().getMethod("pi", new Class[]{}));
		assertThat(a.invoke(), (Matcher) is(closeTo(3.14159, 0.00001)));
		assertThat(a.invoke(), (Matcher) is(equalTo(3.14159862)));
	}

	@Test
	public void dateTypeAdapter() throws Exception {
		Date date = new GregorianCalendar(1949, 4, 26).getTime();
		setFieldInF("sampleDate", new SimpleDateFormat("MM/dd/yyyy").format(date));
		assertThat(f.sampleDate, is(equalTo(date)));
	}

	@Test
	public void byteTypeAdapter() throws Exception {
		setFieldInF("sampleByte", "123");
		assertThat(f.sampleByte, is(equalTo((byte) 123)));
	}

	@Test
	public void shortTypeAdapter() throws Exception {
		setFieldInF("sampleShort", "12345");
		assertThat(f.sampleShort, is((short) 12345));
	}

	@Test
	public void arrayTypeAdapter() throws Exception {
		TypeAdapter a = TypeAdapter.on(f, new Fixture(), f.getClass().getField("sampleArray"));
		a.set(a.parse("1,2,3"));
		assertThat(f.sampleArray[0], is(equalTo((Object) 1)));
		assertThat(f.sampleArray[1], is(equalTo((Object) 2)));
		assertThat(f.sampleArray[2], is(equalTo((Object) 3)));
		assertThat(a.toString(f.sampleArray), is(equalTo("1, 2, 3")));
		assertThat(a.equals(new int[]{1, 2, 3}, f.sampleArray), is(true));
	}

	public void setFieldInF(String fieldName, String value) throws Exception {
		TypeAdapter a = TypeAdapter.on(f, new Fixture(), f.getClass().getField(fieldName));
		a.set(a.parse(value));
	}

	@Test
	public void getReturnsNullOnUndefinedField() throws Exception {
		assertThat(new TypeAdapter().get(), is(nullValue()));
	}

	@Test
	public void toStringWithNull() {
		assertThat(new TypeAdapter().toString(null), is(equalTo("null")));
	}

	@Test
	public void booleanTypeAdapter() throws Exception {
		setFieldInF("sampleBool", "true");
		assertThat(f.sampleBool, is(true));
	}

	@Test
	public void testGetOnField() throws Exception {
		f.sampleBool = true;
		f.sampleInt = 42;

		assertThat((Boolean) TypeAdapter.on(f, new Fixture(), f.getClass().getField("sampleBool")).get(), is(true));
		assertThat((Integer) TypeAdapter.on(f, new Fixture(), f.getClass().getField("sampleInt")).get(), is(42));
	}

	@Test
	public void testEquals() {
		TypeAdapter ta1 = TypeAdapter.adapterFor(String.class);

		assertThat(ta1.equals("x", "x"), Matchers.is(true));
		assertThat(ta1.equals("x", "y"), Matchers.is(false));
		assertThat(ta1.equals(null, null), Matchers.is(true));
		assertThat(ta1.equals(null, "x"), Matchers.is(false));
		assertThat(ta1.equals("x", null), Matchers.is(false));
	}
}
