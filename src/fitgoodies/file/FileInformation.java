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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Represents a file and provides information about it.
 *
 * @author jwierum
 * @version $Id: FileInformation.java 185 2009-08-17 13:47:24Z jwierum $
 */
public abstract class FileInformation {
	/**
	 * The file's name.
	 * @return the filename
	 */
	public abstract String filename();

	/**
	 * The file's path.
	 * @return the path to the file without the filename itself.
	 */
	public abstract String pathname();

	/**
	 * The full path to the file.
	 * @return the file path and the filename
	 */
	public abstract String fullname();

	/**
	 * Opens the file and returns a <code>FileReader</code> object.
	 * @return the open file as <code>FileReader</code>
	 * @throws IOException thrown if the file could not be read
	 */
	public abstract FileReader openFileReader() throws IOException;

	/**
	 * Opens the file and returns a <code>InputStream</code> object.
	 * @return the open file as <code>InputStream</code>
	 * @throws IOException thrown if the file could not be read
	 */
	public abstract InputStream openInputStream() throws IOException;

	/**
	 * Opens the file and returns a <code>BufferedReader</code> object.
	 * The encoding is determined by asking {@link FileFixtureHelper#encoding()}.
	 * @return the open file as <code>BufferdReader</code>
	 * @throws IOException thrown if the file could not be read
	 */
	public BufferedReader openBufferedReader() throws IOException {
		return openBufferedReader(FileFixtureHelper.encoding());
	}

	/**
	 * Opens the file and returns a <code>BufferedReader</code> object using
	 * the given encoding.
	 * @param encoding the encoding to use
	 * @return the open file as <code>BufferdReader</code>
	 * @throws IOException thrown if the file could not be read
	 */
	public BufferedReader openBufferedReader(final String encoding) throws IOException {
		InputStream fis = openInputStream();
		InputStreamReader isr = new InputStreamReader(fis, encoding);
		return new BufferedReader(isr);
	}

	/**
	 * Alias to {@link #fullname()}.
	 * @return the file's path and name
	 */
	@Override
	public final String toString() {
		return fullname();
	}
}
