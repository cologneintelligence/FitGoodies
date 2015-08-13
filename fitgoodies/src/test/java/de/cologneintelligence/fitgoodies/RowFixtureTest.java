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

package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

public final class RowFixtureTest extends FitGoodiesFixtureTestCase<RowFixtureTest.TestRowFixture> {

	public static class BusinessObject {
		public Integer x;
		public String y;
		public Integer z;
		public Long a;
		public Long b;
		public Long c;

		public Integer[] arr;

		public BusinessObject(Integer arr[], int x) {
			this.arr = arr;
			this.x = x;
		}

		public BusinessObject(int x, String y, int z) {
			this(x, y, z, 0, 0, 0);
		}

		public BusinessObject(int x, String y, int z, long a, long b, long c) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public Integer value() {
			return z;
		}
	}


	public static class TestRowFixture extends RowFixture {

		public Class<?> targetClass = BusinessObject.class;
		public Object[] query;

		@Override
		public Class<?> getTargetClass() {
			return targetClass;
		}

		@Override
		public Object[] query() throws Exception {
			if (query == null) {
				throw new IllegalArgumentException("Expected");
			}
			return query;
		}
	}

	@Override
	protected Class<TestRowFixture> getFixtureClass() {
		return TestRowFixture.class;
	}

	@Test
	public void allEqualsSimple() throws Exception {
		Parse table = parseTable(
				tr("col1", "col2", "col3"),
				tr("v1", "v2", "v3"),
				tr("v4", "v5", "v6"));

		fixture.query = new BusinessObject[]{
				new BusinessObject(1, "x", 3),
				new BusinessObject(8, "matched", 6)};

		String columns[] = {"x", "y", "z"};
		expectHeaders(table, columns);

		expectParseExpected(table, 2, 0, 1);
		expectParseExpected(table, 3, 0, 8);

		prepareGetComputed(0, columns[0], 1);
		prepareGetComputed(1, columns[0], 8);

		expectValidatorProcess(table, columns, 2, 0, 0);
		expectValidatorProcess(table, columns, 2, 1, 0);
		expectValidatorProcess(table, columns, 2, 2, 0);
		expectValidatorProcess(table, columns, 3, 0, 1);
		expectValidatorProcess(table, columns, 3, 1, 1);
		expectValidatorProcess(table, columns, 3, 2, 1);

		fixture.doTable(table);
	}

