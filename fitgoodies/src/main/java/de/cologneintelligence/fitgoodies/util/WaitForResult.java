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

package de.cologneintelligence.fitgoodies.util;

import java.lang.reflect.Method;


/**
 * With this class is it possible to invoke a given method of a given class
 * by reflection until the method returns true or a given maxTime is reached.
 * <p/>
 * <p/>
 * The method must have the signature {@code boolean method()}.
 * <p/>
 *
 * @author kmussawisade
 */
public class WaitForResult {
	private final SystemTime systemTime;

	private long lastElapsedTime;
	private boolean lastCallWasSuccessful;

	/**
	 * Initializes a new object.
	 *
	 * @param systemTime SystemTime implementation to use
	 */
	public WaitForResult(final SystemTime systemTime) {
		this.systemTime = systemTime;
	}

	/**
	 * Initializes a new object. The object will use {@link SystemTime}.
	 */
	public WaitForResult() {
		this(new SystemTime());
	}

	private boolean invokeMethod(Object target, Method method) {
		try {
			return (boolean) method.invoke(target);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Invokes the method multiple times, either until it returns true, or
	 * until maxtime is over. After each invoke, the class waits
	 * {@code sleepTime} milliseconds.
	 */
	public void wait(Object target, Method method, long maxTime, long sleepTime) {
		long remaining = maxTime;

		lastCallWasSuccessful = invokeMethod(target, method);
		while (!lastCallWasSuccessful && remaining > 0) {
			systemTime.sleep(sleepTime);
			remaining -= sleepTime;
			lastCallWasSuccessful = invokeMethod(target, method);
		}

		lastElapsedTime = maxTime - remaining;
	}

	/**
	 * Gets the elapsed time of the last call to {@link #wait(Object, Method, long, long)}.
	 *
	 * @return the elapsed time in milliseconds
	 */
	public long getLastElapsedTime() {
		return lastElapsedTime;
	}

	public boolean lastCallWasSuccessful() {
		return lastCallWasSuccessful;
	}
}
