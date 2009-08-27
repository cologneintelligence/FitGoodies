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


package fitgoodies.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import fit.Fixture;
import fit.Parse;
import fit.TypeAdapter;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.adapters.AbstractTypeAdapter;
import fitgoodies.adapters.DummyTypeAdapter;
import fitgoodies.adapters.TypeAdapterHelper;
import fitgoodies.parsers.LongParserMock;
import fitgoodies.references.CrossReferenceHelper;
import fitgoodies.util.FixtureTools;

/**
 * $Id: FixtureToolsTest.java 203 2009-08-24 12:03:16Z jwierum $
 * @author jwierum
 */
public class FixtureToolsTest extends FitGoodiesTestCase {
	public static class DummyValueFixture extends Fixture {
		public int x;
		public String y;

		public int a;
		public String b;

		public BigInteger[] arr = new BigInteger[]{
				new BigInteger("1"),
				new BigInteger("2"),
				new BigInteger("3")};
	}

	public final void testParse() throws Exception {
		Integer intExpected = Integer.valueOf(42);
		assertNull(FixtureTools.parse("42", intExpected.getClass(), null));

		BigInteger biExpected = new BigInteger("123");
		assertEquals(biExpected, FixtureTools.parse("123", biExpected.getClass(), null));
		biExpected = new BigInteger("7");
		assertEquals(biExpected, FixtureTools.parse("7", biExpected.getClass(), null));

		BigDecimal bdExpected = new BigDecimal("312.45");
		assertEquals(bdExpected, FixtureTools.parse("312.45", bdExpected.getClass(), null));
		bdExpected = new BigDecimal("331.0");
		assertEquals(bdExpected, FixtureTools.parse("331.0", bdExpected.getClass(), null));

		fitgoodies.parsers.ParserHelper.instance().registerParser(new LongParserMock());
		assertEquals(Long.valueOf(7), FixtureTools.parse("7", Long.class, "x"));
	}

	public final void testRebindTypeAdapter() {
		TypeAdapter ta = new TypeAdapter();
		TypeAdapter actual;

		ta.type = BigInteger.class;
		actual = FixtureTools.rebindTypeAdapter(ta, null);
		assertSame(ta, actual);

		TypeAdapterHelper.reset();
		TypeAdapterHelper.instance().register(DummyTypeAdapter.class);

		actual = FixtureTools.rebindTypeAdapter(ta, null);
		assertNotSame(ta, actual);
		assertEquals(DummyTypeAdapter.class, actual.getClass());
	}

	public final void testRebingTypeAdapterWithParameter() throws Exception {
		TypeAdapter ta = new TypeAdapter();
		AbstractTypeAdapter<?> actual;

		ta.type = BigInteger.class;
		TypeAdapterHelper.instance().register(DummyTypeAdapter.class);

		actual = (AbstractTypeAdapter<?>)
			FixtureTools.rebindTypeAdapter(ta, "test");
		assertEquals("test", actual.getParameter());

		actual = (AbstractTypeAdapter<?>)
			FixtureTools.rebindTypeAdapter(ta, "parameter");
		assertEquals("parameter", actual.getParameter());
	}

	public final void testGetParameter() throws Exception {
		String[] args = new String[]{
				"x = y", " param = value "
		};

		assertEquals("y", FixtureTools.getArg(args, "x", null));
		assertEquals("value", FixtureTools.getArg(args, "param", null));
		assertEquals("good", FixtureTools.getArg(args, "not-good", "good"));


		args = new String[]{
				"x =z", " a b=test "
		};

		assertEquals("bad", FixtureTools.getArg(args, "param", "bad"));
		assertEquals("z", FixtureTools.getArg(args, "X", null));
		assertEquals("test", FixtureTools.getArg(args, "A B", null));

		assertEquals("null", FixtureTools.getArg(null, "x", "null"));
		assertEquals("error", FixtureTools.getArg(null, "y", "error"));

		args = new String[]{
				"y = a${tests.get(x)}b", " a b=test "
		};

		CrossReferenceHelper.instance().parseBody("${tests.put(x)}", "x");
		assertEquals("axb", FixtureTools.getArg(args, "y", null));
	}

