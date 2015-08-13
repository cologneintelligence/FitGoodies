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


package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.file.readers.FileRecordReader;
import de.cologneintelligence.fitgoodies.file.readers.FileRecordReaderMock;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class AbstractFileRecordReaderFixtureTest extends FitGoodiesTestCase {
	private static class DummyRecordReaderFixture
			extends AbstractFileRecordReaderFixture {

		public DummyRecordReaderFixture(final FileRecordReader r) {
			super.setRecordReader(r);
		}

		@Override
		public void setUp() {
		}
	}

	@Test
	public void testComparison() throws Exception {
		Parse table = parseTable(tr("x", "y"), tr("1", "2"));

		DummyRecordReaderFixture fixture = new DummyRecordReaderFixture(
				new FileRecordReaderMock(new String[][]{
						new String[]{"x", "y"},
						new String[]{"1", "2"}
				}));

		fixture.doTable(table);

		assertThat(fixture.counts().ignores, is(equalTo((Object) 0)));
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
		assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
		assertThat(fixture.counts().right, is(equalTo((Object) 4)));
	}

	@Test
	public void testComparisonWithErrors() throws Exception {
		Parse table = parseTable(
				tr("x", "z", "hello"),
				tr("1", "u", "4"));

		DummyRecordReaderFixture fixture = new DummyRecordReaderFixture(
				new FileRecordReaderMock(new String[][]{
						new String[]{"x", "z", "test"},
						new String[]{"2", "5", "4"}
				}));

		fixture.doTable(table);

		assertThat(fixture.counts().right, is(equalTo((Object) 3)));
		assertThat(fixture.counts().wrong, is(equalTo((Object) 3)));
	}

	@Test
	public void testSurplusRows() throws Exception {
		Parse table = parseTable(tr("x", "z", "hello"));

		DummyRecordReaderFixture fixture = new DummyRecordReaderFixture(
				new FileRecordReaderMock(new String[][]{
						new String[]{"x", "z", "hello"},
						new String[]{"2", "5", "4"},
						new String[]{"2", "5", "4"}
				}));

		fixture.doTable(table);

		assertThat(fixture.counts().right, is(equalTo((Object) 3)));
		assertThat(fixture.counts().wrong, is(equalTo((Object) 6)));

		assertThat(table.parts.more.more.parts.text(), containsString("surplus"));
		assertThat(table.parts.more.more.parts.last().text(), containsString("surplus"));
	}

	@Test
	public void testMissingRows() throws Exception {
		Parse table = parseTable(
				tr("x", "z", "hello"),
				tr("1", "2", "3"),
				tr("4", "5", "6"));

		DummyRecordReaderFixture fixture = new DummyRecordReaderFixture(
				new FileRecordReaderMock(new String[][]{
						new String[]{"x", "z", "hello"},
				}));

		fixture.doTable(table);

		assertThat(fixture.counts().right, is(equalTo((Object) 3)));
		assertThat(fixture.counts().wrong, is(equalTo((Object) 6)));

		assertThat(table.parts.more.more.parts.text(), containsString("missing"));
		assertThat(table.parts.more.more.parts.last().text(), containsString("missing"));
	}

	@Test
	public void testEmptyTable() {
		Parse table = parseTable();

		DummyRecordReaderFixture fixture = new DummyRecordReaderFixture(
				new FileRecordReaderMock(new String[][]{
						new String[]{"x", "z", "hello"},
				}));

		fixture.doTable(table);
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 1)));
		assertThat(table.parts.parts.text(), containsString("at least"));
	}
}
