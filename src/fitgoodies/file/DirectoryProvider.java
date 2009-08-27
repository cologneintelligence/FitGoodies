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


package fitgoodies.file;

import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 * Interface which provides information about the content of a directory.
 *
 * @author jwierum
 * @version $Id: DirectoryProvider.java 185 2009-08-17 13:47:24Z jwierum $
 */
public interface DirectoryProvider {
	/**
	 * Returns all files in the directory.
	 * @return all files in the directory as an iterator
	 * @throws FileNotFoundException thrown if the directory does not exist
	 */
	Iterator<FileInformation> getFiles() throws FileNotFoundException;

	/**
	 * Returns all subdirectories in the directory.
	 * @return all files in the directory as an iterator
	 * @throws FileNotFoundException thrown if the directory does not exist
	 */
	Iterator<DirectoryProvider> getDirectories() throws FileNotFoundException;

	/**
	 * Returns the represented path.
	 * @return the path
	 */
	String getPath();
}
