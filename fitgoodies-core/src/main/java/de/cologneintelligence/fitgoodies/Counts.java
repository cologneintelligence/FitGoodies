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

public class Counts {
	public int right = 0;
	public int wrong = 0;
	public int ignores = 0;
	public int exceptions = 0;

	public Counts(int right, int wrong, int ignores, int exceptions) {
		this.right = right;
		this.wrong = wrong;
		this.ignores = ignores;
		this.exceptions = exceptions;
	}

	public Counts() {
		this(0, 0, 0, 0);
	}

	public String toString() {
		return
				right + " right, " +
						wrong + " wrong, " +
						ignores + " ignored, " +
						exceptions + " exceptions";
	}

	public void tally(Counts source) {
		right += source.right;
		wrong += source.wrong;
		ignores += source.ignores;
		exceptions += source.exceptions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Counts counts = (Counts) o;

		return right == counts.right && wrong == counts.wrong && ignores == counts.ignores && exceptions == counts.exceptions;

	}

	@Override
	public int hashCode() {
		int result = right;
		result = 31 * result + wrong;
		result = 31 * result + ignores;
		result = 31 * result + exceptions;
		return result;
	}
}