	public final void testGetParameters() {
		String[] args = new String[]{
				"x = y", " param = value "
		};

		String[] actual = FixtureTools.getArgs(args);
		assertEquals(2, actual.length);
		assertEquals("x", actual[0]);
		assertEquals("param", actual[1]);


		args = new String[]{
				"x =z", " a b=test "
		};

		actual = FixtureTools.getArgs(args);
		assertEquals(2, actual.length);
		assertEquals("x", actual[0]);
		assertEquals("a b", actual[1]);

		actual = FixtureTools.getArgs(null);
		assertEquals(0, actual.length);

		actual = FixtureTools.getArgs(new String[]{});
		assertEquals(0, actual.length);
	}


	public final void testResolveQuestionMarks() throws Exception {
		Parse table = new Parse("<table>"
				+ "<tr><td>ok</td><td>good()</td>"
				+ "<td>works?</td><td>y?n</td></tr>"
				+ "<tr><td>alone?</td></tr></table>");

		FixtureTools.resolveQuestionMarks(table.parts);
		assertEquals("ok", table.parts.parts.text());
		assertEquals("good()", table.parts.parts.more.text());
		assertEquals("works()", table.parts.parts.more.more.text());
		assertEquals("y?n", table.parts.parts.more.more.more.text());
		assertEquals("alone?", table.parts.more.parts.text());

		table = new Parse("<table>"
				+ "<tr><td>alone?</td><td>ok()</td><td>x</td></tr>"
				+ "<tr><td>works?</td></tr></table>");

		FixtureTools.resolveQuestionMarks(table.parts);
		assertEquals("alone()", table.parts.parts.text());
		assertEquals("ok()", table.parts.parts.more.text());
		assertEquals("x", table.parts.parts.more.more.text());
		assertEquals("works?", table.parts.more.parts.text());
	}

	public final void testCopyParamsToFixture() {
		DummyValueFixture fixture = new DummyValueFixture();
		String[] args = new String[]{" x = 8 ", "y=string", "z=error"};

		fixture.a = 9;
		FixtureTools.copyParamsToFixture(args, fixture);
		assertEquals(9, fixture.a);
		assertEquals(8, fixture.x);
		assertEquals("string", fixture.y);

		fixture = new DummyValueFixture();
		args = new String[]{" a = 42 ", "b=c"};

		FixtureTools.copyParamsToFixture(args, fixture);
		assertEquals(42, fixture.a);
		assertEquals("c", fixture.b);
	}

	public final void testColumnParameters() throws Exception {
		Parse table = new Parse("<table>"
				+ "<tr><td>x[1 2]</td><td>y[3 4]</td><td>z</td></tr>"
				+ "<tr><td>a[7]</td><td>b</td><td>c</td></tr>"
				+ "</table>");

		String[] actual = FixtureTools.extractColumnParameters(table.parts);
		assertArray(new String[]{"1 2", "3 4", null}, actual);
		assertEquals("x", table.parts.parts.text());
		assertEquals("y", table.parts.parts.more.text());
		assertEquals("a[7]", table.parts.more.parts.text());

		table = new Parse("<table>"
				+ "<tr><td>name</td><td>date [ de_DE, dd.MM.yyyy ] </td></tr>"
				+ "</table>");

		actual = FixtureTools.extractColumnParameters(table.parts);
		assertArray(new String[]{null, "de_DE, dd.MM.yyyy"}, actual);
		assertEquals("name", table.parts.parts.text());
		assertEquals("date", table.parts.parts.more.text());
	}

	public final void testConvertBoolean() {
		assertTrue(FixtureTools.convertToBoolean("TrUe"));
		assertTrue(FixtureTools.convertToBoolean("1"));
		assertTrue(FixtureTools.convertToBoolean("yes"));
		assertFalse(FixtureTools.convertToBoolean("false"));
		assertFalse(FixtureTools.convertToBoolean("no"));
		assertFalse(FixtureTools.convertToBoolean("0"));

		try {
			FixtureTools.convertToBoolean("non-bool");
			fail("invalid string should be recognized");
		} catch (IllegalArgumentException e) {
		}
	}
}
