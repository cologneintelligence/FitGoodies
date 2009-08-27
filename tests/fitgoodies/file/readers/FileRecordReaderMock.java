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


package fitgoodies.file.readers;

public class FileRecordReaderMock implements FileRecordReader {
	private final String[][] table;
	private int row;
	private int column;

	public FileRecordReaderMock(final String[][] args) {
		table = args;
	}

	@Override
	public final void close() { }

	@Override
	public final String nextField() {
		String returnValue = null;
		if (column < table[row].length) {
			returnValue = table[row][column];
		}
		++column;
		return returnValue;
	}

	@Override
	public final boolean nextRecord() {
		++row;
		column = 0;
		return row < table.length;
	}

	@Override
	public final boolean canRead() {
		return row < table.length;
	}
}
