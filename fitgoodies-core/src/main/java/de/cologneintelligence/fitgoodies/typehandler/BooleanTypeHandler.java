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
 * TypeHandler which is able to process a {@code BigInteger}.
 */
public class BooleanTypeHandler extends TypeHandler<Boolean> {

	public BooleanTypeHandler(String convertParameter) {
		super(convertParameter);
	}

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}

	@Override
	public Boolean unsafeParse(String input) throws ParseException {
		return parseBool(input);
	}

	public static boolean parseBool(String input) {
		input = input.toLowerCase();

		if (input.equals("yes") || input.equals("true") || input.equals("1") || input.equals("on")) {
			return true;
		} else if (input.equals("no") || input.endsWith("false") || input.equals("0") || input.endsWith("off")) {
			return false;
		} else {
			throw new IllegalArgumentException("Cannot parse '" + input + "' as boolean");
		}
	}
}
