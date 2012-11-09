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


package de.cologneintelligence.fitgoodies.adapters;

import fit.TypeAdapter;

/**
 * Implementation of <code>AbstractTypeAdapter</code> which is able to
 * handle StringBuilder.
 *
 * @author jwierum
 * @version $Id$
 */
public class StringBuilderTypeAdapter extends AbstractTypeAdapter<StringBuilder> {
	/**
	 * Generates a new TypeAdapter based on <code>ta</code>.
	 *
	 * @param ta TypeAdapter to use as source
	 * @param parameter the given column/row parameter - ignored
	 */
	public StringBuilderTypeAdapter(final TypeAdapter ta, final String parameter) {
		super(ta, parameter);
	}

	/**
	 * Returns the data type handled by the class.
	 * @return StringBuilder.class
	 */
	@Override
	public final Class<StringBuilder> getType() {
		return StringBuilder.class;
	}

	/**
	 * Checks whether two StringBuilder <code>a</code> and <code>b</code> are
	 * equal.
	 *
	 * This method removes whitespaces around both strings first.
	 *
	 * @param a first StringBuilder
	 * @param b second StringBuilder
	 * @return true if they are equal or both are null, false otherwise
	 */
	@Override
	public final boolean equals(final Object a, final Object b) {
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
	 * @return <code>o</code> as a String
	 */
	@Override
	public final String toString(final Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString();
	}

	/**
	 * Converts a <code>String</code> into a <code>StringBuilder</code>.
	 * @param s the <code>String</code> to convert
	 * @return a <code>StringBuilder</code> which contains <code>s</code>
	 * @throws Exception if the string could not be parsed
	 */
	@Override
	public Object parse(final String s) throws Exception {
		return new StringBuilder(s);
	}
}
