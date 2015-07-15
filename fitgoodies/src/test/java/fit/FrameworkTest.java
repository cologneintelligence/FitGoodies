package fit;

// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

public class FrameworkTest {

	private TestFixture f;

	@Before
	public void setup() {
		this.f = new TestFixture();
	}

	@Test
	public void intTypeAdapter() throws Exception {
		TypeAdapter a = TypeAdapter.on(f, f.getClass().getField("sampleInt"));
		a.set(a.parse("123456"));
		assertThat(f.sampleInt, is(equalTo((Object) 123456)));
		assertThat(a.parse("-234567").toString(), is(equalTo("-234567")));
		a = TypeAdapter.on(f, f.getClass().getField("sampleInteger"));
		a.set(a.parse("54321"));
		assertThat(f.sampleInteger.toString(), is(equalTo("54321")));
	}

	@Test
	public void doubleTypeAdapter() throws Exception {
		TypeAdapter a = TypeAdapter.on(f, f.getClass().getMethod("pi", new Class[]{}));
		assertThat(a.invoke(), (Matcher) is(closeTo(3.14159, 0.00001)));
		assertThat(a.invoke(), (Matcher) is(equalTo(3.14159862)));
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
	}

	public void setFieldInF(String fieldName, String value) throws Exception {
		TypeAdapter a = TypeAdapter.on(f, f.getClass().getField(fieldName));
		a.set(a.parse(value));
	}

	@Test
	public void dateTypeAdapter() throws Exception {
		Date date = new GregorianCalendar(1949, 4, 26).getTime();
		setFieldInF("sampleDate", DateFormat.getDateInstance().format(date));
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
		TypeAdapter a = TypeAdapter.on(f, f.getClass().getField("sampleArray"));
		a.set(a.parse("1,2,3"));
		assertThat(f.sampleArray[0], is(equalTo((Object) 1)));
		assertThat(f.sampleArray[1], is(equalTo((Object) 2)));
		assertThat(f.sampleArray[2], is(equalTo((Object) 3)));
		assertThat(a.toString(f.sampleArray), is(equalTo("1, 2, 3")));
		assertThat(a.equals(new int[]{1, 2, 3}, f.sampleArray), is(true));
	}

	@Test
	public void testScientificDouble() {
		Double pi = 3.141592865;
		assertEquals(pi, ScientificDouble.valueOf("3.14"));
		assertEquals(pi, ScientificDouble.valueOf("3.142"));
		assertEquals(pi, ScientificDouble.valueOf("3.1416"));
		assertEquals(pi, ScientificDouble.valueOf("3.14159"));
		assertEquals(pi, ScientificDouble.valueOf("3.141592865"));

		assertNotEquals(ScientificDouble.valueOf("3.140"), pi);
		assertNotEquals(ScientificDouble.valueOf("3.144"), pi);
		assertNotEquals(ScientificDouble.valueOf("3.1414"), pi);
		assertNotEquals(ScientificDouble.valueOf("3.141592863"), pi);

		assertEquals(6.02e23, ScientificDouble.valueOf("6.02e23"));
		assertEquals(6.024E23, ScientificDouble.valueOf("6.02E23"));
		assertEquals(6.016e23, ScientificDouble.valueOf("6.02e23"));

		assertThat(ScientificDouble.valueOf("6.02e23"), (Matcher) is(not(equalTo(6.026e23))));
		assertThat(ScientificDouble.valueOf("6.02e23"), (Matcher) is(not(equalTo(6.014e23))));
	}

	private void assertNotEquals(ScientificDouble expected, Double actual) {
		assertThat(expected, (Matcher) is(not(equalTo(actual))));
	}

	private void assertEquals(Double expected, ScientificDouble actual) {
		assertThat(actual, (Matcher) is(equalTo(expected)));
	}

	@Test
	public void testEscape() {
		String junk = "!@#$%^*()_-+={}|[]\\:\";',./?`";
		assertThat(Fixture.escape(junk), is(equalTo(junk)));
		assertThat(Fixture.escape(""), is(equalTo("")));
		assertThat(Fixture.escape("<"), is(equalTo("&lt;")));
		assertThat(Fixture.escape("<<"), is(equalTo("&lt;&lt;")));
		assertThat(Fixture.escape("x<"), is(equalTo("x&lt;")));
		assertThat(Fixture.escape("&"), is(equalTo("&amp;")));
		assertThat(Fixture.escape("<&<"), is(equalTo("&lt;&amp;&lt;")));
		assertThat(Fixture.escape("&<&"), is(equalTo("&amp;&lt;&amp;")));
		assertThat(Fixture.escape("a < b && c < d"), is(equalTo("a &lt; b &amp;&amp; c &lt; d")));
		assertThat(Fixture.escape("a\nb"), is(equalTo("a<br />b")));
	}

	@SuppressWarnings("unused")
	class TestFixture extends ColumnFixture { // used in testTypeAdapter
		public byte sampleByte;
		public short sampleShort;
		public int sampleInt;
		public Integer sampleInteger;
		public float sampleFloat;

		public double pi() {
			return 3.14159862;
		}

		public char ch;
		public String name;
		public int[] sampleArray;
		public Date sampleDate;
	}
}
