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

public class ConstantReceiver extends ValueReceiver {
	private final Class valueType;
	private Object value;

	@SuppressWarnings("unchecked")
	public ConstantReceiver(Object value) {
		this(value, value.getClass());
	}

	public ConstantReceiver(Object value, Class valueType) {
		this.value = value;
		this.valueType = valueType;
	}

	@Override
	public Object get() {
		return value;
	}

	@Override
	public Class getType() {
		return valueType;
	}

	@Override
	public void set(Object target, Object fieldValue) {
		throw new UnsupportedOperationException("Cannot change constant value");
	}

	@Override
	public boolean canSet() {
		return false;
	}
}
