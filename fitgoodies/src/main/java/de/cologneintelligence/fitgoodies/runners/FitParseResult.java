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

package de.cologneintelligence.fitgoodies.runners;

import de.cologneintelligence.fitgoodies.Counts;
import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.htmlparser.FitRow;
import de.cologneintelligence.fitgoodies.htmlparser.FitTable;
import de.cologneintelligence.fitgoodies.util.FitUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of {@link FitResult} which replaces a Parse-Row with one
 * or more results. The fixture is used with {@link RunFixture}, which uses
 * a dummy cell. This dummy cell is preserved.
 */

public final class FitParseResult implements FitResult {
	private final List<FileCount> results = new LinkedList<>();

	@Override
	public void print(final File directory, final OutputStream stream) throws IOException {
		try (Writer writer = new OutputStreamWriter(stream); BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

			bufferedWriter.write("<table><tr><th colspan=\"2\">");
			bufferedWriter.write(directory.getName());
			bufferedWriter.write("</th></tr>");

			for (FileCount fileCount : results) {
				bufferedWriter.write("<tr><td><a href=\"");
				String file = FitUtils.htmlSafeFile(fileCount.getFile());
				bufferedWriter.write(file);
				bufferedWriter.write("\">");
				bufferedWriter.write(file);
				bufferedWriter.write("</a>");
				bufferedWriter.write("</td><td bgcolor=\"");
				bufferedWriter.write(color(fileCount.getCounts()));
				bufferedWriter.write("\">");
				bufferedWriter.write(fileCount.getCounts().toString());
				bufferedWriter.write("</td></tr>");
			}

			bufferedWriter.write("</table>");
		}
	}

	@Override
	public void put(final File file, final Counts result) {
		FileCount fileCount = new FileCount(file, result);
		if (results.contains(fileCount)) {
			results.remove(fileCount);
		}
		results.add(fileCount);
	}

	private String color(final Counts counts) {
		if (counts.wrong > 0 || counts.exceptions > 0) {
			return FitUtils.HTML_RED;
		} else {
			return FitUtils.HTML_GREEN;
		}
	}

	private void addRows(FitTable table, int index) {
        for (int i = 0; i < results.size(); i++) {
            FileCount fileCount = results.get(i);
            FitRow row = table.insert(index + i);

            FitCell cell = row.append();
            cell.setDisplayValueRaw("<a href=\"" + FitUtils.htmlSafeFile(fileCount.getFile()) + "\">"
                + FitUtils.htmlSafeFile(fileCount.getFile()) + "</a>");

            cell = row.append();
            cell.setDisplayValue(fileCount.getCounts().toString());

            if (fileCount.getCounts().wrong > 0 || fileCount.getCounts().exceptions > 0) {
                cell.wrong();
            } else {
                cell.right();
            }
        }
	}

    /**
	 * Replaces a row with one or more results.
	 *
     * @param row the row to replace
     */
	public void insertAndReplace(final FitRow row) {
        if (results.isEmpty()) {
            return;
        }

        int index = row.getIndex();
        FitTable table = row.getTable();
        table.remove(index);
        addRows(table, index);
	}

	/**
	 * Gets the sum of all results.
	 *
	 * @return sum of all saved results
	 */
	public Counts getCounts() {
		Counts counts = new Counts();
		for (FileCount fileCount : results) {
			counts.tally(fileCount.getCounts());
		}
		return counts;
	}
}
