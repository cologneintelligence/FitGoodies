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


package fitgoodies.file;

import java.text.ParseException;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.file.readers.FileRecordReader;
import fitgoodies.file.readers.FileRecordReaderMock;

/**
 * $Id$
 * @author jwierum
 */
public class AbstractFileRecordReaderFixtureTest extends FitGoodiesTestCase {
	private static class DummyRecordReaderFixture
			extends AbstractFileRecordReaderFixture {

		public DummyRecordReaderFixture(final FileRecordReader r) {
			super.setRecordReader(r);
		}

		@Override public final void setUp() {
		};
	}

	public final void testComparison() throws Exception {
		Parse table = new Parse(
				"<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>x</td><td>y</td></tr>"
				+ "<tr><td>1</td><td>2</td></tr>"
				+ "</table>"
				);

		DummyRecordReaderFixture fixture = new DummyRecordReaderFixture(
				new FileRecordReaderMock(new String[][] {
						new String[]{"x", "y"},
						new String[]{"1", "2"}
				}));

		fixture.doTable(table);

		assertEquals(0, fixture.counts.ignores);
		assertEquals(0, fixture.counts.exceptions);
		assertEquals(0, fixture.counts.wrong);
		assertEquals(4, fixture.counts.right);
	}

	public final void testComparisonWithErrors() throws Exception {
		Parse table = new Parse(
				"<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>x</td><td>z</td><td>hello</td></tr>"
				+ "<tr><td>1</td><td>u</td><td>4</td></tr>"
				+ "</table>"
				);

		DummyRecordReaderFixture fixture = new DummyRecordReaderFixture(
				new FileRecordReaderMock(new String[][] {
						new String[]{"x", "z", "test"},
						new String[]{"2", "5", "4"}
				}));

		fixture.doTable(table);

		assertEquals(3, fixture.counts.right);
		assertEquals(3, fixture.counts.wrong);
	}

	public final void testSurplusRows() throws Exception {
		Parse table = new Parse(
				"<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>x</td><td>z</td><td>hello</td></tr>"
				+ "</table>"
				);

		DummyRecordReaderFixture fixture = new DummyRecordReaderFixture(
				new FileRecordReaderMock(new String[][] {
						new String[]{"x", "z", "hello"},
						new String[]{"2", "5", "4"},
						new String[]{"2", "5", "4"}
				}));

		fixture.doTable(table);

		assertEquals(3, fixture.counts.right);
		assertEquals(6, fixture.counts.wrong);

		assertContains("surplus", table.parts.more.more.parts.text());
		assertContains("surplus", table.parts.more.more.parts.last().text());
	}

	public final void testMissingRows() throws Exception {
		Parse table = new Parse(
				"<table>"
				+ "<tr><td>ignore</td></tr>"
				+ "<tr><td>x</td><td>z</td><td>hello</td></tr>"
				+ "<tr><td>1</td><td>2</td><td>3</td></tr>"
				+ "<tr><td>4</td><td>5</td><td>6</td></tr>"
				+ "</table>"
				);

		DummyRecordReaderFixture fixture = new DummyRecordReaderFixture(
				new FileRecordReaderMock(new String[][] {
						new String[]{"x", "z", "hello"},
				}));

		fixture.doTable(table);

		assertEquals(3, fixture.counts.right);
		assertEquals(6, fixture.counts.wrong);

		assertContains("missing", table.parts.more.more.parts.text());
		assertContains("missing", table.parts.more.more.parts.last().text());
	}

	public final void testEmptyTable() throws ParseException {
		Parse table = new Parse("<table><tr><td>ignore</td></tr></table>");

		DummyRecordReaderFixture fixture = new DummyRecordReaderFixture(
				new FileRecordReaderMock(new String[][] {
						new String[]{"x", "z", "hello"},
				}));

		fixture.doTable(table);
		assertEquals(1, fixture.counts.exceptions);
		assertContains("at least", table.parts.parts.text());
	}
}
