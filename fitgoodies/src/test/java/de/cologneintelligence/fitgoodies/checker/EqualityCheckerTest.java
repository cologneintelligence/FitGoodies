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

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class EqualityCheckerTest extends FitGoodiesTestCase {

	@Mock
	ValueReceiver valueReceiver;

	@Mock
	TypeHandler typeHandler;

	@Test
	public void equalsIsTrue() throws Exception {
		when(valueReceiver.get()).thenReturn("received");
		when(valueReceiver.getType()).thenReturn(String.class);
		when(typeHandler.parse("cellValue")).thenReturn("parsed");
		when(typeHandler.equals("parsed", "received")).thenReturn(true);

		useTable(tr("cellValue"));
		new EqualityChecker().check(cellAt(0, 0), "cellValue", valueReceiver, typeHandler);
        lastFitTable.finishExecution();

		assertCounts(1, 0, 0, 0);
	}

	@Test
	public void equalsIsTrue2() throws Exception {
		when(valueReceiver.get()).thenReturn(12L);
		when(valueReceiver.getType()).thenReturn(Long.class);
		when(typeHandler.parse("15")).thenReturn(18L);
		when(typeHandler.equals(18L, 12L)).thenReturn(true);

		useTable(tr("cellValue"));
        new EqualityChecker().check(cellAt(0, 0), "15", valueReceiver, typeHandler);
        lastFitTable.finishExecution();

		assertCounts(1, 0, 0, 0);
	}

	@Test
	public void equalsIsFalse() throws Exception {
		when(valueReceiver.get()).thenReturn(12L);
		when(valueReceiver.getType()).thenReturn(Long.class);
		when(typeHandler.parse("15")).thenReturn(18L);
        when(typeHandler.unsafeEquals(18L, 12L)).thenReturn(false);
		when(typeHandler.toString(12L)).thenReturn("expected");

		useTable(tr("cellValue"));
        new EqualityChecker().check(cellAt(0, 0), "15", valueReceiver, typeHandler);
        lastFitTable.finishExecution();

		assertCounts(0, 1, 0, 0);
		assertThat(htmlAt(0, 0), containsString("expected"));
	}

	@Test
	public void testException() throws Exception {
		when(valueReceiver.get()).thenThrow(new RuntimeException("test"));

		useTable(tr("ok"));
		new EqualityChecker().check(cellAt(0, 0), "15", valueReceiver, typeHandler);
        lastFitTable.finishExecution();

        assertCounts(0, 0, 0, 1);
		assertThat(htmlAt(0, 0), containsString("test"));
	}

	@Test
	public void nullValueReceiverIsIgnored() throws Exception {
		useTable(tr("ok"));
        new EqualityChecker().check(cellAt(0, 0), "15", null, typeHandler);
        lastFitTable.finishExecution();

		assertCounts(0, 0, 1, 0);
	}

}
