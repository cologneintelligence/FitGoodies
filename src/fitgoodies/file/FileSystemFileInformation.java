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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Implementation of {@link FileInformation} which uses the file system.
 *
 * @author jwierum
 * @version $Id$
 */
public class FileSystemFileInformation extends FileInformation {
	private final String dir;
	private final String filename;

	/**
	 * Generates a new information object.
	 * @param directory parent directory of the file
	 * @param name name of the file
	 */
	public FileSystemFileInformation(final String directory, final String name) {
		String directoryWithPostfix = directory;
		if (!directoryWithPostfix.endsWith(File.separator)) {
			directoryWithPostfix += File.separator;
		}
		this.dir = directoryWithPostfix;
		this.filename = name;
	}

	@Override
	public final String filename() {
		return filename;
	}

	@Override
	public final String fullname() {
		return dir + filename;
	}

	@Override
	public final String pathname() {
		return dir;
	}

	@Override
	public final FileReader openFileReader() throws IOException {
		return new FileReader(fullname());
	}

	@Override
	public final FileInputStream openInputStream() throws IOException {
		return new FileInputStream(fullname());
	}
}
