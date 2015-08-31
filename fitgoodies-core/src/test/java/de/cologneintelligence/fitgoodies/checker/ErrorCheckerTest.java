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

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ErrorCheckerTest extends FitGoodiesTestCase {

	@Mock
	ValueReceiver valueReceiver;

	@Mock
	TypeHandler<String> typeHandler;

	@Test
	public void expectedErrorsAreRight() throws Exception {
		String message = "this was expected";

		when(valueReceiver.get()).thenThrow(new RuntimeException(message));

		useTable(tr("ok"));
		new ErrorChecker().check(cellAt(0, 0), null, valueReceiver, typeHandler);
        lastFitTable.finishExecution();

		assertCounts(1, 0, 0, 0);
		assertThat(htmlAt(0, 0), containsString(message));
	}

	@Test
	public void expectedErrorsAreRightNoMessage() throws Exception {
		when(valueReceiver.get()).thenThrow(new RuntimeException((String) null));

		useTable(tr("ok"));
        new ErrorChecker().check(cellAt(0, 0), null, valueReceiver, typeHandler);
        lastFitTable.finishExecution();

		assertCounts(1, 0, 0, 0);
	}

	@Test
	public void missedExceptionsAreWrong() throws Exception {
		when(valueReceiver.get()).thenReturn("text");
		when(valueReceiver.getType()).thenReturn(String.class);
		when(typeHandler.toString("text")).thenReturn("result!");

		useTable(tr("ok"));
        new ErrorChecker().check(cellAt(0, 0), null, valueReceiver, typeHandler);
        lastFitTable.finishExecution();

		assertCounts(0, 1, 0, 0);
		assertThat(htmlAt(0, 0), containsString("result!"));
	}
}
