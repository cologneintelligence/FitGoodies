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
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Helper class which is used to process paths and directories.
 *
 */
public class FileSystemDirectoryHelper {
    /**
     * Removes a prefix from a path. If the path does not start with the prefix,
     * the unchanged path is returned.
     * @param path the full path
     * @param prefix prefix to remove
     * @return <code>path</code> without prefix, or <code>path</code> itself
     * 		unless it doesn't start with the prefix.
     */
    public String removePrefix(final String path, final String prefix) {
        String pathWithoutPrefix = path;

        if (pathWithoutPrefix.startsWith(prefix)) {
            pathWithoutPrefix = pathWithoutPrefix.substring(prefix.length());

            while (pathWithoutPrefix.startsWith(File.separator)) {
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
    public boolean isSubDir(final File subDir, final File parentDir) throws IOException {
        int parentDirLength = parentDir.getCanonicalFile().getAbsolutePath().length();

        File currentDir = subDir.getCanonicalFile().getAbsoluteFile();
        while (currentDir.getAbsolutePath().length() > parentDirLength) {
            currentDir = currentDir.getParentFile();
        }

        return currentDir.equals(parentDir.getAbsoluteFile());
    }

    /**
     * Returns all directory paths between <code>fromDir</code> and <code>toDir</code>.
     * @param fromDir directory to start from
     * @param toDir last directory path
     * @return an array of all generated paths
     */
    public File[] getParentDirs(final File fromDir, final File toDir) throws IOException {
        List<File> result = new LinkedList<File>();
        final File fromDirCanonical = fromDir.getCanonicalFile();
        for (File current = toDir.getCanonicalFile().getAbsoluteFile(); !current.equals(fromDirCanonical); current = current.getParentFile()) {
            result.add(0, current);
        }

        return result.toArray(new File[result.size()]);
    }

    /**
     * Gets the longest common parent directory path of two paths.
     * @param dir1 first path
     * @param dir2 second path
     * @return longest common path in <code>dir1</code> and <code>dir2</code>
     */
    public File getCommonDir(final File dir1, final File dir2) throws IOException {
        List<File> parts1 = getParentDirs(dir1);
        List<File> parts2 = getParentDirs(dir2);
        File matched = null;

        final int maxCommonSize = Math.min(parts1.size(), parts2.size());
        for (int i = 0; i < maxCommonSize; ++i) {
            if (parts1.get(i).equals(parts2.get(i))) {
                matched = parts1.get(i);
            } else {
                break;
            }
        }

        return matched;
    }

    private List<File> getParentDirs(File dir) throws IOException {
        List<File> dirs = new LinkedList<File>();

        File currentDir = dir.getCanonicalFile();
        File lastDir;
        do {
            dirs.add(0, currentDir);
            lastDir = currentDir;
            currentDir = currentDir.getParentFile();
        } while(currentDir != null && !currentDir.equals(lastDir));

        return dirs;
    }

    /**
     * Converts an absolute path into a relative one.
     * @param basePath path to start from
     * @param absPath path to convert
     * @return <code>absPath</code> as a relative path starting from <code>basePath</code>
     */
    public String abs2rel(final String basePath, final String absPath) {
        if (!isAbsolutePath(absPath)) {
            return absPath;
        }

        if (isWindowsDrive(absPath) && isWindowsDrive(basePath) && absPath.charAt(0) != basePath.charAt(0)) {
            return absPath;
        }

        StringBuilder result = new StringBuilder();
        String[] baseParts = getParts(basePath);
        String[] absParts = getParts(absPath);

        // extract common prefix
        int start = 0;
        for (int i = 0; i < Math.min(baseParts.length, absParts.length); ++i) {
            if (baseParts[i].equals(absParts[i])) {
                start = i + 1;
            }
        }

        for (int i = start; i < baseParts.length; ++i) {
            if (result.length() > 0) {
                result.append(File.separator);
            }
            result.append("..");
        }

        for (int i = start; i < absParts.length; ++i) {
            if (result.length() > 0) {
                result.append(File.separator);
            }
            result.append(absParts[i]);
        }

        return result.toString();
    }

    private boolean isAbsolutePath(String path) {
        return isWindowsDrive(path) || path.startsWith(File.separator);
    }

    private String[] getParts(final String path) {
        String newPath = path;
        if (newPath.startsWith(File.separator)) {
            newPath = newPath.substring(1);
        }

        if (newPath.endsWith(File.separator)) {
            newPath = newPath.substring(0, newPath.length() - 1);
        }

        return newPath.split(Pattern.quote(File.separator));
    }

    /**
     * Converts an relative path into an absolute one.
     * @param basePath path to start from
     * @param relPath path to convert
     * @return <code>relPath</code> as a absolute path
     */
    public File rel2abs(final String basePath, final String relPath) {
        String[] baseParts = getParts(basePath);
        String[] relParts = getParts(relPath);

        if (isAbsolutePath(relPath)) {
            return new File(relPath);
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
            result.append(File.separator);
            result.append(part);
        }

        return new File(result.toString());
    }

    private boolean isWindowsDrive(final String pathElement) {
        return pathElement.length() > 1	&& pathElement.charAt(1) == ':';
    }

    /**
     * Counts the number of directories in a given path.
     * @param path the path to analyze
     * @return number of directories
     */
    public int dirDepth(final File path) {
        final String stringPath = path.getPath();
        return stringPath.length() - stringPath.replaceAll(Pattern.quote(File.separator), "").length();
    }


    // FIXME: this method only exists for mocking in tests
    public File subdir(File baseDir, String file) {
        return new File(baseDir, file);
    }
}
