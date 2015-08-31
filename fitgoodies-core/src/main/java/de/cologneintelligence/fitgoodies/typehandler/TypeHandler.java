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

package de.cologneintelligence.fitgoodies.typehandler;

import java.text.ParseException;

/**
 * The {@code AbstractTypeAdapter} is used to implement a custom
 * {@code TypeAdapter}. This adapter is called with another instance of
 * {@code TypeAdapter} which normally is responsible for
 * {@code Object}. The resulting adapter should be responsible only for
 * the generic type T.
 * <p>
 * It is recommended to override {@code toString}, {@code parse} and
 * {@code getType}.
 * <p>
 * A {@code TypeAdapter} must be registered using
 * {@code {@link TypeHandlerFactory#register(Class)}}
 * from code or using a {@code {@link de.cologneintelligence.fitgoodies.typehandler.SetupFixture}}
 * from HTML.
 *
 * @param <T> type the {@code AbstractTypeAdapter} is responsible for.
 */
public abstract class TypeHandler<T> {
	protected final String parameter;

	/**
	 * Constructs an AbstractTypeAdapter object.
	 *
	 * @param convertParameter parameter which is stored and can be used with
	 *                         {@code toString}/{@code parse} to customize the behavior.
	 */
	public TypeHandler(final String convertParameter) {
		parameter = convertParameter;
	}

	/**
	 * Returns the type of which this class is responsible for.
	 * The return Value should just be the type of T.
	 *
	 * @return the type of T.
	 */
	public abstract Class<T> getType();

	/**
	 * Converts an input of type {@code String} to the given target type.
	 *
	 * @param input input from table
	 * @return the parsed Object
     * @exception ParseException thrown if {@code input} cannot be parsed correctly.
	 */
	public abstract T unsafeParse(String input) throws ParseException;

	public T parse(String input) throws ParseException {
		if (input == null) {
			return null;
		} else {
			return unsafeParse(input);
		}
	}

	public boolean equals(T lhs, Object rhs) {
		if (lhs == null) {
			return rhs == null;
		} else {
			return rhs != null && unsafeEquals(lhs, rhs);
		}
	}

	public boolean unsafeEquals(T lhs, Object rhs) {
		return lhs.equals(rhs);
	}

	public String toString(T s) {
		return s.toString();
	}
}
