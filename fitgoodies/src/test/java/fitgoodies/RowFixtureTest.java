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


/**
 *
 */
package fitgoodies;

import java.text.ParseException;

import fit.Fixture;
import fit.Parse;
import fitgoodies.references.CrossReferenceHelper;
import fitgoodies.references.processors.CrossReferenceProcessorMock;

/**
 * $Id$
 * @author jwierum
 *
 */
public final class RowFixtureTest extends FitGoodiesTestCase {
	public static class DummyRowObject {
		public Integer x;
		public String y;
		public Integer z;

		public DummyRowObject(
				final Integer xVal,
				final String yVal,
				final Integer zVal) {
			x = xVal;
			y = yVal;
			z = zVal;
		}
	}

	private static class DummyRowFixture extends RowFixture {
		public boolean upCalled;
		public boolean downCalled;

		@Override public Class<?> getTargetClass() {
			return DummyRowObject.class;
		}

		@Override public Object[] query() throws Exception {
			return new DummyRowObject[] {
					new DummyRowObject(1, "x", 3),
					new DummyRowObject(8, "matched", 6)
			};
		}

		@Override public void setUp() { upCalled = true; }
		@Override public void tearDown() { downCalled = true; }
	}

	public static final class ErrorFixture extends DummyRowFixture {
		private boolean downCalled = false;
		public boolean isDownCalled() { return downCalled; }
		@Override public void tearDown() { downCalled = true; }
	}

	private DummyRowFixture rowFixture;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		rowFixture = new DummyRowFixture();
	}

	public void testNumberCases() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>x</td><td>y</td><td>z</td></tr>"
				+ "<tr><td>1</td><td>x</td><td>3</td></tr></table>");
		rowFixture.doTable(table);
		assertEquals(3, rowFixture.counts.right);
		assertEquals(1, rowFixture.counts.wrong);

		table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>x</td><td>y</td><td>z</td></tr>"
				+ "<tr><td>1</td><td>match</td><td>3</td></tr></table>");
		rowFixture.doTable(table);
		assertEquals(4, rowFixture.counts.wrong);
		assertEquals(5, rowFixture.counts.right);
	}

	public void testCrossReferences() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>x</td><td>y</td><td>z</td></tr>"
				+ "<tr><td>8</td><td>${2}</td><td>6</td></tr></table>");

		CrossReferenceHelper.instance().getProcessors().add(
				new CrossReferenceProcessorMock("2"));

		rowFixture.doTable(table);
		assertEquals(3, rowFixture.counts.right);

		table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>x</td><td>y</td></tr>"
				+ "<tr><td>8</td><td>${test}</td></tr></table>");
		rowFixture.doTable(table);
		assertEquals(4, rowFixture.counts.wrong);
	}

	public void testUpDown() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>number</td><td>n()</td></tr>"
				+ "<tr><td>1</td></tr>1</table>");
		rowFixture.doTable(table);

		assertTrue(rowFixture.upCalled);
		assertTrue(rowFixture.downCalled);
	}

	public void testGetParams() {
		rowFixture.setParams(new String[]{"x=y", "a=b"});

		assertEquals("y", rowFixture.getParam("x"));
		assertNull(rowFixture.getParam("y"));

		assertEquals("b", rowFixture.getParam("a", "z"));
		assertEquals("z", rowFixture.getParam("u", "z"));
	}

	public void testUpWithErrors() throws Exception {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "<tr><td>x</td></tr></table>");

		Fixture fixture = new RowFixture() {
			@Override public Class<?> getTargetClass() { return null; }
			@Override public Object[] query() throws Exception { return null; }
			@Override
			public void setUp() throws Exception { throw new RuntimeException("x"); }
			@Override
			public void tearDown() throws Exception { throw new RuntimeException("x"); }
		};
		fixture.doTable(table);

		assertEquals(0, fixture.counts.right);
		assertEquals(0, fixture.counts.wrong);
		assertEquals(1, fixture.counts.exceptions);
	}

	public void testDownWithErrors() throws Exception {
		Parse table = new Parse("<table><tr><td>ignore</td></tr>"
				+ "</table>");

		ErrorFixture fixture = new ErrorFixture();
		fixture.doTable(table);

		assertEquals(0, fixture.counts.right);
		assertEquals(0, fixture.counts.wrong);
		assertEquals(1, fixture.counts.exceptions);
		assertTrue(fixture.isDownCalled());
	}
}
