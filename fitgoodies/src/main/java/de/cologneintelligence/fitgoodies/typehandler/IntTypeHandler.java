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

package de.cologneintelligence.fitgoodies.typehandler;

import java.text.ParseException;

public class IntTypeHandler extends TypeHandler<Integer> {
	public IntTypeHandler(String convertParameter) {
		super(convertParameter);
	}

	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}

	@Override
	public Integer parse(String input) throws ParseException {
		return Integer.parseInt(input);
	}
}
