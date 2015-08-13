package de.cologneintelligence.fitgoodies.valuereceivers;

import java.lang.reflect.InvocationTargetException;

// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

public abstract class ValueReceiver {
	public abstract Object get() throws IllegalAccessException, InvocationTargetException;
	public abstract Class getType();
	public abstract void set(Object target, Object fieldValue) throws IllegalAccessException;
	public abstract boolean canSet();

}
