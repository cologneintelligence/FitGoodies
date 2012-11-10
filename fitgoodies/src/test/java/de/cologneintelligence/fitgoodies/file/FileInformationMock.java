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


/**
 *
 */
package de.cologneintelligence.fitgoodies.file;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import de.cologneintelligence.fitgoodies.file.FileInformation;

/**
 *
 * @author jwierum
 *
 */
public class FileInformationMock extends FileInformation {
	private final String filename;
	private final String directoryName;
	private final byte[] content;

	public FileInformationMock(final String dirname,
			final String file,
			final byte[] bs) {

		String dirWithPrefix = dirname;
		if (!dirname.endsWith("/")) {
			 dirWithPrefix = dirname + "/";
		}

		this.directoryName = dirWithPrefix;
		this.filename = file;
		this.content = bs;
	}

	@Override
	public final String filename() {
		return filename;
	}

	@Override
	public final String fullname() {
		return directoryName + filename;
	}

	@Override
	public final FileReader openFileReader() throws IOException {
		return null;
	}

	@Override
	public final InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public final String pathname() {
		return directoryName;
	}
}
