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


package de.cologneintelligence.fitgoodies.parsers;

/**
 * Parser which is able to convert a string into a {@code String}.
 *
 */
public class StringParser implements Parser<String> {
	/**
	 * Parses a string and converts it into a {@code String} object.
	 * @param s {@code String} which will be converted
	 * @param parameter ignored
	 * @return {@code String} object which is represented by {@code s}
	 */
	@Override
	public final String parse(final String s, final String parameter) {
		return s;
	}

	/**
	 * Returns the destination class which is managed by this parser.
	 * @return BigInteger.class
	 */
	@Override
	public final Class<String> getType() {
		return String.class;
	}
}
