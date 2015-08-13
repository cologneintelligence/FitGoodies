/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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

package de.cologneintelligence.fitgoodies.util;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public final class WaitForResultTest extends FitGoodiesTestCase {

	public interface WaitForResultActor {
		@SuppressWarnings("unused")
		boolean method();
	}

	@Mock
	private SystemTime systemTime;

	@Mock
	private WaitForResultActor actor;

	private Method waitMethod;
	private WaitForResult waitForResult;

	@Before
	public void setUp() throws NoSuchMethodException {
		waitForResult = new WaitForResult(systemTime);
		waitMethod = WaitForResultActor.class.getMethod("method");
	}

	@Test
	public void trueIsForwarded() throws Exception {
		when(actor.method()).thenReturn(true);

		waitForResult.wait(actor, waitMethod, 1000, 100);

		assertThat(waitForResult.lastCallWasSuccessful(), is(true));
		assertThat(waitForResult.getLastElapsedTime(), is(0L));
	}

	@Test
	public void falseIsForwarded() throws Exception {
		when(actor.method()).thenReturn(false);

		int sleepTime = 100;
		long maxTime = 500L;

		waitForResult.wait(actor, waitMethod, maxTime, sleepTime);

		assertThat(waitForResult.lastCallWasSuccessful(), is(false));
		assertThat(waitForResult.getLastElapsedTime(), is(maxTime));

		verify(actor, times(6)).method();
		verify(systemTime, times(5)).sleep(sleepTime);
		verifyNoMoreInteractions(actor, systemTime);
	}

	@Test
	public void trueInTimeoutIsForwarded() throws Exception {
		when(actor.method()).thenReturn(false, false, false, false, true);

		waitForResult.wait(actor, waitMethod, 300, 50);

		assertThat(waitForResult.lastCallWasSuccessful(), is(true));
		assertThat(waitForResult.getLastElapsedTime(), is(200L));

		verify(actor, times(5)).method();
	}
}
