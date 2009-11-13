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


package fitgoodies.runners;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import fit.Counts;
import fit.Fixture;
import fit.Parse;

/**
 * Implementation of {@link FitResult} which replaces a Parse-Row with one
 * or more results. The fixture is used with {@link RunFixture}, which uses
 * a dummy cell. This dummy cell is preserved.
 *
 * @author jwierum
 * @version $Id$
 */
public final class FitParseResult implements FitResult {
	private final List<FileCount> results = new LinkedList<FileCount>();

	@Override
	public void print(final String directory, final OutputStream stream)
			throws IOException {
		Writer writer = new OutputStreamWriter(stream);
		BufferedWriter bufferedWriter = new BufferedWriter(writer);

		bufferedWriter.write("<table><tr><th colspan=\"2\">");
		bufferedWriter.write(directory);
		bufferedWriter.write("</th></tr>");

		for (FileCount fileCount : results) {
			bufferedWriter.write("<tr><td><a href=\"");
			bufferedWriter.write(fileCount.getFile());
			bufferedWriter.write("\">");
			bufferedWriter.write(fileCount.getFile());
			bufferedWriter.write("</a>");
			bufferedWriter.write("</td><td bgcolor=\"");
			bufferedWriter.write(color(fileCount.getCounts()));
			bufferedWriter.write("\">");
			bufferedWriter.write(fileCount.getCounts().toString());
			bufferedWriter.write("</td></tr>");
		}

		bufferedWriter.write("</table>");
		bufferedWriter.close();
		writer.close();
	}

	@Override
	public void put(final String file, final Counts result) {
		FileCount fileCount = new FileCount(file, result);
		if (results.contains(fileCount)) {
			results.remove(fileCount);
		}
		results.add(fileCount);
	}

	private String color(final Counts counts) {
		if (counts.wrong > 0 || counts.exceptions > 0) {
			return Fixture.red;
		} else {
			return Fixture.green;
		}
	}

	private Parse makeTable() {
		Parse result;
		try {
			result = new Parse("<td></td>", new String[]{"td"});
		} catch (ParseException e) {
			return null;
		}

		for (FileCount fileCount : results) {
			Parse row;
			try {
				row = new Parse("<tr><td></td><td></td></tr>",
						new String[]{"tr", "td"});
			} catch (ParseException e) {
				return null;
			}

			row.parts.body = "<a href=\"" + fileCount.getFile() + "\">"
				+ fileCount.getFile() + "</a>";
			row.parts.more.addToBody(fileCount.getCounts().toString());
			row.parts.more.addToTag(" bgcolor=\"" + color(fileCount.getCounts()) + "\"");

			result.last().more = row;
		}
		return result.more;
	}

	/**
	 * Replaces a line which one or more results.
	 * @param line the line to replace
	 */
	public void replaceLine(final Parse line) {
		Parse table = makeTable();

		table.last().more = line.more;
		line.more = table.more;
		line.parts.more = table.parts;
	}

	/**
	 * Gets the sum of all results.
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
