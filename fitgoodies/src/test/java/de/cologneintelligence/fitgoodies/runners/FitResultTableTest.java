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

import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import fit.Counts;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public final class FitResultTableTest extends FitGoodiesTestCase {
    private FitResultTable result;

    @Before
    public void setUp() {
        result = new FitResultTable(new FileSystemDirectoryHelper());
    }

    private static String tr(final File file, final int indent,
                             final String name, final String color,
                             final int r, final int w, final int i, final int e) {
        StringBuilder builder = new StringBuilder();
        builder.append("<tr bgcolor=\"#");
        builder.append(color);
        builder.append("\"><td>");
        for (int j = 0; j < indent; ++j) {
            builder.append(" &nbsp; &nbsp; &nbsp; &nbsp;");
        }
        builder.append("<a href=\"");
        builder.append(file.toString().replace(File.separatorChar, '/'));
        builder.append("\">");
        builder.append(name.replace(File.separatorChar, '/'));
        builder.append("</a></td><td>");
        builder.append(r);
        builder.append(" right, ");
        builder.append(w);
        builder.append(" wrong, ");
        builder.append(i);
        builder.append(" ignored, ");
        builder.append(e);
        builder.append(" exceptions</td></tr>");
        return builder.toString();
    }

    private static String tr(final File file, final String color,
                             final int r, final int w, final int i, final int e) {
        return tr(file, 0, file.toString(), color, r, w, i, e);
    }

    private static String th(final String path, final String color,
                             final int r, final int w, final int i, final int e) {
        return String.format("<tr bgcolor=\"#%s\"><th style=\"text-align: left\">%s</th><td>%d right, %d wrong, %d ignored, %d exceptions</td></tr>",
                color, path, r, w, i, e);
    }

    @Test
    public void testGetPut() {
        Counts file1 = mkCounts(2, 1, 3, 0);
        Counts file2 = mkCounts(3, 5, 2, 11);
        Counts file3 = mkCounts(5, 7, 3, 4);
        Counts file4 = mkCounts(4, 4, 8, 0);

        result.put(new File("file1.html"), file1);
        result.put(new File("file2.html"), file2);
        result.put(new File("file3.html"), file3);
        result.put(new File("file1.html"), file4);

        assertThat(result.get(new File("file1.html")), is(sameInstance(file4)));
        assertThat(result.get(new File("file2.html")), is(sameInstance(file2)));
        assertThat(result.get(new File("file3.html")), is(sameInstance(file3)));

        result.put(new File("file1.html"), file1);
        assertThat(result.get(new File("file1.html")), is(sameInstance(file1)));

        assertThat(result.get(new File("file7.html")), is(nullValue()));
    }

    @Test
    public void testFiles() {
        final File file1 = new File("file1.html");
        final File file2 = new File("file2.html");
        final File file3 = new File("file3.html");

        result.put(file1, null);
        result.put(file2, null);
        result.put(file3, null);
        result.put(file2, null);

        File[] files = result.getFiles();
        assertThat(Arrays.asList(files), is(equalTo(Arrays.asList(
                file1, file2, file3))));
    }

    @Test
    public void testSummary() {
        result.put(new File("f1"), mkCounts(1, 2, 3, 4));
        result.put(new File("f2"), mkCounts(4, 4, 4, 4));

        Counts counts = result.getSummary();
        assertThat(counts.right, is(equalTo((Object) 5)));
        assertThat(counts.wrong, is(equalTo((Object) 6)));
        assertThat(counts.ignores, is(equalTo((Object) 7)));
        assertThat(counts.exceptions, is(equalTo((Object) 8)));
    }

    @Test
    public void testRow() {
        final File file1 = new File("file1.html");
        final File file2 = new File("file2.html");
        final File file3 = new File("file3.html");

        result.put(file1, mkCounts(2, 1, 3, 0));
        result.put(file2, mkCounts(3, 0, 0, 11));
        result.put(file3, mkCounts(0, 0, 0, 0));

        assertThat(result.getRow(file1), is(equalTo(tr(file1, "ffcfcf", 2, 1, 3, 0))));
        assertThat(result.getRow(file2), is(equalTo(tr(file2, "ffcfcf", 3, 0, 0, 11))));
        assertThat(result.getRow(file3), is(equalTo(tr(file3, "cfffcf", 0, 0, 0, 0))));

        result.put(file3, mkCounts(7, 0, 0, 0));
        assertThat(result.getRow(file3), is(equalTo(tr(file3, "cfffcf", 7, 0, 0, 0))));
    }

    @Test
    public void testSummaryRow() {
        assertThat(result.getSummaryRow(new File("x")), is(equalTo("<tr bgcolor=\"#cfffcf\"><th style=\"text-align: left\">x"
                + "</th><th style=\"text-align: left\">"
                + "0 right, 0 wrong, 0 ignored, "
                + "0 exceptions</th></tr>")));

        final File file1 = new File("file1.html");
        final File file2 = new File("file2.html");
        final File file3 = new File("file3.html");

        result.put(file1, mkCounts(2, 1, 3, 0));
        result.put(file2, mkCounts(3, 0, 0, 11));

        assertThat(result.getSummaryRow(new File("y")), is(equalTo("<tr bgcolor=\"#ffcfcf\"><th style=\"text-align: left\">y"
                + "</th><th style=\"text-align: left\">"
                + "5 right, 1 wrong, 3 ignored, "
                + "11 exceptions</th></tr>")));

        result.put(file1, mkCounts(2, 0, 0, 0));
        result.put(file2, mkCounts(3, 0, 1, 0));
        result.put(file3, mkCounts(5, 0, 0, 0));

        assertThat(result.getSummaryRow(new File("z")), is(equalTo("<tr bgcolor=\"#cfffcf\"><th style=\"text-align: left\">z"
                + "</th><th style=\"text-align: left\">"
                + "10 right, 0 wrong, 1 ignored, "
                + "0 exceptions</th></tr>")));
    }

    @Test
    public void testPrint() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        final File file1 = new File("file1.html");
        final File file2 = new File("file2.html");
        final File file3 = new File("file3.html");
        result.put(file1, mkCounts(2, 1, 3, 0));
        result.put(file2, mkCounts(3, 0, 0, 11));
        result.put(file3, mkCounts(0, 0, 0, 0));

        result.print(new File("mydir"), bos);
        bos.close();

        assertThat(bos.toString(), is(equalTo("<table>"
                + "<tr bgcolor=\"#ffcfcf\">"
                + "<th style=\"text-align: left\">mydir</th>"
                + "<th style=\"text-align: left\">5 right, 1 wrong, "
                + "3 ignored, 11 exceptions</th>"
                + "</tr>"
                + "<tr><td colspan=\"2\"></td></tr>"
                + tr(file1, "ffcfcf", 2, 1, 3, 0)
                + tr(file2, "ffcfcf", 3, 0, 0, 11)
                + tr(file3, "cfffcf", 0, 0, 0, 0)
                + "</table>")));
    }

    @Test
    public void testPrintWithSubDirs() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        final File file1 = new File("file1.html");
        final File file2 = new File("file2.html");
        final File fileGA2 = new File("g/a/file2.html");
        final File fileG1 = new File("g/file1.html");
        final File fileG2 = new File("g/file2.html");
        final File fileM2 = new File("m/file2.html");
        final File fileX = new File("x.html");

        result.put(file1, mkCounts(3, 2, 1, 2));
        result.put(file2, mkCounts(20, 2, 0, 0));
        result.put(fileGA2, mkCounts(25, 0, 0, 0));
        result.put(fileG1, mkCounts(1, 1, 0, 0));
        result.put(fileG2, mkCounts(0, 0, 0, 0));
        result.put(fileM2, mkCounts(1, 1, 1, 1));
        result.put(fileX, mkCounts(2, 0, 0, 1));

        result.print(new File("tests"), bos);
        bos.close();

        assertThat(bos.toString(), is(equalTo("<table>"
                + "<tr bgcolor=\"#ffcfcf\">"
                + "<th style=\"text-align: left\">tests</th>"
                + "<th style=\"text-align: left\">52 right, 6 wrong,"
                + " 2 ignored, 4 exceptions</th>"
                + "</tr>"
                + "<tr><td colspan=\"2\"></td></tr>"
                + tr(file1, "ffcfcf", 3, 2, 1, 2)
                + tr(file2, "ffcfcf", 20, 2, 0, 0)
                + th("g", "ffcfcf", 26, 1, 0, 0)
                + th("g/a", "cfffcf", 25, 0, 0, 0)
                + tr(fileGA2, 2, "file2.html", "cfffcf", 25, 0, 0, 0)
                + tr(fileG1, 1, "file1.html", "ffcfcf", 1, 1, 0, 0)
                + tr(fileG2, 1, "file2.html", "cfffcf", 0, 0, 0, 0)
                + th("m", "ffcfcf", 1, 1, 1, 1)
                + tr(fileM2, 1, "file2.html", "ffcfcf", 1, 1, 1, 1)
                + tr(fileX, "ffcfcf", 2, 0, 0, 1)
                + "</table>")));
    }

    @Test
    // can this ever happen?
    public void testNull() {
        final File file3 = new File("file3.html");
        result.put(file3, null);

        assertThat(result.getRow(file3), is(equalTo("<tr bgcolor=\"#efefef\"><td><a href=\"file3.html\">"
                + "file3.html</a></td><td>(none)</td></tr>")));
        assertThat(result.getSummaryRow(new File("n")), is(equalTo("<tr bgcolor=\"#cfffcf\"><th style=\"text-align: left\">n</th>"
                + "<th style=\"text-align: left\">"
                + "0 right, 0 wrong, 0 ignored, "
                + "0 exceptions</th></tr>")));
    }

    @Test
    public void testIndent() {
        final File file1 = new File("file1.html");
        final File file2 = new File("file2.html");
        final File fileG1 = new File("g/file1.html");
        final File fileG2 = new File("g/file2.html");
        final File fileGG2 = new File("g/g/file2.html");
        final File fileM2 = new File("m/file2.html");
        final File fileX = new File("x.html");

        result.put(file1, null);
        result.put(file2, null);
        result.put(fileG1, null);
        result.put(fileG2, null);
        result.put(fileGG2, null);
        result.put(fileM2, null);
        result.put(fileX, null);

        assertThat(result.getRow(file1), is(equalTo("<tr bgcolor=\"#efefef\"><td><a href=\"file1.html\">"
                + "file1.html</a></td><td>(none)</td></tr>")));
        assertThat(result.getRow(file2), is(equalTo("<tr bgcolor=\"#efefef\"><td><a href=\"file2.html\">"
                + "file2.html</a></td><td>(none)</td></tr>")));
        assertThat(result.getRow(fileG1), is(equalTo("<tr bgcolor=\"#efefef\"><td> &nbsp; &nbsp; &nbsp; &nbsp;"
                + "<a href=\"g/file1.html\">file1.html</a></td><td>(none)</td></tr>")));
        assertThat(result.getRow(fileG2), is(equalTo("<tr bgcolor=\"#efefef\"><td> &nbsp; &nbsp; &nbsp; &nbsp;"
                + "<a href=\"g/file2.html\">file2.html</a></td><td>(none)</td></tr>")));
        assertThat(result.getRow(fileGG2), is(equalTo("<tr bgcolor=\"#efefef\"><td> &nbsp; &nbsp; &nbsp; &nbsp;"
                + " &nbsp; &nbsp; &nbsp; &nbsp;<a href=\"g/g/file2.html\">"
                + "file2.html</a></td><td>(none)</td></tr>")));
        assertThat(result.getRow(fileM2), is(equalTo("<tr bgcolor=\"#efefef\"><td> &nbsp; &nbsp; &nbsp; &nbsp;"
                + "<a href=\"m/file2.html\">file2.html</a></td><td>(none)</td></tr>")));
        assertThat(result.getRow(fileX), is(equalTo("<tr bgcolor=\"#efefef\"><td><a href=\"x.html\">"
                + "x.html</a></td><td>(none)</td></tr>")));
    }

    @Test
    public void testSubSummary() throws IOException {
        result.put(new File("file1.html"), mkCounts(3, 2, 1, 2));
        result.put(new File("file2.html"), mkCounts(20, 2, 0, 0));
        result.put(new File("g/a/file2.html"), mkCounts(25, 0, 0, 0));
        result.put(new File("g/file1.html"), mkCounts(1, 1, 0, 0));
        result.put(new File("g/file2.html"), mkCounts(0, 0, 0, 0));
        result.put(new File("m/file2.html"), mkCounts(1, 1, 1, 1));
        result.put(new File("x.html"), mkCounts(2, 0, 0, 1));

        assertThat(result.getSubSummaryRow(new File("g")), is(equalTo(th("g", "ffcfcf", 26, 1, 0, 0))));
        assertThat(result.getSubSummaryRow(new File("g/a")), is(equalTo(th("g/a", "cfffcf", 25, 0, 0, 0))));
    }

    @Test
    public void testEmptyTable() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        result.print(new File("test"), stream);
        assertThat(stream.toString(), is(equalTo("<table><tr bgcolor=\"#cfffcf\"><th style=\"text-align: left\">test</th>"
                + "<th style=\"text-align: left\">0 right, 0 wrong, 0 ignored, 0 exceptions</th>"
                + "</tr><tr><td colspan=\"2\"></td></tr>"
                + "<tr><td colspan=\"2\">no files found</td></tr>"
                + "</table>")));
    }

    @Test
    public void testTitles() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        File file1000wIt01AF = new File("1000_w/it_01/a/f.html");
        File file1000wIt01AG = new File("1000_w/it_01/a/g.html");
        File file1000wIt01AH = new File("1000_w/it_01/a/hij/m.html");
        File file1001x20A = new File("1001_x/20_a.html");
        File file1001X260b = new File("1001_x/260-b.html");

        result.put(file1000wIt01AF, mkCounts(1, 0, 0, 0));
        result.put(file1000wIt01AG, mkCounts(1, 0, 0, 0));
        result.put(file1000wIt01AH, mkCounts(1, 0, 0, 0));
        result.put(file1001x20A, mkCounts(1, 0, 0, 0));
        result.put(file1001X260b, mkCounts(1, 0, 0, 0));

        result.print(new File("test"), stream);
        assertThat(stream.toString(), is(equalTo("<table><tr bgcolor=\"#cfffcf\"><th style=\"text-align: left\">test</th>"
                + "<th style=\"text-align: left\">5 right, 0 wrong, 0 ignored, 0 exceptions</th>"
                + "</tr><tr><td colspan=\"2\"></td></tr>"
                + th("1000_w", "cfffcf", 3, 0, 0, 0)
                + th("1000_w/it_01", "cfffcf", 3, 0, 0, 0)
                + th("1000_w/it_01/a", "cfffcf", 3, 0, 0, 0)
                + tr(file1000wIt01AF, 3, "f.html", "cfffcf", 1, 0, 0, 0)
                + tr(file1000wIt01AG, 3, "g.html", "cfffcf", 1, 0, 0, 0)
                + th("1000_w/it_01/a/hij", "cfffcf", 1, 0, 0, 0)
                + tr(file1000wIt01AH, 4, "m.html", "cfffcf", 1, 0, 0, 0)
                + th("1001_x", "cfffcf", 2, 0, 0, 0)
                + tr(file1001x20A, 1, "20_a.html", "cfffcf", 1, 0, 0, 0)
                + tr(file1001X260b, 1, "260-b.html", "cfffcf", 1, 0, 0, 0)
                + "</table>")));
    }
}
