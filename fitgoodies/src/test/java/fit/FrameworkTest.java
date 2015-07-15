package fit;

// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

import junit.framework.TestCase;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class FrameworkTest extends TestCase {

	public FrameworkTest(String name) {
		super(name);
	}

	public void testTypeAdapter() throws Exception {
		TestFixture f = new TestFixture();
		TypeAdapter a = TypeAdapter.on(f, f.getClass().getField("sampleInt"));
		a.set(a.parse("123456"));
		assertEquals(123456, f.sampleInt);
		assertEquals("-234567", a.parse("-234567").toString());
		a = TypeAdapter.on(f, f.getClass().getField("sampleInteger"));
		a.set(a.parse("54321"));
		assertEquals("54321", f.sampleInteger.toString());
		a = TypeAdapter.on(f, f.getClass().getMethod("pi", new Class[]{}));
		assertEquals(3.14159, ((Double) a.invoke()).doubleValue(), 0.00001);
		assertEquals(new Double(3.14159862), a.invoke());
		a = TypeAdapter.on(f, f.getClass().getField("ch"));
		a.set(a.parse("abc"));
		assertEquals('a', f.ch);
		a = TypeAdapter.on(f, f.getClass().getField("name"));
		a.set(a.parse("xyzzy"));
		assertEquals("xyzzy", f.name);
		a = TypeAdapter.on(f, f.getClass().getField("sampleFloat"));
		a.set(a.parse("6.02e23"));
		assertEquals(6.02e23, f.sampleFloat, 1e17);
		a = TypeAdapter.on(f, f.getClass().getField("sampleArray"));
		a.set(a.parse("1,2,3"));
		assertEquals(1, f.sampleArray[0]);
		assertEquals(2, f.sampleArray[1]);
		assertEquals(3, f.sampleArray[2]);
		assertEquals("1, 2, 3", a.toString(f.sampleArray));
		assertTrue(a.equals(new int[]{1, 2, 3}, f.sampleArray));
		a = TypeAdapter.on(f, f.getClass().getField("sampleDate"));
		Date date = new GregorianCalendar(1949, 4, 26).getTime();
		a.set(a.parse(DateFormat.getDateInstance().format(date)));
		assertEquals(date, f.sampleDate);
		a = TypeAdapter.on(f, f.getClass().getField("sampleByte"));
		a.set(a.parse("123"));
		assertEquals(123, f.sampleByte);
		a = TypeAdapter.on(f, f.getClass().getField("sampleShort"));
		a.set(a.parse("12345"));
		assertEquals(12345, f.sampleShort);
	}

	public void testScientificDouble() {
		Double pi = new Double(3.141592865);
		assertEquals(ScientificDouble.valueOf("3.14"), pi);
		assertEquals(ScientificDouble.valueOf("3.142"), pi);
		assertEquals(ScientificDouble.valueOf("3.1416"), pi);
		assertEquals(ScientificDouble.valueOf("3.14159"), pi);
		assertEquals(ScientificDouble.valueOf("3.141592865"), pi);
		assertTrue(!ScientificDouble.valueOf("3.140").equals(pi));
		assertTrue(!ScientificDouble.valueOf("3.144").equals(pi));
		assertTrue(!ScientificDouble.valueOf("3.1414").equals(pi));
		assertTrue(!ScientificDouble.valueOf("3.141592863").equals(pi));
		assertEquals(ScientificDouble.valueOf("6.02e23"), new Double(6.02e23));
		assertEquals(ScientificDouble.valueOf("6.02E23"), new Double(6.024E23));
		assertEquals(ScientificDouble.valueOf("6.02e23"), new Double(6.016e23));
		assertTrue(!ScientificDouble.valueOf("6.02e23").equals(new Double(6.026e23)));
		assertTrue(!ScientificDouble.valueOf("6.02e23").equals(new Double(6.014e23)));
	}

	public void testEscape() {
		String junk = "!@#$%^*()_-+={}|[]\\:\";',./?`";
		assertEquals(junk, Fixture.escape(junk));
		assertEquals("", Fixture.escape(""));
		assertEquals("&lt;", Fixture.escape("<"));
		assertEquals("&lt;&lt;", Fixture.escape("<<"));
		assertEquals("x&lt;", Fixture.escape("x<"));
		assertEquals("&amp;", Fixture.escape("&"));
		assertEquals("&lt;&amp;&lt;", Fixture.escape("<&<"));
		assertEquals("&amp;&lt;&amp;", Fixture.escape("&<&"));
		assertEquals("a &lt; b &amp;&amp; c &lt; d", Fixture.escape("a < b && c < d"));
		assertEquals("a<br />b", Fixture.escape("a\nb"));
	}

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
