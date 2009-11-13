/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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


package fitgoodies.adapters;

import java.lang.reflect.InvocationTargetException;

import fit.TypeAdapter;

/**
 * This <code>TypeAdapter</code> encapsulates another <code>TypeAdapter</code>
 * and caches the return value. This is important, because it can be dangerous
 * to call a method multiple times. So the first result is cached.
 *
 * @author jwierum
 * @version $Id$
 */
public final class CachingTypeAdapter extends TypeAdapter {
	private final TypeAdapter parent;
	private Object cache;
	private boolean fetched;

	/**
	 * Creates a new caching <code>TypeAdapter</code>.
	 * The adapter <code>ta</code> becomes encapsulated, calls to {@link #get()}
	 * will be cached automatically.
	 * @param ta <code>TypeAdapter</code> to encapsulate
	 */
	public CachingTypeAdapter(final TypeAdapter ta) {
		this.field = ta.field;
		this.fixture = ta.fixture;
		this.method = ta.method;
		this.target = ta.target;
		this.type = ta.type;
		parent = ta;
	}

	private Object value() throws IllegalAccessException, InvocationTargetException {
		if (!fetched) {
			fetched = true;
			cache = parent.get();
		}
		return cache;
	}

	/**
	 * Delegates the call to the parent object.
	 * @param a object 1
	 * @param b object 2
	 * @return <code>parent.equals(a, b)</code>
	 */
	@Override
	public boolean equals(final Object a, final Object b) {
		return parent.equals(a, b);
	}

	/**
	 * Delegates the call to the parent object.
	 * @param obj other obejct
	 * @return <code>parent.equals(obj)</code>
	 */
	@Override
	public boolean equals(final Object obj) {
		return parent.equals(obj);
	}

	/**
	 * Returns the value of the parent object. The result is cached, so that
	 * multiple calls to <code>get()</code> do not result in multiple parent calls.
	 * @return <code>parent.get()</code>
	 * @throws IllegalAccessException thrown if the field could not be accessed
	 * @throws InvocationTargetException thrown if the target method could not be invoked
	 */
	@Override
	public Object get() throws IllegalAccessException,
			InvocationTargetException {
		return value();
	}

	/**
	 * Delegates the call to the parent object.
	 * @return <code>parent.hashCode()</code>
	 */
	@Override
	public int hashCode() {
		return parent.hashCode();
	}

	/**
	 * Delegates the call to the parent object.
	 * The result is <em>not</em> cached. Use {@link #get()} to read a cached
	 * value.
	 * @return <code>parent.invoke()</code>
	 * @throws IllegalAccessException thrown if the field could not be accessed
	 * @throws InvocationTargetException thrown if the target method could not be invoked
	 */
	@Override
	public Object invoke() throws IllegalAccessException,
			InvocationTargetException {
		return parent.invoke();
	}

	/**
	 * Delegates the call to the parent object.
	 * @param s String to parse
	 * @return <code>parent.parse(s)</code>
	 * @throws Exception thrown if the string could not be parsed
	 */
	@Override
	public Object parse(final String s) throws Exception {
		return parent.parse(s);
	}

	/**
	 * Delegates the call to the parent object.
	 * The cached value is reseted.
	 *
	 * @param value the value to set
	 * @throws IllegalAccessException thrown if the value could not be set
	 */
	@Override
	public void set(final Object value) throws IllegalAccessException {
		parent.set(value);
		fetched = false;
	}

	/**
	 * Delegates the call to the parent object.
	 * @return <code>parent.toString()</code>
	 */
	@Override
	public String toString() {
		return parent.toString();
	}

	/**
	 * Delegates the call to the parent object.
	 * @param o which will be represented as <code>String</code>
	 * @return <code>parent.toString(o)</code>
	 */
	@Override
	public String toString(final Object o) {
		return parent.toString(o);
	}
}
