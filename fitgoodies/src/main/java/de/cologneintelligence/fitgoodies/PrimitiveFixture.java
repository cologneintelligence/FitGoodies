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

package de.cologneintelligence.fitgoodies;

public class PrimitiveFixture extends Fixture {

	// format converters ////////////////////////

	public static long parseLong(Parse cell) {
		return Long.parseLong(cell.text());
	}

	public static double parseDouble(Parse cell) {
		return Double.parseDouble(cell.text());
	}

	public static boolean parseBoolean(Parse cell) {
		return Boolean.valueOf(cell.text());
	}

	// answer comparisons ///////////////////////

	public void check(Parse cell, String value) {
		if (cell.text().equals(value)) {
			right(cell);
		} else {
			wrong(cell, value);
		}
	}

	public void check(Parse cell, long value) {
		if (parseLong(cell) == value) {
			right(cell);
		} else {
			wrong(cell, Long.toString(value));
		}
	}

	public void check(Parse cell, double value) {
		if (parseDouble(cell) == value) {
			right(cell);
		} else {
			wrong(cell, Double.toString(value));
		}
	}

	public void check(Parse cell, boolean value) {
		if (parseBoolean(cell) == value) {
			right(cell);
		} else {
			wrong(cell, Boolean.toString(value));
		}
	}

}
