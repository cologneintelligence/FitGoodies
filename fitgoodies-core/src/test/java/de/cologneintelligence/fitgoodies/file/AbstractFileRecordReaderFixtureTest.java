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

import de.cologneintelligence.fitgoodies.file.readers.FileRecordReaderMock;
import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesFixtureTestCase;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;


public class AbstractFileRecordReaderFixtureTest extends FitGoodiesFixtureTestCase<AbstractFileRecordReaderFixtureTest.DummyRecordReaderFixture> {
	public static class DummyRecordReaderFixture extends AbstractFileRecordReaderFixture {
		@Override
		public void setUp() {
		}
	}

    @Override
    protected Class<DummyRecordReaderFixture> getFixtureClass() {
        return DummyRecordReaderFixture.class;
    }

    @Test
	public void testComparison() throws Exception {
		useTable(tr("x", "y"), tr("1", "2"));

        fixture.setRecordReader(
            new FileRecordReaderMock(new String[][]{
                new String[]{"x", "y"},
                new String[]{"1", "2"}
            }));

		run();

        expectConstantValidation(0, 0, "x");
        expectConstantValidation(0, 1, "y");
        expectConstantValidation(1, 0, "1");
        expectConstantValidation(1, 1, "2");
	}

    @Test
	public void testComparisonWithErrors() throws Exception {
		useTable(
				tr("x", "z", "hello"),
				tr("1", "u", "4"));

		fixture.setRecordReader(
				new FileRecordReaderMock(new String[][]{
						new String[]{"x", "z", "test"},
						new String[]{"2", "5", "4"}
				}));

		run();

        expectConstantValidation(0, 0, "x");
        expectConstantValidation(0, 1, "z");
        expectConstantValidation(0, 2, "test");
        expectConstantValidation(1, 0, "2");
        expectConstantValidation(1, 1, "5");
        expectConstantValidation(1, 2, "4");
	}

	@Test
	public void testSurplusRows() throws Exception {
		useTable(tr("x", "z", "hello"));

		fixture.setRecordReader(
				new FileRecordReaderMock(new String[][]{
						new String[]{"x", "z", "hello"},
						new String[]{"2", "5", "4"},
						new String[]{"2", "9", "7"}
				}));

		run();

        assertCounts(0, 2, 0, 0);

		assertThat(htmlAt(1, 0), containsString("surplus"));
		assertThat(htmlAt(1, 2), containsString("5"));
		assertThat(htmlAt(2, 0), containsString("surplus"));
		assertThat(htmlAt(2, 3), containsString("7"));
	}

	@Test
	public void testMissingRows() throws Exception {
		useTable(
				tr("x", "z", "hello"),
				tr("1", "2", "3"),
				tr("4", "5", "6"));

        fixture.setRecordReader(
				new FileRecordReaderMock(new String[][]{
						new String[]{"x", "z", "hello"},
				}));

		run();

        assertCounts(0, 2, 0, 0);

		assertThat(htmlAt(1, 0), containsString("missing"));
		assertThat(htmlAt(2, 0), containsString("missing"));
		assertThat(htmlAt(2, 2), containsString("5"));
	}

	@Test
	public void testEmptyTable() {
		useTable();

		fixture.setRecordReader(
            new FileRecordReaderMock(new String[][]{
                new String[]{"x", "z", "hello"},
            }));

		run();
        assertCounts(0, 1, 0, 0);
	}

}
