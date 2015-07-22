/*
 * Copyright (c) 2009-2015  Cologne Intelligence GmbH
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

package fit;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ColumnFixtureTest extends FitGoodiesTestCase {

	@SuppressWarnings("unused")
	public static class TestFixture1 extends ColumnFixture {
		int rowsCalled = 0;
		boolean resetCalled = false;

		public int x;

		public int y() {
			if (x < 0) throw new RuntimeException("test");
			return x + 1;
		}

		public String empty() {
			return "X";
		}

		@Override
		public void reset() {
			rowsCalled++;
		}

		@Override
		public void execute() {
			resetCalled = true;
		}
	}

	@Test
	public void emptyTableDoesNothing() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", "y()"));
		fixture.doTable(table);

		assertCounts(fixture.counts, table, 0, 0, 0, 0);
		assertThat(fixture.resetCalled, is(false));
		assertThat(fixture.rowsCalled, is(0));
	}

	@Test
	public void testOneRow() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", "y()"), tr("1", "2"));
		fixture.doTable(table);

		assertCounts(fixture.counts, table, 1, 0, 0, 0);
		assertThat(fixture.resetCalled, is(true));
		assertThat(fixture.rowsCalled, is(1));
	}

	@Test
	public void testTwoRows() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", "y()"), tr("1", "2"), tr("5", "5"));
		fixture.doTable(table);

		assertCounts(fixture.counts, table, 1, 1, 0, 0);
		assertThat(fixture.resetCalled, is(true));
		assertThat(fixture.rowsCalled, is(2));
	}

	@Test
	public void testEmptyCellsDoNotValidate() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("empty()"), tr(""), tr("X"));
		fixture.doTable(table);

		assertThat(table.at(0, 2, 0).body, containsString("X"));
		assertThat(table.at(0, 3, 0).body, is(equalTo("X")));
		assertCounts(fixture.counts, table, 1, 0, 0, 0);
	}

	@Test
	public void testException() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", "y()"), tr("-1", "2"));
		fixture.doTable(table);

		assertThat(table.at(0, 2, 1).body, containsString("Exception"));
		assertCounts(fixture.counts, table, 0, 0, 0, 1);
	}

	@Test
	public void emptyCellIsIgnored() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", ""), tr("5", "test"));
		fixture.doTable(table);

		assertThat(table.at(0, 2, 1).body, is(equalTo("test")));
		assertCounts(fixture.counts, table, 0, 0, 1, 0);
	}

	@Test
	public void missingColumnThrowsException() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", "bla"), tr("5", "test"), tr("3", "bla"));
		fixture.doTable(table);

		assertCounts(fixture.counts, table, 0, 0, 2, 1);
	}

}
