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

import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;
import org.junit.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ColumnFixtureTest extends FitGoodiesFixtureTestCase<ColumnFixtureTest.TestFixture> {

	public static class TestFixture extends ColumnFixture {
		int rowsCalled = 0;
		boolean executeCalled = false;
		boolean throwExceptionInExecute = false;
		boolean throwExceptionInReset = false;

		@Override
		public void reset() throws Exception {
			rowsCalled++;
			super.reset();

			if (throwExceptionInReset) {
				throwExceptionInReset = false;
				throw new RuntimeException("expected");
			}
		}

		@Override
		public void execute() throws Exception {
			executeCalled = true;
			super.execute();

			if (throwExceptionInExecute) {
				throw new RuntimeException("expected");
			}
		}
	}


	@Override
	protected Class<TestFixture> getFixtureClass() {
		return TestFixture.class;
	}

	@Test
	public void testProcess2() throws Exception {
		final Parse table = parseTable(tr("a field", "a method?"), tr("1", "2"));

		expectFieldSet(table, 2, 0, fixture, "a field", 5);
		expectMethodValidation(table, 2, 1, fixture, "a method");

		fixture.doTable(table);
	}

	@Test
	public void testProcess1() throws Exception {
		Parse table = parseTable(tr("x", "y()", "field", "method?"),
				tr("-1", "2", "3", "4"),
				tr("a", "b", "val1", "val2"));

		expectFieldSet(table, 2, 0, fixture, "x", 1);
		expectMethodValidation(table, 2, 1, fixture, "y");
		expectFieldSet(table, 2, 2, fixture, "field", 2);
		expectMethodValidation(table, 2, 3, fixture, "method");
		expectFieldSet(table, 3, 0, fixture, "x", 3);
		expectMethodValidation(table, 3, 1, fixture, "y");
		expectFieldSet(table, 3, 2, fixture, "field", 4);
		expectMethodValidation(table, 3, 3, fixture, "method");

		fixture.doTable(table);
	}

	@Test
	public void missingColumnFieldThrowsException() throws Exception {
		final Parse table = parseTable(
				tr("x", "bla"),
				tr("5", "test"),
				tr("3", "bla"));

		when(valueReceiverFactory.createReceiver(fixture, "x")).thenThrow(new NoSuchFieldException("x"));
		when(valueReceiverFactory.createReceiver(fixture, "bla")).thenThrow(new NoSuchFieldException("bla"));

		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 0, 0, 0, 2);

		verify(valueReceiverFactory).createReceiver(fixture, "x");
		verify(valueReceiverFactory).createReceiver(fixture, "bla");
		verify(validator).process(table.at(0, 2, 0), fixture.counts(), null, null, typeHandlerFactory);
		verify(validator).process(table.at(0, 2, 1), fixture.counts(), null, null, typeHandlerFactory);
		verify(validator).process(table.at(0, 3, 0), fixture.counts(), null, null, typeHandlerFactory);
		verify(validator).process(table.at(0, 3, 1), fixture.counts(), null, null, typeHandlerFactory);
	}

	@Test
	public void executeExceptionWithoutMethodCallDoesNotStopExecution() throws Exception {
		fixture.throwExceptionInExecute = true;

		Parse table = parseTable(tr("col"), tr("1"));
		expectFieldSet(table, 2, 0, fixture, "col", 5);

		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 0, 0, 0, 1);
	}

	@Test
	public void executeExceptionDoesNotStopExecution() throws Exception {
		fixture.throwExceptionInExecute = true;

		Parse table = parseTable(tr("col()"), tr("1"));
		expectMethodValidation(table, 2, 0, fixture, "col");

		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 0, 0, 0, 1);
	}

	@Test
	public void exceptionInResetSkipsRow() throws Exception {
		fixture.throwExceptionInReset = true;

		Parse table = parseTable(tr("col"), tr("1"), tr("2"));
		expectFieldSet(table, 3, 0, fixture, "col", 6);

		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 0, 0, 0, 1);
	}

	@Test
	public void exceptionInSetValueIsReported() throws Exception {
		Parse table = parseTable(tr("col"), tr("1"));
		expectValidationForFieldWithError(table, 3, 0, "col", Long.class);

		fixture.doTable(table);

		assertCounts(fixture.counts(), table, 0, 0, 0, 1);
	}


	protected void expectValidationForFieldWithError(final Parse parse, final int x, final int y, String field, Class type) throws Exception {
		final ValueReceiver valueReceiver = expectFieldValueReceiverCreation(field, type);
		prepareGetTypeHandler(valueReceiver.getType(), null);

		expectations.add(new Task() {
			@Override
			public void run() throws IllegalAccessException {
				verify(validator).preProcess(cellThat(parse, x, y));
			}
		});

		when(validator.preProcess(cellThat(parse, x, y))).thenThrow(new RuntimeException("expected"));
	}
}
