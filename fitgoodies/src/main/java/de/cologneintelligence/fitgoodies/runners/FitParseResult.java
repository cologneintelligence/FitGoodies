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


package de.cologneintelligence.fitgoodies.runners;

import de.cologneintelligence.fitgoodies.Counts;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import de.cologneintelligence.fitgoodies.Parse;

import java.io.*;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import static de.cologneintelligence.fitgoodies.util.FixtureTools.htmlSafeFile;

/**
 * Implementation of {@link FitResult} which replaces a Parse-Row with one
 * or more results. The fixture is used with {@link RunFixture}, which uses
 * a dummy cell. This dummy cell is preserved.
 */
public final class FitParseResult implements FitResult {
    private final List<FileCount> results = new LinkedList<>();

    @Override
    public void print(final File directory, final OutputStream stream)
            throws IOException {
        try (Writer writer = new OutputStreamWriter(stream); BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            bufferedWriter.write("<table><tr><th colspan=\"2\">");
            bufferedWriter.write(directory.getName());
            bufferedWriter.write("</th></tr>");

            for (FileCount fileCount : results) {
                bufferedWriter.write("<tr><td><a href=\"");
                String file = htmlSafeFile(fileCount.getFile());
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

    private Parse makeTable() {
        Parse result = makeTd();

        for (FileCount fileCount : results) {
            Parse row = makeTrTd();

            row.parts.body = "<a href=\"" + htmlSafeFile(fileCount.getFile()) + "\">"
                    + htmlSafeFile(fileCount.getFile()) + "</a>";
            row.parts.more.addToBody(fileCount.getCounts().toString());
            row.parts.more.addToTag(" bgcolor=\"" + color(fileCount.getCounts()) + "\"");

            result.last().more = row;
        }

        return result.more;
    }

    private Parse makeTrTd() {
        return parse("<tr><td></td><td></td></tr>", new String[]{"tr", "td"});
    }

    private Parse makeTd() {
        return parse("<td></td>", new String[]{"td"});
    }

    private Parse parse(String s, String[] strings) {
        Parse row;
        try {
            row = new Parse(s, strings);
        } catch (ParseException e) {
            throw new AssertionError("Unable to parse table");
        }
        return row;
    }

    /**
     * Replaces a row which one or more results.
     *
     * @param row the row to replace
     */
    public void replaceLastIn(final Parse row) {
        Parse table = makeTable();

        if (table != null) {
            table.last().more = row.more;
            row.more = table.more;
            row.parts.more = table.parts;
        }
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
