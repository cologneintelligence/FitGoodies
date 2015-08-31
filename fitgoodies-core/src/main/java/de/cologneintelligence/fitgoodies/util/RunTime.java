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

package de.cologneintelligence.fitgoodies.util;

public class RunTime {
	long start = System.currentTimeMillis();
	long elapsed = 0;

	public String toString() {
		elapsed = (System.currentTimeMillis() - start);
		if (elapsed > 600000) {
			return d(3600000) + ":" + d(600000) + d(60000) + ":" + d(10000) + d(1000);
		} else {
			return d(60000) + ":" + d(10000) + d(1000) + "." + d(100) + d(10);
		}
	}

	String d(long scale) {
		long report = elapsed / scale;
		elapsed -= report * scale;
		return Long.toString(report);
	}
}
