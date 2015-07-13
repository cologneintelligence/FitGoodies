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


package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.file.readers.FixedLengthRecordReader;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FixtureTools;
import fit.Parse;
import fit.TypeAdapter;

import java.io.IOException;

/**
 * {@link AbstractFileRecordReaderFixture} implementation which processes
 * files with fixed record length using a
 * {@link de.cologneintelligence.fitgoodies.file.readers.FixedLengthRecordReader}.
 * There is one optional parameter which correspondents to the newLineAtEOR
 * parameter of
 * {@link FixedLengthRecordReader#FixedLengthRecordReader(java.io.BufferedReader, int[], boolean)}.
 * It is called &quot;skipEOL&quot;.
 * <p>
 *
 * The first row must contain the field width of each field:
 * <table border="1" summary="">
 * <tr><td>fitgoodies.file.FixedLengthFileRecordFixture</td>
 * 		<td>file=/myfile.txt</td></tr>
 * <tr><td>3</td><td>10</td><td>25</td></tr>
 * <tr><td>1</td><td>record 1</td><td>more content</td></tr>
 * <tr><td>2</td><td>record 2</td><td>some text</td></tr>
 * </table>
 *
 *
 */
public class FixedLengthFileRecordFixture extends AbstractFileRecordReaderFixture {
	private boolean noeol;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		final String skipEOL = getParam("skipEOL", "0");
		noeol = false;
		if (skipEOL.equalsIgnoreCase("true")
				|| skipEOL.equalsIgnoreCase("yes")
				|| skipEOL.equalsIgnoreCase("0")) {
			noeol = true;
		}
	}

	@Override
	public void doRows(final Parse rows) {
		final int[] width = extractWidth(rows);

		if (width == null) {
			return;
		}

		try {
			setRecordReader(new FixedLengthRecordReader(
					getFile().openBufferedReader(super.getEncoding()),
					width, noeol));
		} catch (final IOException e) {
			exception(rows.more, e);
		}

		super.doRows(rows.more);
	}

	/**
	 * Parses the first table row and generates an <code>int</code> array
	 * which contains the length of each record field.
	 *
	 * @param row row to process
	 * @return length of each field
	 */
	public final int[] extractWidth(final Parse row) {
		Parse cell = row.parts;

		int cellCount = 0;
		while (cell != null) {
			++cellCount;
			cell = cell.more;
		}

		cell = row.parts;
		final int[] width = new int[cellCount];
		final CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
		for (int i = 0; i < cellCount; ++i) {
			try {
				FixtureTools.processCell(cell, TypeAdapter.on(this, Integer.class), this, helper);
				width[i] = Integer.parseInt(cell.text());
			} catch (final Exception e) {
				exception(cell, e);
				return null;
			}
			cell = cell.more;
		}
		return width;
	}
}
