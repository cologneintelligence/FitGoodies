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

package de.cologneintelligence.fitgoodies.testsupport;

import de.cologneintelligence.fitgoodies.Counts;
import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.htmlparser.FitTable;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.hamcrest.Matcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public abstract class FitGoodiesTestCase {

    protected FitTable lastFitTable;
    protected Element lastElement;

    protected static Counts mkCounts(final int r, final int w, final int i,
                                     final int e) {
        final Counts c = new Counts();
        c.right = r;
        c.wrong = w;
        c.ignores = i;
        c.exceptions = e;
        return c;
    }

    @Before
    public void cleanupDependencyManager() throws Exception {
        DependencyManager.clear();
    }

    public File mockDirectory(String pattern, String... files) {
        DirectoryMockHelper helper = new DirectoryMockHelper();
        for (String file : files) {
            helper.addFile(file);
        }

        return helper.finishMock(pattern);
    }

    protected void assertCounts(int right, int wrong, int ignores, int exceptions) {
        assertThat("Wrong counts! " + lastFitTable.pretty(), lastFitTable.getCounts(),
            equalTo(new Counts(right, wrong, ignores, exceptions)));
    }

    protected File getMockedFile(File dir, String... name) {
        File tmp = dir;
        for (String aName : name) {
            tmp = find(tmp, aName);
        }
        return tmp;
    }

    private File find(File dir, String s) {
        final File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(s)) {
                    return file;
                }
            }
        }
        throw new IllegalArgumentException("not found: " + s);
    }

    protected String tr(String... tds) {
        StringBuilder builder = new StringBuilder();
        builder.append("<tr>");
        for (String td : tds) {
            td(builder, td);
        }
        builder.append("</tr>");
        return builder.toString();
    }

    private void td(StringBuilder builder, String value) {
        builder.append("<td>").append(value).append("</td>");
    }

    protected FitCell parseTd(String value) {
        useTable(tr(value));
        return lastFitTable.rows().get(0).cells().get(0);
    }

    protected void useTable(String... trs) {
        StringBuilder builder = new StringBuilder();
        builder.append("<table>");
        builder.append(tr("ignoredClass"));
        for (String c : trs) {
            builder.append(c);
        }
        builder.append("</table>");

        lastElement = Jsoup.parse(builder.toString()).select("table").first();
        lastFitTable = new FitTable(lastElement);
    }

    public String htmlAt(int row, int col) {
        return lastElement.select("tr").get(row + 1).select("td").get(col).html();
    }

    public FitCell cellAt(int row, int col) {
        return lastFitTable.rows().get(row).cells().get(col);
    }

    protected Matcher<String> containsAll(String... values) {
        List<Matcher<? super String>> matchers = new ArrayList<>(values.length);
        for (String string : values) {
            matchers.add(containsString(string));
        }

        return allOf(matchers);
    }
}
