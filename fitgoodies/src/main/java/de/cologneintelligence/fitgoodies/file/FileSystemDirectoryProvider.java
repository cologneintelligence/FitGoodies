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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides the content of a single directory in the file system.
 *
 * @author jwierum
 * @version $Id$
 */
public class FileSystemDirectoryProvider implements DirectoryProvider {
	private final String path;

	/**
	 * Initializes a new directory provider.
	 * @param directoryPath directory to provide
	 */
	public FileSystemDirectoryProvider(final String directoryPath) {
		String pathWithPostfix = directoryPath;
		if (!pathWithPostfix.endsWith(File.separator)) {
			pathWithPostfix += File.separator;
		}
		this.path = pathWithPostfix;
	}

	/**
	 * Returns all files in the directory.
	 * @return all files in the directory as an iterator
	 * @throws FileNotFoundException thrown if the directory does not exist
	 */
	@Override
	public final Iterator<FileInformation> getFiles() throws FileNotFoundException {
		File dir = new File(path);
		List<FileInformation> files = new LinkedList<FileInformation>();
		try {
		    String[] sortedFileNames = dir.list();
		    Arrays.sort(sortedFileNames);
			for (String file : sortedFileNames) {
				if (new File(path + file).isFile()) {
					files.add(new FileSystemFileInformation(dir.getAbsolutePath(), file));
				}
			}
		} catch (NullPointerException e) {
			throw new FileNotFoundException(path);
		}

		return files.iterator();
	}

	/**
	 * Returns all subdirectories in the directory.
	 * @return all files in the directory as an iterator
	 * @throws FileNotFoundException thrown if the directory does not exist
	 */
	@Override
	public final Iterator<DirectoryProvider> getDirectories() throws FileNotFoundException {
		File dir = new File(path);
		List<DirectoryProvider> dirs = new LinkedList<DirectoryProvider>();
		try {
			for (String file : dir.list()) {
				if (new File(path + file).isDirectory()) {
					dirs.add(new FileSystemDirectoryProvider(path + file));
				}
			}
		} catch (NullPointerException e) {
			throw new FileNotFoundException(path);
		}

		return dirs.iterator();
	}

	@Override
	public final String getPath() {
		return path;
	}
}
