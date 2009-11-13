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

import java.util.HashMap;
import java.util.Map;

import fit.TypeAdapter;

/**
 * Singleton helper class which manages all registered <code>TypeAdapter</code>s.
 *
 * @author jwierum
 * @version $Id$
 */
public final class TypeAdapterHelper {
	private final Map<Class<?>, Class<? extends AbstractTypeAdapter<?>>> adapters =
		new HashMap<Class<?>, Class<? extends AbstractTypeAdapter<?>>>();

	private TypeAdapterHelper() {
		register(StringBuilderTypeAdapter.class);
        register(StringBufferTypeAdapter.class);
        register(StringTypeAdapter.class);
        register(DateTypeAdapter.class);
        register(SQLDateTypeAdapter.class);
	}

	private static TypeAdapterHelper instance;

	/**
	 * Gets the instance of the class.
	 * @return an instance of TypeAdapterHelper
	 */
	public static TypeAdapterHelper instance() {
		if (instance == null) {
			instance = new TypeAdapterHelper();
		}
		return instance;
	}

	/**
	 * Resets the helper to default values.
	 */
	public static void reset() {
		instance = null;
	}

	/**
	 * Returns a new instance of <class>typeAdapter</class>, passing
	 * <class>baseTypeAdapter</class> as the only constructor argument.
	 *
	 * @param typeAdapter subclass of {@link AbstractTypeAdapter}
	 * 		which is used to create the new adapter.
	 * @param baseTypeAdapter constructor argument for the new object.
	 * @param parameter column/row parameter
	 * @return an instance of <code>typeAdapter</code>
	 */
	private AbstractTypeAdapter<?> instantiate(
			final Class<? extends AbstractTypeAdapter<?>> typeAdapter,
					final TypeAdapter baseTypeAdapter, final String parameter) {
		try {
			return typeAdapter.getConstructor(
					new Class<?>[]{TypeAdapter.class, String.class}).newInstance(
							baseTypeAdapter, parameter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Instantiates a new <code>TypeAdapter</code> which is capable of handling
	 * <code>typeAdapter.target.getClass()</code>.
	 *
	 * If no such adapter exists, the old one is returned.
	 *
	 * @param typeAdapter <code>TypeAdapter</code> to replace
	 * @param parameter Column/row parameter
	 * @return an instance of <code>TypeAdapter</code>, either a specialized one,
	 * 		or <code>typeAdapter</code> itself.
	 */
	public TypeAdapter getAdapter(final TypeAdapter typeAdapter,
			final String parameter) {
		if (typeAdapter.type.isArray()) {
			return new ArrayTypeAdapter(typeAdapter, parameter);
		} else if (adapters.containsKey(typeAdapter.type)) {
			return instantiate(adapters.get(typeAdapter.type), typeAdapter, parameter);
		} else {
			return typeAdapter;
		}
	}

	/**
	 * Registers a new <code>AbstractTypeAdapter</code>.
	 *
	 * After registering an adapter, {@link #getAdapter(TypeAdapter, String)}
	 * is able to return it.
	 * @param adapterClass subclass of <code>AbstractTypeAdapter</code> to register
	 */
	public void register(final Class<? extends AbstractTypeAdapter<?>> adapterClass) {
		TypeAdapter ta = new TypeAdapter();
		Class<?> targetType = ((AbstractTypeAdapter<?>)
				instantiate(adapterClass, ta, null)).getType();
		adapters.put(targetType, adapterClass);
	}
}
