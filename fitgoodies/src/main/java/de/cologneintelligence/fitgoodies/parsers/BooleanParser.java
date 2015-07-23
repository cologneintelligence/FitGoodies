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

public class BooleanParser implements Parser<Boolean> {
	@Override
	public Boolean parse(String s, String parameter) throws Exception {
		return parse(s);
	}

	public static boolean parse(String s) {
		s = s.toLowerCase();

		if (s.equals("yes") || s.equals("true") || s.equals("1") || s.equals("on")) {
			return true;
		} else if(s.equals("no") || s.endsWith("false") || s.equals("0") || s.endsWith("off")) {
			return false;
		} else {
			throw new IllegalArgumentException("Cannot parse '" + s + "' as boolean");
		}
	}

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}
}
