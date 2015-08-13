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


package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.file.readers.FileRecordReader;
import de.cologneintelligence.fitgoodies.valuereceivers.ConstantReceiver;

import java.io.IOException;

/**
 * This class takes a {@link de.cologneintelligence.fitgoodies.file.readers.FileRecordReader} and compares
 * the value of this reader with the content of the HTML table.
 */
public abstract class AbstractFileRecordReaderFixture extends AbstractFileReaderFixture {

	private FileRecordReader reader;

	/**
	 * Sets the underlying {@code FileRecordReader}.
	 *
	 * @param recordReader the reader to use
	 */
	public void setRecordReader(final FileRecordReader recordReader) {
		reader = recordReader;
	}

	@Override
	protected void doRow(final Parse row) {
		Parse cell = row.parts;

		while (cell != null) {
			if (reader.canRead()) {
				String actualValue = reader.nextField();
				ConstantReceiver receiver =
						new ConstantReceiver(actualValue, String.class);
				check(cell, receiver, null);
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
	protected void doRows(final Parse rows) {
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
