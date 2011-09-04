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


package de.cologneintelligence.fitgoodies.file;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Helper class which is used to process pathes and directories.
 *
 * @author jwierum
 * @version $Id$
 */
public abstract class AbstractDirectoryHelper {
	/**
	 * Creates a all directories in <code>path</code>.
	 * @param path path to create
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	public abstract boolean mkDir(String path);

	/**
	 * Returns the separator char (normally, this is / or \).
	 * @return the used separator char
	 */
	public abstract String separator();

	/**
	 * Counts the number of directories in a given path.
	 * @param path the path to analyze
	 * @return number of directories
	 */
	public abstract int dirDepth(String path);

	/**
	 * Extracts the filename from a file path.
	 * @param path filename with path
	 * @return the filename
	 */
	public abstract String getFilename(String path);

	/**
	 * Extracts the directory component of a file path.
	 * @param file the file path to analyze
	 * @return the directory component
	 */
	public abstract String getDir(String file);

	/**
	 * Joins two paths. Note: at the moment, <code>path2</code> must not contain
	 * &quot;..&quot;.
	 * @param path1 first path
	 * @param path2 path to append
	 * @return the combined path
	 */
	public final String join(final String path1, final String path2) {
		if (path1.endsWith(separator())) {
			return path1 + path2;
		} else {
			return path1 + separator() + path2;
		}
	}

	/**
	 * Removes a prefix from a path. If the path does not start with the prefix,
	 * the unchanged path is returned.
	 * @param path the full path
	 * @param prefix prefix to remove
	 * @return <code>path</code> without prefix, or <code>path</code> itself
	 * 		unless it doesn't start with the prefix.
	 */
	public final String removePrefix(final String path, final String prefix) {
		String pathWithoutPrefix = path;

		if (pathWithoutPrefix.startsWith(prefix)) {
			pathWithoutPrefix = pathWithoutPrefix.substring(prefix.length());

			while (pathWithoutPrefix.startsWith(separator())) {
				pathWithoutPrefix = pathWithoutPrefix.substring(1);
			}
		}

		return pathWithoutPrefix;
	}

	/**
	 * Checks whether <code>subDir</code> is a sub directory of <code>parentDir</code>.
	 * Note: the comparison is case sensitive.
	 * @param subDir potential sub directory
	 * @param parentDir parent directory
	 * @return <code>true</code> if <code>subDir</code> is a subDirectory of
	 * 		<code>parentDir</code>, false otherwise.
	 */
	public final boolean isSubDir(final String subDir, final String parentDir) {
		String[] parts1 = getParts(subDir);
		String[] parts2 = getParts(parentDir);

		if (parentDir.equals("")) {
			return true;
		}

		if (parts1.length < parts2.length) {
			return false;
		} else {
			for (int i = 0; i < parts2.length; ++i) {
				if (!parts1[i].equals(parts2[i])) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns all directory paths between <code>fromDir</code> and <code>toDir</code>.
	 * @param fromDir directory to start from
	 * @param toDir last directory path
	 * @return an array of all generated paths
	 */
	public final String[] getParentDirs(final String fromDir, final String toDir) {
		ArrayList<String> result = new ArrayList<String>();

		String prefix = fromDir;
		if (!prefix.endsWith(separator()) && !prefix.isEmpty()) {
			prefix += separator();
		}

		String suffix = toDir.substring(prefix.length());
		if (suffix.endsWith(separator())) {
			suffix = suffix.substring(0, suffix.length() - 1);
		}

		String[] parts = getParts(suffix);
		StringBuilder fullPath = new StringBuilder(prefix);

		for (String part : parts) {
			fullPath.append(part);
			fullPath.append(separator());
			result.add(fullPath.toString());
		}

		return result.toArray(new String[]{});
	}

	/**
	 * Gets the longest common parent directory path of two pathes.
	 * @param dir1 first path
	 * @param dir2 second path
	 * @return longest common path in <code>dir1</code> and <code>dir2</code>
	 */
	public final String getCommonDir(final String dir1, final String dir2) {
		StringBuilder result = new StringBuilder();

		String[] parts1 = getParts(dir1);
		String[] parts2 = getParts(dir2);

		for (int i = 0; i < Math.min(parts1.length, parts2.length); ++i) {
			if (!parts1[i].equals(parts2[i])) {
				break;
			} else {
				result.append(parts1[i]);
				result.append(separator());
			}
		}
		return result.toString();
	}

	/**
	 * Checks whether a path is an absolute one.
	 * @param path path to analyze
	 * @return <code>true</code> if the given path is absolute
	 */
	public abstract boolean isAbsolutePath(String path);

	/**
	 * Converts an absolute path into a relative one.
	 * @param basePath path to start from
	 * @param absPath path to convert
	 * @return <code>absPath</code> as a relative path starting from <code>basePath</code>
	 */
	public final String abs2rel(final String basePath, final String absPath) {
		String[] baseParts = getParts(basePath);
		String[] absParts = getParts(absPath);

		StringBuilder result = new StringBuilder();

		if (!isAbsolutePath(absPath)) {
			return absPath;
		}

		// extract common prefix
		int start = 0;
		for (int i = 0; i < Math.min(baseParts.length, absParts.length); ++i) {
			if (baseParts[i].equals(absParts[i])) {
				start = i + 1;
			}
		}

		for (int i = start; i < baseParts.length; ++i) {
			if (result.length() > 0) {
				result.append(separator());
			}
			result.append("..");
		}

		for (int i = start; i < absParts.length; ++i) {
			if (result.length() > 0) {
				result.append(separator());
			}
			result.append(absParts[i]);
		}

		return result.toString();
	}

	private String[] getParts(final String path) {
		String newPath = path;
		if (newPath.startsWith(separator())) {
			newPath = newPath.substring(1);
		}

		if (newPath.endsWith(separator())) {
			newPath = newPath.substring(0, newPath.length() - 1);
		}

		return newPath.split(Pattern.quote(separator()));
	}

	/**
	 * Converts an relative path into an absolute one.
	 * @param basePath path to start from
	 * @param relPath path to convert
	 * @return <code>relPath</code> as a absolute path
	 */
	public final String rel2abs(final String basePath, final String relPath) {
		String[] baseParts = getParts(basePath);
		String[] relParts = getParts(relPath);

		if (isAbsolutePath(relPath)) {
			return relPath;
		}

		List<String> parts = new ArrayList<String>();
		for (int i = 0; i < baseParts.length; ++i) {
			if (i > 0 || !isWindowsDrive(basePath)) {
				parts.add(baseParts[i]);
			}
		}

		for (String part : relParts) {
			if (part.equals("..") && parts.size() > 0) {
				parts.remove(parts.size() - 1);
			} else if (!part.equals(".") && !part.equals("..")) {
				parts.add(part);
			}
		}

		StringBuilder result = new StringBuilder();
		if (isWindowsDrive(basePath)) {
			result.append(baseParts[0]);
		}

		for (String part : parts) {
			result.append(separator());
			result.append(part);
		}

		return result.toString();
	}

	private boolean isWindowsDrive(final String pathElement) {
		return pathElement.length() > 1	&& pathElement.charAt(1) == ':';
	}
}