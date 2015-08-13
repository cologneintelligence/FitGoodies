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

package de.cologneintelligence.fitgoodies.valuereceivers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodValueReceiver extends ValueReceiver {
	private final Method method;
	private final Object target;

	public MethodValueReceiver(Method method, Object target) {
		this.method = method;
		this.target = target;
	}

	@Override
	public Object get() throws IllegalAccessException, InvocationTargetException {
		return method.invoke(target);
	}

	@Override
	public Class<?> getType() {
		return method.getReturnType();
	}

	@Override
	public void set(Object target, Object fieldValue) throws IllegalAccessException {
		throw new UnsupportedOperationException("Cannot set value on method");
	}

	@Override
	public boolean canSet() {
		return false;
	}

	@Override
	public String toString() {
		return String.format("Method %s on %s", method.getName(), target.toString());
	}

	public Method getMethod() {
		return method;
	}
}
