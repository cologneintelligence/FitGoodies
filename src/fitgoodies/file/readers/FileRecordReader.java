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

import java.io.IOException;

/**
 * FileRecordReaders are used by {@link fitgoodies.file.AbstractFileRecordReaderFixture}
 * to process files with record sets.
 *
 * @version $Id: FileRecordReader.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public interface FileRecordReader {
	/**
	 * Returns the next field in the record set.
	 * @return next field's value, or <code>null</code>, if the last column has
	 * 		been reached.
	 */
	String nextField();

	/**
	 * Reads the next row.
	 * @return <code>true</code> if a record set could been read,
	 * 		<code>false</code> on end of file.
	 * @throws IOException when the underlying Stream reports an error
	 */
	boolean nextRecord() throws IOException;

	/**
	 * Closes the underlying stream.
	 * @throws IOException when the underlying Stream reports an error
	 */
	void close() throws IOException;

	/**
	 * Reports whether {@link #nextField()} can be called or whether no more
	 * record set is available.
	 * @return <code>true</code> if {@link #nextField()} will return values,
	 * 		<code>false</code> otherwise
	 */
	boolean canRead();
}
