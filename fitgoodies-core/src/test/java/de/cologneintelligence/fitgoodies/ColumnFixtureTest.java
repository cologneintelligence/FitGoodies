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

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;
import org.junit.Test;

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
		useTable(tr("a field", "a method?"), tr("1", "2"));

		expectFieldSet(1, 0, fixture, "a field", 5);
		expectMethodValidation(1, 1, "a method");

		run();
	}

	@Test
	public void testProcess1() throws Exception {
		useTable(tr("x", "y()", "field", "method?"),
				tr("-1", "2", "3", "4"),
				tr("a", "b", "val1", "val2"));

		expectFieldSet(1, 0, fixture, "x", 1);
		expectMethodValidation(1, 1, "y");
		expectFieldSet(1, 2, fixture, "field", 2);
		expectMethodValidation(1, 3, "method");
		expectFieldSet(2, 0, fixture, "x", 3);
		expectMethodValidation(2, 1, "y");
		expectFieldSet(2, 2, fixture, "field", 4);
		expectMethodValidation(2, 3, "method");

		run();
	}

	@Test
	public void missingColumnFieldThrowsException() throws Exception {
		useTable(
				tr("x", "bla"),
				tr("5", "test"),
				tr("3", "bla"));

		when(valueReceiverFactory.createReceiver(fixture, "x")).thenThrow(new NoSuchFieldException("x"));
		when(valueReceiverFactory.createReceiver(fixture, "bla")).thenThrow(new NoSuchFieldException("bla"));

		run();

		assertCounts(0, 0, 0, 2);
	}

	@Test
	public void executeExceptionWithoutMethodCallDoesNotStopExecution() throws Exception {
		fixture.throwExceptionInExecute = true;

		useTable(tr("col"), tr("1"));
		expectFieldSet(1, 0, fixture, "col", 5);

		run();

		assertCounts(0, 0, 0, 1);
	}

	@Test
	public void executeExceptionDoesNotStopExecution() throws Exception {
		fixture.throwExceptionInExecute = true;

		useTable(tr("col()"), tr("1"));
		expectMethodValidation(1, 0, "col");

		run();

		assertCounts(0, 0, 0, 1);
	}

	@Test
	public void exceptionInResetSkipsRow() throws Exception {
		fixture.throwExceptionInReset = true;

		useTable(tr("col"), tr("1"), tr("2"));
		expectFieldSet(2, 0, fixture, "col", 6);

		run();

		assertCounts(0, 0, 0, 1);
	}

	@Test
	public void exceptionInSetValueIsReported() throws Exception {
		useTable(tr("col"), tr("1"));
		expectValidationForFieldWithError(1, 0, "col", Long.class);

		run();

		assertCounts(0, 0, 0, 1);
	}


	protected void expectValidationForFieldWithError(final int x, final int y, String field, Class type) throws Exception {
		final ValueReceiver valueReceiver = expectFieldValueReceiverCreation(field, type);
		prepareGetTypeHandler(valueReceiver.getType(), null);
		when(validator.preProcess(cellThat(x, y))).thenThrow(new RuntimeException("expected"));
	}
}
