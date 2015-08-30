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

import de.cologneintelligence.fitgoodies.htmlparser.FitCell;

public class PrimitiveFixture extends Fixture {

	// format converters ////////////////////////

	public static long parseLong(FitCell cell) {
		return Long.parseLong(cell.getFitValue());
	}

	public static double parseDouble(FitCell cell) {
		return Double.parseDouble(cell.getFitValue());
	}

	public static boolean parseBoolean(FitCell cell) {
		return Boolean.valueOf(cell.getFitValue());
	}

	// answer comparisons ///////////////////////

	public void check(FitCell cell, String value) {
		if (cell.getFitValue().equals(value)) {
			cell.right();
		} else {
			cell.wrong(value);
		}
	}

	public void check(FitCell cell, long value) {
		if (parseLong(cell) == value) {
			cell.right();
		} else {
			cell.wrong(Long.toString(value));
		}
	}

	public void check(FitCell cell, double value) {
		if (parseDouble(cell) == value) {
			cell.right();
		} else {
			cell.wrong(Double.toString(value));
		}
	}

	public void check(FitCell cell, boolean value) {
		if (parseBoolean(cell) == value) {
			cell.right();
		} else {
			cell.wrong(Boolean.toString(value));
		}
	}

}
