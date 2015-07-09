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


package org.example;

import org.apache.log4j.Logger;

public final class ISBN {
	private final String isbn10;
	private final Logger logger = Logger.getLogger(ISBN.class);

	public ISBN(final String isbn) {
		isbn10 = isbn;
	}

	public String stripped() {
		return isbn10.replaceAll("-", "");
	}

	@Override
	public int hashCode() {
		return stripped().hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		} else if (!(other instanceof ISBN)) {
			return false;
		} else {
			return stripped().equals(((ISBN) other).stripped());
		}
	}

	public boolean isValid() {
		String s = isbn10.replaceAll("-", "");
		logger.debug("Validating: " + isbn10);

		char[] chars = new char[10];
		int sum = 0;

		if (s.length() != 10) {
			return false;
		}

		s.getChars(0, 10, chars, 0);

		for (int i = 0; i < 10; ++i) {
			if (chars[i] == 'X' && i == 9) {
				chars[i] = 10;
			} else {
				chars[i] -= '0';
			}

			sum = (sum + (i + 1) * chars[i]) % 11;
		}

		logger.debug("Result: " + sum);
		return sum == 0;
	}
}
