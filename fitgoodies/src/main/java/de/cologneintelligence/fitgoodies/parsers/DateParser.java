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

import de.cologneintelligence.fitgoodies.date.SetupHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import java.text.ParseException;
import java.util.Date;

/**
 * Parser which is able to convert a string into a {@code Date}.
 */
public class DateParser implements Parser<Date> {
	/**
	 * Parses a string and converts it into a {@code Date} object.
	 *
	 * @param s         {@code String} which will be converted
	 * @param parameter ignored
	 * @return {@code Date} object which is represented by {@code s}
	 */
	@Override
	public final Date parse(final String s, final String parameter) throws ParseException {
		SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
		return helper.parse(s, parameter);
	}

	/**
	 * Returns the destination class which is managed by this parser.
	 *
	 * @return BigInteger.class
	 */
	@Override
	public final Class<Date> getType() {
		return Date.class;
	}
}
