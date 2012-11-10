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


package de.cologneintelligence.fitgoodies.file.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Reader which is capable to process char-delimited files.
 *
 * @author jwierum
 * @version $Id$
 */
public class DelimiterRecordReader implements FileRecordReader {
	private String[] parts;
	private int partIndex;
	private final String delimiter;
	private final BufferedReader reader;

	/**
	 * Creates a new reader object.
	 * @param bufferedReader underlying stream to process
	 * @param fieldDelimiter field delimiter character
	 * @throws IOException thrown if <code>bufferedReader</code> reports a problem
	 */
	public DelimiterRecordReader(final BufferedReader bufferedReader,
			final String fieldDelimiter) throws IOException {

		delimiter = fieldDelimiter;
		this.reader = bufferedReader;

		nextRecord();
	}

	@Override
	public final String nextField() {
		if (parts == null || partIndex >= parts.length) {
			return null;
		}

		String returnValue = parts[partIndex];
		++partIndex;
		return returnValue;
	}

	@Override
	public final boolean nextRecord() throws IOException {
		String line;
		line = reader.readLine();

		if (line == null) {
			parts = null;
			return false;
		}

		parts = line.split(Pattern.quote(delimiter));
		partIndex = 0;
		return true;
	}

	@Override
	public final void close() throws IOException {
		reader.close();
	}

	@Override
	public final boolean canRead() {
		return parts != null;
	}
}
