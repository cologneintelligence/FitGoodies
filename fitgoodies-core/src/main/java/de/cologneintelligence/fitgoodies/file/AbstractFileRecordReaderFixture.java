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

import de.cologneintelligence.fitgoodies.file.readers.FileRecordReader;
import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.htmlparser.FitRow;
import de.cologneintelligence.fitgoodies.htmlparser.FitTable;
import de.cologneintelligence.fitgoodies.valuereceivers.ConstantReceiver;

import java.io.IOException;
import java.util.List;

/**
 * This class takes a {@link de.cologneintelligence.fitgoodies.file.readers.FileRecordReader} and compares
 * the value of this reader with the content of the HTML table.
 */
public abstract class AbstractFileRecordReaderFixture extends AbstractFileReaderFixture {

	private FileRecordReader reader;
    private FitTable table;

    /**
	 * Sets the underlying {@code FileRecordReader}.
	 *
	 * @param recordReader the reader to use
	 */
	public void setRecordReader(final FileRecordReader recordReader) {
		reader = recordReader;
	}

    @Override
    public void doTable(FitTable table) {
        this.table = table;
        super.doTable(table);
    }

    @Override
	protected void doRow(final FitRow row) {
        // FIXME: introduce row parameters here...
        for (FitCell cell : row.cells()) {
			if (reader.canRead()) {
				String actualValue = reader.nextField();
				ConstantReceiver receiver =
						new ConstantReceiver(actualValue.trim(), String.class);
				check(cell, receiver, null);
			} else {
                row.wrong("missing");
			}
		}

		try {
			reader.nextRecord();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void doRows(final List<FitRow> rows) throws Exception {
		super.doRows(rows);

		while (reader.canRead()) {
            FitRow row = table.appendRow();
            row.wrong("surplus");

			String field = reader.nextField();
			while (field != null) {
				addCell(row, field);
				field = reader.nextField();
			}

			try {
				reader.nextRecord();
			} catch (IOException e) {
				row.exception(e);
				break;
			}
		}

		try {
			reader.close();
		} catch (IOException e) {
            table.exception(e);
		}
	}

	private void addCell(final FitRow row, final String field) {
        row.append().setDisplayValue(field);
	}
}
