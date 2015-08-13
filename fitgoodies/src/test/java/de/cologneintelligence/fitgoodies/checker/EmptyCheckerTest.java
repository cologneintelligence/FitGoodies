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

package de.cologneintelligence.fitgoodies.checker;

import de.cologneintelligence.fitgoodies.Counts;
import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class EmptyCheckerTest extends FitGoodiesTestCase {
	@Mock
	ValueReceiver valueReceiver;

	@Mock
	TypeHandler<String> typeHandler;

	@Test
	public void testCheckNullEqNull() throws Exception {

		int right = 1;
		int wrong = 0;

		check(right, wrong, null, null, true);
	}

	@Test
	public void testCheckEmptyEqNull() throws Exception {

		int right = 1;
		int wrong = 0;
		String getValue = "";

		check(right, wrong, getValue, null, true);
	}

	@Test
	public void testCheckNonNullEqNonNull() throws Exception {

		int right = 1;
		int wrong = 0;
		String getValue = "result";
		String cellValue = "bla";

		check(right, wrong, getValue, cellValue, false);
	}

	@Test
	public void testCheckValueNeqEmpty() throws Exception {

		int right = 0;
		int wrong = 1;
		String getValue = "result";
		String cellValue = "foobar";

		String text = check(right, wrong, getValue, cellValue, true);
		assertThat(text, containsString("foobar"));
	}

	@Test
	public void testCheckNullNeqNonEmpty() throws Exception {

		int right = 0;
		int wrong = 1;

		check(right, wrong, null, null, false);
	}

	@Test
	public void testException() throws Exception {
		final String message = "this was expected";

		when(valueReceiver.get()).thenThrow(new RuntimeException(message));

		Parse table = parseTable(tr("ok"));
		Counts counts = new Counts();
		new EmptyChecker(true).check(table.at(0, 1, 0), counts, null,
				valueReceiver, typeHandler);

		assertCounts(counts, table, 0, 0, 0, 1);
		assertThat(table.at(0, 1, 0).body, containsString(message));
	}

	protected String check(int right, int wrong, String getValue, String cellValue, boolean expectTrue) throws IllegalAccessException, InvocationTargetException {
		when(valueReceiver.get()).thenReturn(getValue);
		when(valueReceiver.getType()).thenReturn(String.class);
		when(typeHandler.toString(getValue)).thenReturn(cellValue);

		Parse table = parseTable(tr("ok"));
		Counts counts = new Counts();
		new EmptyChecker(expectTrue).check(table.at(0, 1, 0), counts, null,
				valueReceiver, typeHandler);

		assertCounts(counts, table, right, wrong, 0, 0);
		return table.at(0, 1, 0).body;
	}
}
