/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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
 * Interface which provides a parser.
 * @param <T> data type which the parser is responsible for.
 *
 * @author jwierum
 * @version $Id$
 */
public interface Parser<T> {
	/**
	 * converts <code>s</code> into an instance of <code>&lt;T&gt;</code>.
	 * @param s <code>String</code> which will be converted
	 * @param parameter <code>String</code> which is provided as parameter in the table header
	 * @return <code>&lt;T&gt;</code> object which is represented by <code>s</code>
	 * @throws Exception thrown, if the string could not be converted.
	 */
	T parse(String s, String parameter) throws Exception;

	/**
	 * Returns the data type which the parser is responsible for.
	 * @return data type which the parser is responsible for
	 */
	Class<T> getType();
}
