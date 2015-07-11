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
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;


public final class WaitForResultTest extends FitGoodiesTestCase {
    public static final class Actor {
        private int counter = 0;
        public Boolean myTestMethod() {
            counter++;
            return counter > 11;
        }

		public int getCalls() {
			return counter;
		}

		public Boolean myTestMethodReturnsTrue() { return true; }
        public Boolean myTestMethodReturnsFalse() { return false; }
    }

    public static final class SystemTimeMock implements SystemTime {
    	private long curentSystemTime = 1000000L;
    	@Override
    	public long currentSystemTimeInMS() {
    		return curentSystemTime;
    	}
    	@Override
    	public void sleep(final long sleepTimeInMillis) {
    		curentSystemTime += sleepTimeInMillis;
    	}
    }

    @Test
    public void testWaitForConstructor() throws Exception {
        Method method = Actor.class.getMethod("myTestMethodReturnsTrue");
        WaitForResult waitForResult = new WaitForResult(method, new Actor(), 0L);
        assertThat(waitForResult, not(CoreMatchers.is(nullValue())));
    }

    @Test
    public void testInvokeMethodReturnsTrue() throws Exception {
        Method method = Actor.class.getMethod("myTestMethodReturnsTrue");
        WaitForResult waitForResult = new WaitForResult(method, new Actor(), 0L);
        waitForResult.invokeMethod();
        assertThat(waitForResult.lastCallWasSuccessful(), is(true));
    }

    @Test
    public void testInvokeMethodReturnsFalse() throws Exception {
        Method method = Actor.class.getMethod("myTestMethodReturnsFalse");
        WaitForResult waitForResult = new WaitForResult(method, new WaitForResultTest.Actor(), 0L);
        waitForResult.invokeMethod();
        assertThat(waitForResult.lastCallWasSuccessful(), is(false));

    }

    @Test
    public void testInvokeWithoutTimeout() throws Exception {
    	Actor actor = new Actor();
        Method method = actor.getClass().getMethod("myTestMethod");
        final long timeout = 2000L;

        WaitForResult waitForResult =
        	new WaitForResult(method, actor,
        			timeout, new SystemTimeMock());

        waitForResult.setSleepTime(20L);
        waitForResult.repeatInvokeWithTimeout();
        assertThat(actor.getCalls(), is(equalTo((Object) 12)));
        assertThat(waitForResult.getLastElapsedTime(), is(equalTo((Object) 220L)));
        assertThat(waitForResult.lastCallWasSuccessful(), is(true));
    }

    @Test
    public void testInvokeWithTimeout() throws Exception {
    	Actor actor = new Actor();
        Method method = actor.getClass().getMethod("myTestMethod");
        final long timeout = 51L;

        WaitForResult waitForResult =
        	new WaitForResult(method, actor,
        			timeout, new SystemTimeMock());

        waitForResult.setSleepTime(10L);
        waitForResult.repeatInvokeWithTimeout();
        assertThat(actor.getCalls(), is(equalTo((Object) 7)));
        assertThat(waitForResult.getLastElapsedTime(), is(equalTo((Object) 60L)));
        assertThat(waitForResult.lastCallWasSuccessful(), is(false));

    }
}
