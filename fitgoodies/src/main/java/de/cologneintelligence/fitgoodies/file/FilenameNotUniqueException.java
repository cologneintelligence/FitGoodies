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

import java.io.IOException;

/**
 * This exception is thrown by {@link FileSelector#getUniqueFile()} if more than
 * one file matches the pattern.
 *
 */
public class FilenameNotUniqueException extends IOException {
	private static final long serialVersionUID = 6290009229751508492L;

	/**
	 * Generates the new exception.
	 * @param pattern the pattern which did not result in a unique match
	 */
	public FilenameNotUniqueException(final String pattern) {
		super("The pattern " + pattern + " matches more than one file");
	}
}
