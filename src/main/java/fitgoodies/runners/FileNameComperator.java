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

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Pattern;

import fitgoodies.file.FileInformation;

/**
 * Compares two FileInformation objects. They are sorted in alphabetical order,
 * with two exceptions: files with the name &quot;setup.html&quot; are the first
 * file in the directory, files named &quot;teardown.html&quot; are processed as
 * the last item in a directory
 *
 * @author jwierum
 * @version $Id$
 */
public class FileNameComperator implements Comparator<FileInformation>,
	Serializable {

	private static final int RIGHT_IS_SMALLER = 1;
	private static final int LEFT_IS_SMALLER = -1;
	private static final int EQUAL = 0;

	private static final long serialVersionUID = -1560343564922070071L;

	private static final Pattern setupPattern =
		Pattern.compile("(" + Pattern.quote(Character.toString(File.separatorChar))
		+ "|^)setup\\.html?$",	Pattern.CASE_INSENSITIVE);

	private static final Pattern tearDownPattern =
		Pattern.compile("(" + Pattern.quote(Character.toString(File.separatorChar))
		+ "|^)teardown\\.html?$",	Pattern.CASE_INSENSITIVE);

	/**
	 * Compare two <code>FileInformation</code> objects. See {@link FileNameComperator}
	 * for more information.
	 *
	 * @param lhs left hand side
	 * @param rhs right hand side
	 * @return &lt; 0 if <code>lhs</code> is greater, &gt; 0 if <code>rhr</code>
	 * 		is greater, 0 if they are equal.
	 */
	@Override
	public final int compare(final FileInformation lhs,
			final FileInformation rhs) {

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

	private static int subCompare(
			final FileInformation lhs, final FileInformation rhs) {
		if (isSubDirectory(lhs, rhs) && isSetupFile(lhs)) {
			return LEFT_IS_SMALLER;
		} else if (isSubDirectory(lhs, rhs) && isTearDownFile(lhs)) {
			return 1;
		} else {
			return compareFullnames(lhs, rhs);
		}
	}

	private static boolean isSubDirectory(
			final FileInformation lhs, final FileInformation rhs) {
		String leftDir = lhs.pathname();
		String rightDir = rhs.pathname();

		return (leftDir.length() <= rightDir.length() && rightDir.startsWith(leftDir));
	}

	private static int compareFullnames(
			final FileInformation lhs, final FileInformation rhs) {
		return lhs.fullname().compareTo(rhs.fullname());
	}

	private static int compareDirectoryNames(
			final FileInformation lhs, final FileInformation rhs) {
		return lhs.pathname().compareTo(rhs.pathname());
	}

	private static boolean isTearDownFile(final FileInformation fo) {
		return tearDownPattern.matcher(fo.filename()).find();
	}

	private static boolean isSetupFile(final FileInformation fo) {
		return setupPattern.matcher(fo.filename()).find();
	}
}
