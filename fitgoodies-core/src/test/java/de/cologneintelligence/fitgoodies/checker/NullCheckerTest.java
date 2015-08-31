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

package de.cologneintelligence.fitgoodies.checker;

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class NullCheckerTest extends FitGoodiesTestCase {
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

		int right = 0;
		int wrong = 1;
		String getValue = "foo";

		check(right, wrong, getValue, "foo", true);
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

		useTable(tr("ok"));
		new NullChecker(true).check(cellAt(0, 0), null,
				valueReceiver, typeHandler);

        lastFitTable.finishExecution();

		assertCounts(0, 0, 0, 1);
		assertThat(htmlAt(0, 0), containsString(message));
	}

	protected String check(int right, int wrong, String getValue, String cellValue, boolean expectTrue) throws IllegalAccessException, InvocationTargetException {
		when(valueReceiver.get()).thenReturn(getValue);
		when(valueReceiver.getType()).thenReturn(String.class);
		when(typeHandler.toString(getValue)).thenReturn(cellValue);

		useTable(tr("ok"));
		new NullChecker(expectTrue).check(cellAt(0, 0), null, valueReceiver, typeHandler);
        lastFitTable.finishExecution();

		assertCounts(right, wrong, 0, 0);
		return htmlAt(0, 0);
	}

}
