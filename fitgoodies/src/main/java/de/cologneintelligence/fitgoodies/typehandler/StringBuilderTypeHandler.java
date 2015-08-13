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

/**
 * Implementation of {@code AbstractTypeAdapter} which is able to
 * handle StringBuilder.
 */
public class StringBuilderTypeHandler extends TypeHandler<StringBuilder> {
	/**
	 * Generates a new TypeAdapter based on {@code ta}.
	 *
	 * @param parameter the given column/row parameter - ignored
	 */
	public StringBuilderTypeHandler(final String parameter) {
		super(parameter);
	}

	/**
	 * Returns the data type handled by the class.
	 *
	 * @return StringBuilder.class
	 */
	@Override
	public final Class<StringBuilder> getType() {
		return StringBuilder.class;
	}

	/**
	 * Checks whether two StringBuilder {@code a} and {@code b} are
	 * equal.
	 * <p/>
	 * This method removes whitespaces around both strings first.
	 *
	 * @param a first StringBuilder
	 * @param b second StringBuilder
	 * @return true if they are equal or both are null, false otherwise
	 */
	public final boolean equals(final StringBuilder a, final StringBuilder b) {
		if (a == null) {
			return b == null;
		}
		if (b == null) {
			return false;
		}
		return a.toString().trim().equals(b.toString().trim());
	}

	/**
	 * Returns the String representation of an StringBuilder.
	 *
	 * @param o StringBuilder to convert
	 * @return {@code o} as a String
	 */
	public final String toString(final StringBuilder o) {
		if (o == null) {
			return "null";
		}
		return o.toString();
	}

	/**
	 * Converts a {@code String} into a {@code StringBuilder}.
	 *
	 * @param s the {@code String} to convert
	 * @return a {@code StringBuilder} which contains {@code s}
	 */
	@Override
	public StringBuilder unsafeParse(final String s) {
		return new StringBuilder(s);
	}
}
