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

package de.cologneintelligence.fitgoodies.valuereceivers;

import java.lang.reflect.Field;

class FieldValueReceiver extends ValueReceiver {
	private final Field field;
	private final Object target;

	public FieldValueReceiver(Field field, Object target) {
		this.field = field;
		this.target = target;
	}

	@Override
	public Object get() throws IllegalAccessException {
		return field.get(target);
	}

	@Override
	public Class<?> getType() {
		return field.getType();
	}

	@Override
	public void set(Object target, Object fieldValue) throws IllegalAccessException {
		field.set(target, fieldValue);
	}

	@Override
	public boolean canSet() {
		return true;
	}

	Field getField() {
		return field;
	}
}
