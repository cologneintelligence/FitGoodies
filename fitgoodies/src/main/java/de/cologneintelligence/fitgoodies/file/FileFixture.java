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

import de.cologneintelligence.fitgoodies.ActionFixture;

/**
 * The file fixture is used to select files via HTML:
 *
 * <table>
 * <tr><td>fitgoodies.file.FileFixture</td></tr>
 * <tr><td>directory</td><td>/my/dir</td></tr>
 * <tr><td>pattern</td><td>.*\.txt</td></tr>
 * <tr><td>encoding</td><td>utf-8</td></tr>
 * </table>
 *
 * The fixture sets the values in {@link FileFixtureHelper}.
 *
 * @see FileFixtureHelper FileFixtureHelper
 * @author jwierum
 * @version $Id$
 */
public class FileFixture extends ActionFixture {
	/**
	 * Sets the filename pattern to <code>pattern</code>.
	 * @param pattern pattern to use
	 */
	public final void pattern(final String pattern) {
		FileFixtureHelper.instance().setPattern(pattern);
	}

	/**
	 * Calls {@link #pattern(String)}, using the next cell as its parameter.
	 * @throws Exception propagated to fit
	 */
	public void pattern() throws Exception {
		transformAndEnter();
	}

	/**
	 * Calls {@link #directory(String)}, using the next cell as its parameter.
	 * @throws Exception propagated to fit
	 */
	public void directory() throws Exception {
		transformAndEnter();
	}

	/**
	 * Calls {@link #encoding(String)}, using the next cell as its parameter.
	 * @throws Exception propagated to fit
	 */
	public void encoding() throws Exception {
		transformAndEnter();
	}

	/**
	 * Sets the directory to <code>directory</code>.
	 * @param directory directory to use
	 */
	public final void directory(final String directory) {
		FileFixtureHelper.instance().setProvider(
				new FileSystemDirectoryProvider(directory));
	}

	/**
	 * Sets the encoding to <code>encoding</code>.
	 * @param encoding encoding to use
	 */
	public final void encoding(final String encoding) {
		FileFixtureHelper.instance().setEncoding(encoding);
	}
}