	@Test
	public void exceptionInQueryIsReported() {
		Parse table = parseTable(tr("col1"));

		expectHeaders(table, "header");

		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 0, 0, 0, 1);
	}

	@Test
	public void allEqualsMultiLevel() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(3, "x", 6, 1, 2, 3),
				new BusinessObject(2, "y", 7, 1, 2, 3),
				new BusinessObject(2, "y", 6, 1, 2, 3),
				new BusinessObject(1, "x", 6, 1, 2, 3),
				new BusinessObject(1, "x", 3, 1, 2, 3)};

		Parse table = parseTable(
				tr("col1", "col2", "col3", "col4", "col5", "col6"),
				tr("v1", "v2", "v3", "v4", "v5", "v6"),
				tr("w1", "w2", "w3", "w4", "w5", "w6"),
				tr("x1", "x2", "x3", "x4", "x5", "x6"),
				tr("y1", "y2", "y3", "y4", "y5", "y6"),
				tr("z1", "z2", "z3", "z4", "z5", "z6"));


		String columns[] = {"x", "y", "z", "a", "b", "c"};
		expectHeaders(table, columns);

		expectParseExpected(table, 2, 0, 1);
		expectParseExpected(table, 2, 1, "parsed a");
		expectParseExpected(table, 2, 2, 100);
		expectParseExpected(table, 3, 0, 1);
		expectParseExpected(table, 3, 1, "parsed a");
		expectParseExpected(table, 3, 2, 101);
		expectParseExpected(table, 4, 0, 2);
		expectParseExpected(table, 4, 1, "parsed b");
		expectParseExpected(table, 4, 2, 200);
		expectParseExpected(table, 5, 0, 2);
		expectParseExpected(table, 5, 1, "parsed b");
		expectParseExpected(table, 5, 2, 201);
		expectParseExpected(table, 6, 0, 3);

		prepareGetComputed(4, columns[0], 1);
		prepareGetComputed(4, columns[1], "parsed a");
		prepareGetComputed(4, columns[2], 100);
		prepareGetComputed(3, columns[0], 1);
		prepareGetComputed(3, columns[1], "parsed a");
		prepareGetComputed(3, columns[2], 101);
		prepareGetComputed(2, columns[0], 2);
		prepareGetComputed(2, columns[1], "parsed b");
		prepareGetComputed(2, columns[2], 200);
		prepareGetComputed(1, columns[0], 2);
		prepareGetComputed(1, columns[1], "parsed b");
		prepareGetComputed(1, columns[2], 201);
		prepareGetComputed(0, columns[0], 3);

		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 6; ++j) {
				expectValidatorProcess(table, columns, 2 + i, j, 4 - i);
			}
		}

		fixture.doTable(table);
	}

	@Test
	public void commentRowsAreIgnored() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(0, "v1", 0),
				new BusinessObject(0, "v2", 0)};

		Parse table = parseTable(
				tr("col1", "col2", "col3"),
				tr("v1", "v2", "v3"),
				tr("w1", "w2", "w3"));


		String columns[] = {"x", "", "z"};
		expectHeaders(table, columns);

		expectParseExpected(table, 2, 0, 2);
		expectParseExpected(table, 2, 2, 4);
		expectParseExpected(table, 3, 0, 2);
		expectParseExpected(table, 3, 2, 3);

		prepareGetComputed(0, columns[0], 2);
		prepareGetComputed(0, columns[2], 4);
		prepareGetComputed(1, columns[0], 2);
		prepareGetComputed(1, columns[2], 3);

		expectValidatorProcess(table, columns, 2, 0, 0);
		expectValidatorProcessNull(table, 2, 1);
		expectValidatorProcess(table, columns, 2, 2, 0);
		expectValidatorProcess(table, columns, 3, 0, 1);
		expectValidatorProcessNull(table, 3, 1);
		expectValidatorProcess(table, columns, 3, 2, 1);

		fixture.doTable(table);
	}

	@Test
	public void multipleSameLinesAreMatched() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(0, "v1", 0),
				new BusinessObject(0, "v2", 0)};

		Parse table = parseTable(
				tr("col1", "col2", "col3"),
				tr("v1", "v2", "v3"),
				tr("w1", "w2", "w3"));


		String columns[] = {"x", "y", "z"};
		expectHeaders(table, columns);

		expectParseExpected(table, 2, 0, 2);
		expectParseExpected(table, 2, 1, "a");
		expectParseExpected(table, 2, 2, 4);
		expectParseExpected(table, 3, 0, 2);
		expectParseExpected(table, 3, 1, "a");
		expectParseExpected(table, 3, 2, 4);

		prepareGetComputed(0, columns[0], 2);
		prepareGetComputed(0, columns[1], "a");
		prepareGetComputed(0, columns[2], 4);
		prepareGetComputed(1, columns[0], 2);
		prepareGetComputed(1, columns[1], "a");
		prepareGetComputed(1, columns[2], 4);

		expectValidatorProcess(table, columns, 2, 0, 0);
		expectValidatorProcess(table, columns, 2, 1, 0);
		expectValidatorProcess(table, columns, 2, 2, 0);
		expectValidatorProcess(table, columns, 3, 0, 1);
		expectValidatorProcess(table, columns, 3, 1, 1);
		expectValidatorProcess(table, columns, 3, 2, 1);

		fixture.doTable(table);
	}

	@Test
	public void exceptionsInGroupingIgnoreRestOfLine() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(0, "v1", 0),
				new BusinessObject(0, "v2", 0)};

		Parse table = parseTable(
				tr("col1", "col2", "col3"),
				tr("v1", "v2", "v3"),
				tr("w1", "w2", "w3"));


		String columns[] = {"x", "y", "z"};
		expectHeaders(table, columns);

		expectParseExpected(table, 2, 0, 2);
		expectParseExpectedWithException(table, 2, 1);
		expectParseExpected(table, 3, 0, 2);
		expectParseExpected(table, 3, 1, "a");

		prepareGetComputed(0, columns[0], 2);
		prepareGetComputed(0, columns[1], "a");
		prepareGetComputed(0, columns[2], 4);
		prepareGetComputed(1, columns[0], 2);
		prepareGetComputed(1, columns[1], "a");
		prepareGetComputed(1, columns[2], 42);
		prepareAppendSurplus(Integer.class, 2, "one");
		prepareAppendSurplus(String.class, "a", "two");
		prepareAppendSurplus(Integer.class, 42, "three");

		expectValidatorProcess(table, columns, 3, 0, 0);
		expectValidatorProcess(table, columns, 3, 1, 0);
		expectValidatorProcess(table, columns, 3, 2, 0);

		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 0, 1, 1, 1);
		assertThat(table.at(0, 4, 0).body, allOf(containsString("surplus"), containsString("one")));
		assertThat(table.at(0, 4, 2).body, containsString("three"));
	}

	@Test
	public void surplusIsMarked() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(0, "v1", 0),
				new BusinessObject(0, "v2", 0),
				null};

		Parse table = parseTable(
				tr("col1", "col2", "col3"));

		String columns[] = {"x", "y"};
		expectHeaders(table, columns);

		prepareGetComputed(0, columns[0], 2);
		prepareGetComputed(0, columns[1], "a");
		prepareGetComputed(1, columns[0], 4);
		prepareGetComputed(1, columns[1], "b");
		prepareAppendSurplus(Integer.class, 2, "one");
		prepareAppendSurplus(String.class, "a", "two");
		prepareAppendSurplus(Integer.class, 4, "four");
		prepareAppendSurplus(String.class, "b", "five");

		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 0, 3, 2, 0);
		assertThat(table.at(0, 2, 0).body, allOf(containsString("surplus"), containsString("null")));
		assertThat(table.at(0, 3, 0).body, allOf(containsString("surplus"), containsString("one")));
		assertThat(table.at(0, 3, 1).body, containsString("two"));
		assertThat(table.at(0, 4, 0).body, allOf(containsString("surplus"), containsString("four")));
		assertThat(table.at(0, 4, 1).body, containsString("five"));

		verify(valueReceiverFactory, atLeast(0)).createReceiver(isNull(), any(String.class));
	}

	@Test
	public void arraysAreASupportedType() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(new Integer[]{1, 2, 3}, 4)
		};

		Parse table = parseTable(
				tr("col1", "col2"),
				tr("1, 2, 3", "4"));

		String columns[] = {"arr", "x"};
		expectHeaders(table, columns);

		expectParseExpected(table, 2, 0, new Integer[]{1, 2, 3});
		prepareGetComputed(0, columns[0], new Integer[]{1, 2, 3});

		expectValidatorProcess(table, columns, 2, 0, 0);
		expectValidatorProcess(table, columns, 2, 1, 0);

		fixture.doTable(table);
	}

	@Test
	public void methodsAreSupported() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(new Integer[]{1, 2, 3}, 4)
		};

		Parse table = parseTable(
				tr("col1()"),
				tr("7"));

		String columns[] = {"value()"};
		expectHeaders(table, columns);

		expectParseExpected(table, 2, 0, 7);
		prepareGetComputed(0, columns[0], 7);

		expectValidatorProcess(table, columns, 2, 0, 0);

		fixture.doTable(table);
	}

	@Test
	public void missingIsMarked() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(0, "v1", 0)};

		Parse table = parseTable(
				tr("col1", "col2", "col3", "col4"),
				tr("42", "v1", "0", "."),
				tr("43", "v2", "0", "."),
				tr("44", "v2", "0", "."));


		String columns[] = {"x", "y", "z", null};
		expectHeaders(table, columns);

		prepareGetComputed(0, columns[0], 2);
		prepareGetComputed(0, columns[1], "a");
		prepareGetComputed(0, columns[2], 4);

		expectMissing(table, 2, 0, 2, "2");
		expectMissing(table, 2, 1, "a", "a");
		expectMissing(table, 2, 2, 4, "4");
		expectMissingComment(table, 2, 3, null);
		expectMissing(table, 3, 0, 2, "2");
		expectMissing(table, 3, 1, "b", "b");
		expectMissing(table, 3, 2, 8, "8");
		expectMissingComment(table, 3, 3, null);
		expectMissing(table, 4, 0, 8, "8");
		expectMissing(table, 4, 1, "x", "x");
		expectMissing(table, 4, 2, 8, "8");
		expectMissingComment(table, 4, 3, "y");

		expectValidatorProcess(table, columns, 2, 0, 0);
		expectValidatorProcess(table, columns, 2, 1, 0);
		expectValidatorProcess(table, columns, 2, 2, 0);
		expectValidatorProcessNull(table, 2, 3);

		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 0, 2, 0, 0);
		assertThat(table.at(0, 3, 0).body, allOf(containsString("missing"), containsString("2")));
		assertThat(table.at(0, 3, 1).body, containsString("b"));
		assertThat(table.at(0, 3, 2).body, containsString("8"));
		assertThat(table.at(0, 3, 3).body, containsString("null"));
		assertThat(table.at(0, 4, 0).body, allOf(containsString("missing"), containsString("8")));
		assertThat(table.at(0, 4, 1).body, containsString("x"));
		assertThat(table.at(0, 4, 2).body, containsString("8"));
		assertThat(table.at(0, 4, 3).body, containsString("y"));
	}

	private void prepareAppendSurplus(Class<?> clazz, Object input, String result) {
		TypeHandler handler = prepareGetTypeHandler(clazz, null);
		when(handler.toString(input)).thenReturn(result);
	}

	private void expectValidatorProcess(Parse table, String[] columns, int expectedRow,
	                                    int expectedColumn, int computedRow) throws Exception {
		final ValueReceiver valueReceiver = expectValueReceiverCreation(fixture.query[computedRow], columns[expectedColumn]);
		addValidationToExpectations(table, expectedRow, expectedColumn, valueReceiver);
	}

	private void expectValidatorProcessNull(Parse table, int expectedRow, int expectedColumn) throws Exception {
		addValidationToExpectations(table, expectedRow, expectedColumn, null);
	}

	private void addValidationToExpectations(final Parse table, final int expectedRow, final int expectedColumn, final ValueReceiver valueReceiver) {
		expectations.add(new Task() {
			@Override
			public void run() throws Exception {
				verify(validator).process(table.at(0, expectedRow, expectedColumn), fixture.counts(),
						valueReceiver, null, typeHandlerFactory);
			}
		});
	}

	private void prepareGetComputed(int row, String column, Object result) throws Exception {
		ValueReceiver receiver = expectValueReceiverCreation(fixture.query[row], column);
		when(receiver.get()).thenReturn(result);
		when(receiver.getType()).thenReturn(result.getClass());
	}

	private void expectHeaders(Parse table, String... header) {
		for (int i = 0; i < header.length; i++) {
			preparePreprocess(table.at(0, 1, i), header[i]);
		}
	}

	private void expectParseExpected(Parse table, int row, int col, Object result) throws ParseException {
		@SuppressWarnings("RedundantStringConstructorCall")
		String s = new String();

		Parse cell = table.at(0, row, col);
		when(validator.preProcess(cell)).thenReturn(s);
		TypeHandler colHandler = prepareGetTypeHandler(result.getClass(), null);
		when(colHandler.parse(argThatSame(s))).thenReturn(result);
	}

	private void expectMissing(Parse table, int row, int col, Object result, String toStringResult) throws ParseException {
		@SuppressWarnings("RedundantStringConstructorCall")
		String s = new String();

		Parse cell = table.at(0, row, col);
		when(validator.preProcess(cell)).thenReturn(s);
		TypeHandler colHandler = prepareGetTypeHandler(result.getClass(), null);
		when(colHandler.parse(argThatSame(s))).thenReturn(result);
		when(colHandler.toString(argThatSame(s))).thenReturn(toStringResult);
	}

	private void expectMissingComment(Parse table, int row, int col, String toStringResult) throws ParseException {
		Parse cell = table.at(0, row, col);
		when(validator.preProcess(cell)).thenReturn(toStringResult);
	}

	private void expectParseExpectedWithException(Parse table, int row, int col) {
		Parse cell = table.at(0, row, col);
		when(validator.preProcess(cell)).thenThrow(new RuntimeException("This was expected!"));
	}
}
