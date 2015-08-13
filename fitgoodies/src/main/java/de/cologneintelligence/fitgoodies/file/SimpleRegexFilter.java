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


package de.cologneintelligence.fitgoodies.file;

import java.io.File;
import java.io.FileFilter;

/**
 * Simple filter that checks, whether the filename matches a given regex.
 *
 */
public class SimpleRegexFilter implements FileFilter {
	private final String regex;

	/**
	 * Constructs a new filter.
	 * @param filter pattern to use
	 */
	public SimpleRegexFilter(final String filter) {
		if (filter == null) {
			throw new RuntimeException("No pattern set");
		}
		regex = filter;
	}

	/**
	 * Returns the saved pattern.
	 * @return the used pattern
	 */
	public String getPattern() {
		return regex;
	}

	@Override
	public boolean accept(File pathname) {
		return pathname.isFile() && pathname.getName().matches(regex);
	}
}
