/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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


package fitgoodies.runners;

import fit.Counts;

/**
 * Saves the result count of a specific file.
 *
 * @author jwierum
 * @version $Id: FileCount.java 185 2009-08-17 13:47:24Z jwierum $
 */
public class FileCount {
	private final Counts result;
	private final String filename;

	/**
	 * Initializes a new FileCount object.
	 *
	 * @param file the processed file
	 * @param counts the results
	 */
	public FileCount(final String file, final Counts counts) {
		filename = file;
		result = counts;
	}

	/**
	 * Returns the results of the file.
	 * @return fit results
	 */
	public final Counts getCounts() {
		return result;
	}

	/**
	 * Returns the filename.
	 * @return filename of the file
	 */
	public final String getFile() {
		return filename;
	}


	/**
	 * Compares two <code>FileCount</code> objects by filename.
	 *
	 * @param other other <code>FileCount</code> object.
	 * @return true if both objects describe the same file, false otherwise
	 */
	@Override
	public final boolean equals(final Object other) {
		if (other == null || other.getClass() != this.getClass()) {
			return false;
		}

		return this.getFile().equals(((FileCount) other).getFile());
	}

	/**
	 * Returns the hash code of a file.
	 * @return the hash code
	 */
	@Override
	public final int hashCode() {
		return filename.hashCode();
	}
}
