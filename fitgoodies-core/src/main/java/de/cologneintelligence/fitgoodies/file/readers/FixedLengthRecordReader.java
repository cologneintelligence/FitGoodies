/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
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
 */
public class FixedLengthRecordReader implements FileRecordReader {
	private final BufferedReader reader;
	private final int[] width;
	private final boolean newLineAtEndOfRecord;
	private String[] parts;
	private int partIndex;

	/**
	 * Creates a new reader object.
	 *
	 * @param bufferedReader underlying stream to process
	 * @param fieldWidth     array which describes the length of each column
	 * @param newLineAtEOR   if a record is terminated with a newline, set
	 *                       this to {@code true}
	 * @throws IOException thrown if {@code bufferedReader} reports a problem
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
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public String nextField() {
		if (parts == null || partIndex >= parts.length) {
			return null;
		}

		String returnValue = parts[partIndex];
		++partIndex;
		return returnValue;
	}

	@Override
	public boolean nextRecord() throws IOException {
		for (int field = 0; field < width.length; ++field) {
            int toRead = width[field];
            char[] buf = new char[toRead];

            while (toRead > 0) {
                int read = reader.read(buf, width[field] - toRead, toRead);

                if (read == -1) {
                    parts = null;
                    return false;
                }

                toRead -= read;
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
	public boolean canRead() {
		return parts != null;
	}
}
