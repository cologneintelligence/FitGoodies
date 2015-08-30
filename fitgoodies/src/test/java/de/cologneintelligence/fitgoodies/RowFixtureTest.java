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

import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
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
		useTable(tr("col1", "col2", "col3"),
				tr("v1", "v2", "v3"),
				tr("v4", "v5", "v6"));

		fixture.query = new BusinessObject[]{
				new BusinessObject(1, "x", 3),
				new BusinessObject(8, "matched", 6)};

		String columns[] = {"x", "y", "z"};
		expectHeaders(columns);

		expectParseExpected(1, 0, 1);
		expectParseExpected(2, 0, 8);

		prepareGetComputed(0, columns[0], 1);
		prepareGetComputed(1, columns[0], 8);

		expectValidatorProcess(columns, 1, 0, 0);
		expectValidatorProcess(columns, 1, 1, 0);
		expectValidatorProcess(columns, 1, 2, 0);
		expectValidatorProcess(columns, 2, 0, 1);
		expectValidatorProcess(columns, 2, 1, 1);
		expectValidatorProcess(columns, 2, 2, 1);

		run();
	}

	@Test
	public void exceptionInQueryIsReported() {
		useTable(tr("col1"));

		expectHeaders("header");

		run();

		assertCounts(0, 0, 0, 1);
	}

	@Test
	public void allEqualsMultiLevel() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(3, "x", 6, 1, 2, 3),
				new BusinessObject(2, "y", 7, 1, 2, 3),
				new BusinessObject(2, "y", 6, 1, 2, 3),
				new BusinessObject(1, "x", 6, 1, 2, 3),
				new BusinessObject(1, "x", 3, 1, 2, 3)};

		useTable(
				tr("col1", "col2", "col3", "col4", "col5", "col6"),
				tr("v1", "v2", "v3", "v4", "v5", "v6"),
				tr("w1", "w2", "w3", "w4", "w5", "w6"),
				tr("x1", "x2", "x3", "x4", "x5", "x6"),
				tr("y1", "y2", "y3", "y4", "y5", "y6"),
				tr("z1", "z2", "z3", "z4", "z5", "z6"));


		String columns[] = {"x", "y", "z", "a", "b", "c"};
		expectHeaders(columns);

		expectParseExpected(1, 0, 1);
		expectParseExpected(1, 1, "parsed a");
		expectParseExpected(1, 2, 100);
		expectParseExpected(2, 0, 1);
		expectParseExpected(2, 1, "parsed a");
		expectParseExpected(2, 2, 101);
		expectParseExpected(3, 0, 2);
		expectParseExpected(3, 1, "parsed b");
		expectParseExpected(3, 2, 200);
		expectParseExpected(4, 0, 2);
		expectParseExpected(4, 1, "parsed b");
		expectParseExpected(4, 2, 201);
		expectParseExpected(5, 0, 3);

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
				expectValidatorProcess(columns, 1 + i, j, 4 - i);
			}
		}

		run();
	}

	@Test
	public void commentRowsAreIgnored() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(0, "v1", 0),
				new BusinessObject(0, "v2", 0)};

		useTable(
				tr("col1", "col2", "col3"),
				tr("v1", "v2", "v3"),
				tr("w1", "w2", "w3"));


		String columns[] = {"x", "", "z"};
		expectHeaders(columns);

		expectParseExpected(1, 0, 2);
		expectParseExpected(1, 2, 4);
		expectParseExpected(2, 0, 2);
		expectParseExpected(2, 2, 3);

		prepareGetComputed(0, columns[0], 2);
		prepareGetComputed(0, columns[2], 4);
		prepareGetComputed(1, columns[0], 2);
		prepareGetComputed(1, columns[2], 3);

		expectValidatorProcess(columns, 1, 0, 0);
		expectValidatorProcessNull(1, 1);
		expectValidatorProcess(columns, 1, 2, 0);
		expectValidatorProcess(columns, 2, 0, 1);
		expectValidatorProcessNull(2, 1);
		expectValidatorProcess(columns, 2, 2, 1);

		run();
	}

	@Test
	public void multipleSameLinesAreMatched() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(0, "v1", 0),
				new BusinessObject(0, "v2", 0)};

		useTable(
				tr("col1", "col2", "col3"),
				tr("v1", "v2", "v3"),
				tr("w1", "w2", "w3"));


		String columns[] = {"x", "y", "z"};
		expectHeaders(columns);

		expectParseExpected(1, 0, 2);
		expectParseExpected(1, 1, "a");
		expectParseExpected(1, 2, 4);
		expectParseExpected(2, 0, 2);
		expectParseExpected(2, 1, "a");
		expectParseExpected(2, 2, 4);

		prepareGetComputed(0, columns[0], 2);
		prepareGetComputed(0, columns[1], "a");
		prepareGetComputed(0, columns[2], 4);
		prepareGetComputed(1, columns[0], 2);
		prepareGetComputed(1, columns[1], "a");
		prepareGetComputed(1, columns[2], 4);

		expectValidatorProcess(columns, 1, 0, 0);
		expectValidatorProcess(columns, 1, 1, 0);
		expectValidatorProcess(columns, 1, 2, 0);
		expectValidatorProcess(columns, 2, 0, 1);
		expectValidatorProcess(columns, 2, 1, 1);
		expectValidatorProcess(columns, 2, 2, 1);

		run();
	}

	@Test
	public void exceptionsInGroupingIgnoreRestOfLine() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(0, "v1", 0),
				new BusinessObject(0, "v2", 0)};

		useTable(
				tr("col1", "col2", "col3"),
				tr("v1", "v2", "v3"),
				tr("w1", "w2", "w3"));


		String columns[] = {"x", "y", "z"};
		expectHeaders(columns);

		expectParseExpected(1, 0, 2);
		expectParseExpectedWithException(1, 1);
		expectParseExpected(2, 0, 2);
		expectParseExpected(2, 1, "a");

		prepareGetComputed(0, columns[0], 2);
		prepareGetComputed(0, columns[1], "a");
		prepareGetComputed(0, columns[2], 4);
		prepareGetComputed(1, columns[0], 2);
		prepareGetComputed(1, columns[1], "a");
		prepareGetComputed(1, columns[2], 42);
		prepareAppendSurplus(Integer.class, 2, "one");
		prepareAppendSurplus(String.class, "a", "two");
		prepareAppendSurplus(Integer.class, 42, "three");

		expectValidatorProcess(columns, 2, 0, 0);
		expectValidatorProcess(columns, 2, 1, 0);
		expectValidatorProcess(columns, 2, 2, 0);

		run();

		assertCounts(0, 1, 1, 1);
        assertThat(htmlAt(3, 0), containsString("surplus"));
		assertThat(htmlAt(3, 1), containsString("one"));
		assertThat(htmlAt(3, 3), containsString("three"));
	}

	@Test
	public void surplusIsMarked() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(0, "v1", 0),
				new BusinessObject(0, "v2", 0),
				null};

		useTable(tr("col1", "col2", "col3"));

		String columns[] = {"x", "y"};
		expectHeaders(columns);

		prepareGetComputed(0, columns[0], 2);
		prepareGetComputed(0, columns[1], "a");
		prepareGetComputed(1, columns[0], 4);
		prepareGetComputed(1, columns[1], "b");
		prepareAppendSurplus(Integer.class, 2, "one");
		prepareAppendSurplus(String.class, "a", "two");
		prepareAppendSurplus(Integer.class, 4, "four");
		prepareAppendSurplus(String.class, "b", "five");

		run();

		assertCounts(0, 3, 2, 0);
        assertThat(htmlAt(1, 0), containsString("surplus"));
		assertThat(htmlAt(1, 1), containsString("null"));
        assertThat(htmlAt(2, 0), containsString("surplus"));
        assertThat(htmlAt(2, 1), containsString("one"));
		assertThat(htmlAt(2, 2), containsString("two"));
		assertThat(htmlAt(3, 0), containsString("surplus"));
		assertThat(htmlAt(3, 1), containsString("four"));
		assertThat(htmlAt(3, 2), containsString("five"));

		verify(valueReceiverFactory, atLeast(0)).createReceiver(isNull(), any(String.class));
	}

	@Test
	public void arraysAreASupportedType() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(new Integer[]{1, 2, 3}, 4)
		};

		useTable(
				tr("col1", "col2"),
				tr("1, 2, 3", "4"));

		String columns[] = {"arr", "x"};
		expectHeaders(columns);

		expectParseExpected(1, 0, new Integer[]{1, 2, 3});
		prepareGetComputed(0, columns[0], new Integer[]{1, 2, 3});

		expectValidatorProcess(columns, 1, 0, 0);
		expectValidatorProcess(columns, 1, 1, 0);

		run();
	}

	@Test
	public void methodsAreSupported() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(new Integer[]{1, 2, 3}, 4)
		};

		useTable(
				tr("col1()"),
				tr("7"));

		String columns[] = {"value()"};
		expectHeaders(columns);

		expectParseExpected(1, 0, 7);
		prepareGetComputed(0, columns[0], 7);

		expectValidatorProcess(columns, 1, 0, 0);

		run();
	}

	@Test
	public void missingIsMarked() throws Exception {
		fixture.query = new BusinessObject[]{
				new BusinessObject(0, "v1", 0)};

		useTable(
            tr("col1", "col2", "col3", "col4"),
            tr("42", "v1", "0", "."),
            tr("43", "v2", "0", "."),
            tr("44", "v2", "0", "."));


		String columns[] = {"x", "y", "z", null};
		expectHeaders(columns);

		prepareGetComputed(0, columns[0], 2);
		prepareGetComputed(0, columns[1], "a");
		prepareGetComputed(0, columns[2], 4);

		expectMissing(1, 0, 2, "2");
		expectMissing(1, 1, "a", "a");
		expectMissing(1, 2, 4, "4");
		expectMissingComment(1, 3, null);
		expectMissing(2, 0, 2, "2");
		expectMissing(2, 1, "b", "b");
		expectMissing(2, 2, 8, "8");
		expectMissingComment(2, 3, null);
		expectMissing(3, 0, 8, "8");
		expectMissing(3, 1, "x", "x");
		expectMissing(3, 2, 8, "8");
		expectMissingComment(3, 3, "y");

		expectValidatorProcess(columns, 1, 0, 0);
		expectValidatorProcess(columns, 1, 1, 0);
		expectValidatorProcess(columns, 1, 2, 0);
		expectValidatorProcessNull(1, 3);

		run();

		assertCounts(0, 2, 0, 0);
		assertThat(htmlAt(1, 0), isEmptyString());
		assertThat(htmlAt(1, 1), is(equalTo("42")));
		assertThat(htmlAt(2, 0), containsString("missing"));
		assertThat(htmlAt(2, 1), containsString("2"));
		assertThat(htmlAt(2, 2), containsString("b"));
		assertThat(htmlAt(2, 3), containsString("8"));
		assertThat(htmlAt(2, 4), containsString("null"));
		assertThat(htmlAt(3, 0), containsString("missing"));
		assertThat(htmlAt(3, 1), containsString("8"));
		assertThat(htmlAt(3, 2), containsString("x"));
		assertThat(htmlAt(3, 3), containsString("8"));
		assertThat(htmlAt(3, 4), containsString("y"));
	}

	private void prepareAppendSurplus(Class<?> clazz, Object input, String result) {
		TypeHandler handler = prepareGetTypeHandler(clazz, null);
		when(handler.toString(input)).thenReturn(result);
	}

	private void expectValidatorProcess(String[] columns, int expectedRow,
	                                    int expectedColumn, int computedRow) throws Exception {
		final ValueReceiver valueReceiver = expectValueReceiverCreation(fixture.query[computedRow], columns[expectedColumn]);
		addValidationToExpectations(expectedRow, expectedColumn, valueReceiver);
	}

	private void expectValidatorProcessNull(int expectedRow, int expectedColumn) throws Exception {
		addValidationToExpectations(expectedRow, expectedColumn, null);
	}

	private void addValidationToExpectations(final int expectedRow, final int expectedColumn,
                                             final ValueReceiver valueReceiver) {
		expectations.add(new Task() {
			@Override
			public void run() throws Exception {
				verify(validator).process(cellAt(expectedRow, expectedColumn),
                    valueReceiver, null, typeHandlerFactory);
			}
		});
	}

	private void prepareGetComputed(int row, String column, Object result) throws Exception {
		ValueReceiver receiver = expectValueReceiverCreation(fixture.query[row], column);
		when(receiver.get()).thenReturn(result);
		when(receiver.getType()).thenReturn(result.getClass());
	}

	private void expectHeaders(String... header) {
		for (int i = 0; i < header.length; i++) {
			preparePreprocess(cellAt(0, i), header[i]);
		}
	}

	private void expectParseExpected(int row, int col, Object result) throws ParseException {
		@SuppressWarnings("RedundantStringConstructorCall")
		String s = new String();

        FitCell cell = cellAt(row, col);
		when(validator.preProcess(cell)).thenReturn(s);
		TypeHandler colHandler = prepareGetTypeHandler(result.getClass(), null);
		when(colHandler.parse(argThatSame(s))).thenReturn(result);
	}

	private void expectMissing(int row, int col, Object result, String toStringResult) throws ParseException {
		@SuppressWarnings("RedundantStringConstructorCall")
		String s = new String();

        FitCell cell = cellAt(row, col);
        when(validator.preProcess(cell)).thenReturn(s);
		TypeHandler colHandler = prepareGetTypeHandler(result.getClass(), null);
		when(colHandler.parse(argThatSame(s))).thenReturn(result);
		when(colHandler.toString(argThatSame(s))).thenReturn(toStringResult);
	}

	private void expectMissingComment(int row, int col, String toStringResult) throws ParseException {
        FitCell cell = cellAt(row, col);
        when(validator.preProcess(cell)).thenReturn(toStringResult);
	}

	private void expectParseExpectedWithException(int row, int col) {
        FitCell cell = cellAt(row, col);
        when(validator.preProcess(cell)).thenThrow(new RuntimeException("This was expected!"));
	}
}
