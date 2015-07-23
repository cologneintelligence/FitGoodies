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

package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.processors.CrossReferenceProcessorMock;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
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

	@SuppressWarnings("unused")
	public static class NumberObjFixture extends ColumnFixture {
		public Integer testNr;
		public Integer testNrAtStart;

		public Integer number;

		public final Integer n() {
			return number;
		}

		@Override
		public void setUp() {
			testNrAtStart = testNr;
		}
	}

	public static class StringObjFixture extends ColumnFixture {
		public String string;

		public final String s() {
			if ("null".equals(string)) {
				return null;
			} else {
				return string;
			}
		}
	}

	private StringObjFixture stringObjFixture;
	private NumberObjFixture numberObjFixture;

	@Before
	public void setUp() throws Exception {
		stringObjFixture = new StringObjFixture();
		numberObjFixture = new NumberObjFixture();
	}

	@Test
	public void emptyTableDoesNothing() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", "y()"));
		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 0, 0, 0, 0);
		assertThat(fixture.resetCalled, is(false));
		assertThat(fixture.rowsCalled, is(0));
	}

	@Test
	public void testOneRow() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", "y()"), tr("1", "2"));
		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 1, 0, 0, 0);
		assertThat(fixture.resetCalled, is(true));
		assertThat(fixture.rowsCalled, is(1));
	}

	@Test
	public void testTwoRows() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", "y()"), tr("1", "2"), tr("5", "5"));
		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 1, 1, 0, 0);
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
		assertCounts(fixture.counts(), table, 1, 0, 0, 0);
	}

	@Test
	public void testException() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", "y()"), tr("-1", "2"));
		fixture.doTable(table);

		assertThat(table.at(0, 2, 1).body, containsString("Exception"));
		assertCounts(fixture.counts(), table, 0, 0, 0, 1);
	}

	@Test
	public void emptyCellIsIgnored() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", ""), tr("5", "test"));
		fixture.doTable(table);

		assertThat(table.at(0, 2, 1).body, is(equalTo("test")));
		assertCounts(fixture.counts(), table, 0, 0, 1, 0);
	}

	@Test
	public void missingColumnThrowsException() {
		TestFixture1 fixture = new TestFixture1();
		final Parse table = parseTable(tr("x", "bla"), tr("5", "test"), tr("3", "bla"));
		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 0, 0, 2, 1);
	}


	@Test
	public void testSimpleStringCases() {
		Parse table = parseTable(
				tr("string", "s()"),
				tr("x", "x</td></tr></table>"));
		stringObjFixture.doTable(table);
		assertThat(stringObjFixture.counts().right, is(equalTo((Object) 1)));

		table = parseTable(
				tr("string", "s()"),
				tr("x", "y</td></tr></table>"));
		stringObjFixture.doTable(table);
		assertThat(stringObjFixture.counts().wrong, is(equalTo((Object) 1)));
	}

	@Test
	public void testSimpleNumberCase() {
		Parse table = parseTable(
				tr("number", "n()"),
				tr("2", "2"));
		numberObjFixture.doTable(table);
		assertThat(numberObjFixture.counts().right, is(equalTo((Object) 1)));
	}

	@Test
	public void testCrossReferencesWithoutException() {
		Parse table = parseTable(
				tr("string", "s()"),
				tr("matched", "${test}"));

		CrossReferenceHelper helper = DependencyManager.getOrCreate(
				CrossReferenceHelper.class);
		helper.getProcessors().add(new CrossReferenceProcessorMock("test"));

		stringObjFixture.doTable(table);
		assertThat(stringObjFixture.counts().right, is(equalTo((Object) 1)));

		table = parseTable(
				tr("string", "s()"),
				tr("test2", "${test}"));
		stringObjFixture.doTable(table);
		assertThat(stringObjFixture.counts().wrong, is(equalTo((Object) 1)));
	}

	@Test
	public void testCrossReferencesWithException() {
		Parse table = parseTable(tr("string", "s()"),
				tr("x", "${nonEmpty()}"));

		stringObjFixture.doTable(table);
		assertThat(stringObjFixture.counts().wrong, is(equalTo((Object) 0)));
		assertThat(stringObjFixture.counts().right, is(equalTo((Object) 1)));

		table = parseTable(tr("string", "s()"),
				tr("null", "${nonEmpty()}"));

		stringObjFixture.doTable(table);
		assertThat(stringObjFixture.counts().wrong, is(equalTo((Object) 1)));
		assertThat(stringObjFixture.counts().right, is(equalTo((Object) 1)));
		assertThat(table.parts.more.more.parts.more.text(), containsString("!"));

		table = parseTable(
				tr("number", "n()"),
				tr("2", "${empty()}"));
		numberObjFixture.doTable(table);

		assertThat(numberObjFixture.counts().wrong, is(equalTo((Object) 1)));
		assertThat(numberObjFixture.counts().exceptions, is(equalTo((Object) 0)));
	}

	@Test
	public void testUpWithErrors() throws Exception {
		Parse table = parseTable(tr("x"));

		ColumnFixture fixture = new ColumnFixture() {
			@Override
			public void setUp() {
				throw new RuntimeException("x");
			}
		};
		fixture.doTable(table);

		assertThat(fixture.counts().right, is(equalTo((Object) 0)));
		assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 1)));
	}

	@Test
	public void testGetParams() {
		stringObjFixture.setParams(new String[]{"x=y", "a=b"});

		assertThat(stringObjFixture.getArg("x"), is(equalTo("y")));
		assertThat(stringObjFixture.getArg("y"), is(nullValue()));

		assertThat(stringObjFixture.getArg("a", "z"), is(equalTo("b")));
		assertThat(stringObjFixture.getArg("u", "z"), is(equalTo("z")));
	}

	@Test
	public void testInit() {
		Parse table = parseTable(
				tr("number", "n()"),
				tr("1", "1"));

		numberObjFixture.setParams(new String[]{"testNr = 9"});
		numberObjFixture.doTable(table);

		assertThat(numberObjFixture.testNrAtStart, is(equalTo(9)));
	}

	@Test
	public void testSetValue() {
		Parse table = parseTable(
				tr("number", "n()"),
				tr("2", "${tests.put(x)}"),
				tr("${tests.get(x)}", "2"));
		numberObjFixture.doTable(table);
		assertThat(numberObjFixture.counts().right, is(equalTo((Object) 2)));
		assertThat(numberObjFixture.counts().exceptions, is(equalTo((Object) 0)));
	}


	@Test
	public void testColumnParameters() throws Exception {
		Parse table = parseTableWithoutAnnotation(
				tr("x[1 2]", "y[3 4]", "z"),
				tr("a[7]", "b", "c"));

		String[] actual = new ColumnFixture().extractColumnParameters(table.parts);

		assertThat(Arrays.asList("1 2", "3 4", null), is(equalTo(Arrays.asList(actual))));
		assertThat(table.parts.parts.text(), is(equalTo("x")));
		assertThat(table.parts.parts.more.text(), is(equalTo("y")));
		assertThat(table.parts.more.parts.text(), is(equalTo("a[7]")));

		table = parseTableWithoutAnnotation(tr("name", "date [ de_DE, dd.MM.yyyy ] "));

		actual = new ColumnFixture().extractColumnParameters(table.parts);
		assertThat(Arrays.asList(null, "de_DE, dd.MM.yyyy"), is(equalTo(Arrays.asList(actual))));
		assertThat(table.parts.parts.text(), is(equalTo("name")));
		assertThat(table.parts.parts.more.text(), is(equalTo("date")));
	}
}
