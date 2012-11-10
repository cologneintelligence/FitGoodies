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

import java.lang.reflect.Method;


/**
 * With this class is it possible to invoke a given method of a given class
 * by reflection until the method returns true or a given maxtime is reached.
 * <br /><br />
 *
 * The method must have the signature <code>boolean method()</code>.
 *
 * @author kmussawisade
 * @version $Id$
 */
public final class WaitForResult {
	private final SystemTime systemTime;
    private final Method method;
    private final Object actor;
    private final Long maxTime;
    private boolean lastCallWasSuccessful;
    private long sleepTime;
    private long lastElapsedTime;

    /**
     * Initializes a new object.
     * @param method method to execute
     * @param actor object which owns the method
     * @param maxTime the maximum time to wait
     * @param systemTime SystemTime implementation to use
     */
    public WaitForResult(final Method method, final Object actor,
    		final long maxTime, final SystemTime systemTime) {
    	this.systemTime = systemTime;
        this.method = method;
		this.actor = actor;
        this.maxTime = maxTime;
    }

    /**
     * Initializes a new object. The object will use {@link SystemTimeImpl}.
     * @param method method to execute
     * @param actor object which owns the method
     * @param maxTime the maximum time to wait
     */
    public WaitForResult(final Method method, final Object actor, final long maxTime) {
    	this(method, actor, maxTime, new SystemTimeImpl());
    }

    /**
     * Invokes the method. The result can be fetched with {@link #lastCallWasSuccessfull()};
     */
    public void invokeMethod() {
        try {
        	lastCallWasSuccessful = (Boolean) method.invoke(actor);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
    }

    /**
     * Invokes the method multiple times, either until it returns true, or
     * until maxtime is over. After each invoke, the class waits
     * <code>sleepTime</code> milliseconds.
     *
     * @see #setSleepTime(long) setSleepTime(long)
     */
    public void repeatInvokeWithTimeout() {
        long invokeTime = systemTime.currentSystemTimeInMS();
        boolean repeat = true;
        do  {
            invokeMethod();
            lastElapsedTime = elapsedSince(invokeTime);
            repeat = notFinished(maxTime - lastElapsedTime, lastCallWasSuccessful);
            waitIfRequired(repeat, sleepTime);
        } while (repeat);
    }

    private void waitIfRequired(final boolean repeat, final long sleepTime) {
        if (repeat) {
            systemTime.sleep(sleepTime);
        }
    }

    private long elapsedSince(final long invokeTime) {
        return systemTime.currentSystemTimeInMS() - invokeTime;
    }

    private boolean notFinished(final long deltaTime,
    		final boolean isReady) {
        return (deltaTime > 0 && !isReady);
    }

    /**
     * Sets the sleep time between two invokes.
     * @param sleepTime time between invokes
     */
    public void setSleepTime(final long sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * Returns the result of the last invoke. <br />
     * Note: it is possible, that both, the timeout and the invoke are true.
     * This is the case when the last invoke did return <code>true</code> and
     * exceeded the timeout in the same call.
     * @return the last result of {@link #repeatInvokeWithTimeout()}
     * and {@link #invokeMethod()}.
     */
	public boolean lastCallWasSuccessfull() {
		return lastCallWasSuccessful;
	}

	/**
	 * Gets the elapsed time of the last call to {@link #repeatInvokeWithTimeout()}.
	 * @return the elapsed time in milliseconds
	 */
	public long getLastElapsedTime() {
		return lastElapsedTime;
	}
}
