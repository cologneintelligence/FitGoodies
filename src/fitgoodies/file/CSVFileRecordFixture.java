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

import fitgoodies.file.readers.CSVRecordReader;

/**
 * {@link AbstractFileRecordReaderFixture} implementation which processes CSV files
 * using a {@link fitgoodies.file.readers.CSVRecordReader}.
 * The fixture has two more parameters: delimiter and mask. The delimiter
 * defaults to comma, the mask defaults to the quotation mark.
 *
 * @author jwierum
 * @version $Id$
 */
public class CSVFileRecordFixture extends AbstractFileRecordReaderFixture {
	@Override
	public void setUp() throws Exception {
		super.setUp();

		char fieldDelimiter = getParam("delimiter", ",").charAt(0);
		char fieldMask = getParam("mask", "\"").charAt(0);

		setRecordReader(new CSVRecordReader(
				getFile().openBufferedReader(super.getEncoding()),
				fieldDelimiter, fieldMask));
	}
}
