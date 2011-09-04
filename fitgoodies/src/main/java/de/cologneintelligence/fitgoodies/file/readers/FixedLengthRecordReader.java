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


package de.cologneintelligence.fitgoodies.file.readers;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Reader which is capable to process files with fixed length records.
 *
 * @author jwierum
 * @version $Id$
 */
public class FixedLengthRecordReader implements FileRecordReader {
	private final BufferedReader reader;
	private final int[] width;
	private final boolean newLineAtEndOfRecord;
	private String[] parts;
	private int partIndex;

	/**
	 * Creates a new reader object.
	 * @param bufferedReader underlying stream to process
	 * @param fieldWidth array which describes the length of each column
	 * @param newLineAtEOR if a record is terminated with a newline, set
	 * 		this to <code>true</code>
	 * @throws IOException thrown if <code>bufferedReader</code> reports a problem
	 */
	public FixedLengthRecordReader(
			final BufferedReader bufferedReader,
			final int[] fieldWidth,
			final boolean newLineAtEOR) throws IOException {

		reader = bufferedReader;
		width = fieldWidth;
		newLineAtEndOfRecord = newLineAtEOR;

		parts = new String[width.length];
		nextRecord();
	}

	@Override
	public final void close() throws IOException {
		reader.close();
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
		for (int field = 0; field < width.length; ++field) {
			char[] buf = new char[width[field]];
			if (reader.read(buf) == -1) {
				parts = null;
				return false;
			}
			parts[field] = new String(buf);
		}

		if (newLineAtEndOfRecord) {
			reader.readLine();
		}

		partIndex = 0;
		return true;
	}


	@Override
	public final boolean canRead() {
		return parts != null;
	}
}
