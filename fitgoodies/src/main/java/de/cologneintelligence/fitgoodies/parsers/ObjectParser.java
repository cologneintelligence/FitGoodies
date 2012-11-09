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


package de.cologneintelligence.fitgoodies.parsers;

/**
 * Parser which is able to convert a string into an <code>Object</code>.
 * This class provides a fallback mechanism. If a destination Object has just
 * the type object, the parser compares their <code>toString()</code> value.
 *
 * @author jwierum
 * @version $Id$
 */
public class ObjectParser implements Parser<Object> {
	/**
	 * Returns the destination class which is managed by this parser.
	 * @return Object.class
	 */
	@Override
	public final Class<Object> getType() {
		return Object.class;
	}

	/**
	 * Parses a string and converts it into an <code>object</code>.
	 * @param s <code>String</code> which will be converted
	 * @param parameter ignored
	 * @return <code>s.toString()</code>
	 */
	@Override
	public final Object parse(final String s, final String parameter) {
		return s;
	}
}
