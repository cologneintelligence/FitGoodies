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

import java.io.IOException;

import fit.Parse;
import fit.TypeAdapter;
import fitgoodies.file.readers.FileRecordReader;

/**
 * This class takes a {@link fitgoodies.file.readers.FileRecordReader} and compares
 * the value of this reader with the content of the HTML table.
 *
 * @author jwierum
 * @version $Id: AbstractFileRecordReaderFixture.java 185 2009-08-17 13:47:24Z jwierum $
 */
public abstract class AbstractFileRecordReaderFixture extends
		AbstractFileReaderFixture {

	/** for internal usage only - used to resolve cross references. */
	public String actualValue;

	private FileRecordReader reader;
	private final TypeAdapter typeAdapter;

	/**
	 * Initializes a new <code>AbstractFileRecordReaderFixture</code>.
	 */
	public AbstractFileRecordReaderFixture() {
		try {
			typeAdapter = TypeAdapter.on(this, this.getClass().getField("actualValue"));
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets the underlying <code>FileRecordReader</code>.
	 * @param recordReader the reader to use
	 */
	public final void setRecordReader(final FileRecordReader recordReader) {
		reader = recordReader;
	}

	@Override
	public void doRow(final Parse row) {
		Parse cell = row.parts;

		while (cell != null) {
			if (reader.canRead()) {
				actualValue = reader.nextField();
				check(cell, typeAdapter);
			} else {
				wrong(cell);
				info(cell, "(missing)");
			}
			cell = cell.more;
		}

		try {
			reader.nextRecord();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void doRows(final Parse rows) {
		if (rows == null) {
			throw new RuntimeException("Table must contain at least one row");
		}
		super.doRows(rows);

		Parse row = rows.last();
		while (reader.canRead()) {
			final Parse firstCell = new Parse("ignored", "", null, null);
			Parse cell = firstCell;

			String field = reader.nextField();
			while (field != null) {
				cell = addCell(cell, field);
				field = reader.nextField();
			}
			row.more = new Parse("tr", "", firstCell.more, null);
			row = row.more;

			try {
				reader.nextRecord();
			} catch (IOException e) {
				exception(row.more, e);
				break;
			}
		}

		try {
			reader.close();
		} catch (IOException e) {
			exception(rows.parts.more, e);
		}
	}

	private Parse addCell(final Parse cell, final String field) {
		final Parse newCell = new Parse("td", field, null, null);
		cell.more = newCell;
		wrong(newCell);
		info(newCell, "(surplus)");
		return newCell;
	}
}
