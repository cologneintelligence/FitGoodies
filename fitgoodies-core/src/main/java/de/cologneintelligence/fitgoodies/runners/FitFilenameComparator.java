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

package de.cologneintelligence.fitgoodies.runners;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Pattern;


/**
 * Compares two FileInformation objects. They are sorted in alphabetical order,
 * with two exceptions: files with the name &quot;setup.html&quot; are the first
 * file in the directory, files named &quot;teardown.html&quot; are processed as
 * the last item in a directory
 */
public abstract class FitFilenameComparator<T> implements Comparator<T>, Serializable {
	private static final long serialVersionUID = 2;

	private static final int RIGHT_IS_SMALLER = 1;
	private static final int LEFT_IS_SMALLER = -1;
	private static final int EQUAL = 0;

	private static final Pattern setupPattern =
			Pattern.compile("([\\\\/]|^)setup\\.html?$", Pattern.CASE_INSENSITIVE);

	private static final Pattern tearDownPattern =
			Pattern.compile("([\\\\//]|^)teardown\\.html?$", Pattern.CASE_INSENSITIVE);

	/**
	 * Compare two <code>FileInformation</code> objects. See {@link FitFilenameComparator}
	 * for more information.
	 *
	 * @param lhs left hand side
	 * @param rhs right hand side
	 * @return &lt; 0 if <code>lhs</code> is greater, &gt; 0 if <code>rhr</code>
	 * is greater, 0 if they are equal.
	 */
	@Override
	public final int compare(final T lhs, final T rhs) {

		int c = compareDirectoryNames(lhs, rhs);

		if (c == EQUAL) {
			if (isSetupFile(lhs) || isTearDownFile(rhs)) {
				return LEFT_IS_SMALLER;
			} else if (isSetupFile(rhs) || isTearDownFile(lhs)) {
				return RIGHT_IS_SMALLER;
			} else {
				return compareFullnames(lhs, rhs);
			}
		} else if (c <= LEFT_IS_SMALLER) {
			return subCompare(lhs, rhs);
		} else {
			return -subCompare(rhs, lhs);
		}
	}

	private int subCompare(final T lhs, final T rhs) {
		if (isSubDirectory(lhs, rhs) && isSetupFile(lhs)) {
			return LEFT_IS_SMALLER;
		} else if (isSubDirectory(lhs, rhs) && isTearDownFile(lhs)) {
			return RIGHT_IS_SMALLER;
		} else {
			return compareFullnames(lhs, rhs);
		}
	}

	private boolean isSubDirectory(final T lhs, final T rhs) {
		String leftDir = getParent(lhs);
		String rightDir = getParent(rhs);

		return leftDir.length() <= rightDir.length() && rightDir.startsWith(leftDir);
	}

	private int compareFullnames(final T lhs, final T rhs) {
		return getFile(lhs).getPath().compareTo(getFile(rhs).getPath());
	}

	private int compareDirectoryNames(final T lhs, final T rhs) {
		return getParent(lhs).compareTo(getParent(rhs));
	}

	private String getParent(T file) {
		String parent = getFile(file).getParent();
		return parent == null ? "" : parent;
	}

	private boolean isTearDownFile(final T fo) {
		return tearDownPattern.matcher(getFileName(fo)).find();
	}

	private boolean isSetupFile(final T fo) {
		return setupPattern.matcher(getFileName(fo)).find();
	}

	protected abstract File getFile(T file);

	protected abstract String getFileName(T file);
}
