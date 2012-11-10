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

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class takes a filter pattern and a {@link DirectoryProvider} and returns
 * matching files.
 *
 * @author jwierum
 * @version $Id$
 */
public class FileSelector {
	private final SimpleRegexFilter filter;
	private final DirectoryProvider provider;

	/**
	 * Initializes a new selector.
	 * @param directoryProvider directory to use
	 * @param pattern filename filter pattern
	 */
	public FileSelector(final DirectoryProvider directoryProvider,
			final String pattern) {
		filter = new SimpleRegexFilter(pattern);
		this.provider = directoryProvider;
	}

	/**
	 * Returns the only file that matches the pattern. If no file matches, a
	 * <code>null</code>. If more than one file matches, a
	 * {@link FilenameNotUniqueException} is thrown.
	 * @return the matching file or <code>null</code> if no file matched
	 * @throws FilenameNotUniqueException more than one file matched the pattern
	 * @throws FileNotFoundException thrown, if the directory does not exist.
	 */
	public final FileInformation getUniqueFile()
			throws FilenameNotUniqueException, FileNotFoundException {
		Iterator<FileInformation> it = provider.getFiles();
		it = new FilterDirectoryIterator(it, filter);

		if (!it.hasNext()) {
			throw new FileNotFoundException();
		}

		FileInformation result = it.next();

		if (it.hasNext()) {
			throw new FilenameNotUniqueException(filter.getPattern());
		}
		return result;
	}

	/**
	 * Returns the first matching file.
	 * @return the matching file or <code>null</code> if no file matches
	 * @throws FileNotFoundException thrown, if the directory does not exist
	 */
	public final FileInformation getFirstFile() throws FileNotFoundException {
		Iterator<FileInformation> it = provider.getFiles();
		it = new FilterDirectoryIterator(it, filter);

		if (!it.hasNext()) {
			throw new FileNotFoundException();
		}

		return it.next();
	}

	/**
	 * Returns a list of all matching files.
	 * @return all files which match the pattern
	 */
	public final FileInformation[] getFiles()  {
		List<FileInformation> files = new LinkedList<FileInformation>();
		Iterator<FileInformation> it;

		try {
			it = provider.getFiles();
		} catch (FileNotFoundException e) {
			return new FileInformation[]{};
		}

		it = new FilterDirectoryIterator(it, filter);

		while (it.hasNext()) {
			files.add(it.next());
		}

		return files.toArray(new FileInformation[]{});
	}

	/**
	 * Returns the last matching file.
	 * @return the matching file or <code>null</code> if no file matches
	 * @throws FileNotFoundException thrown, if the directory does not exist
	 */
    public final FileInformation getLastFile() throws FileNotFoundException {
        Iterator<FileInformation> it = provider.getFiles();
        it = new FilterDirectoryIterator(it, filter);

        if (!it.hasNext()) {
            throw new FileNotFoundException();
        }

        FileInformation result = null;
        while (it.hasNext()) {
            result = it.next();
        }

        return result;
    }
}
