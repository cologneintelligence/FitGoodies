/*
 * Copyright (c) 2009-2015  Cologne Intelligence GmbH
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

import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ArgumentParserTest extends FitGoodiesTestCase {
    private static final String PATTERN = ".*\\.html?";

    @Mock
    private FileSystemDirectoryHelper fsHelper;

    @Test
    public void simpleCall() {
        testParser(null, new String[]{"-d", "outdir", "-f", "in.html"},
                "outdir", "utf-8",
                new File("in.html").getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void outputIsRequired() {
        new ArgumentParser(null, null).parse(new String[]{"-f", "in.html"});
    }

    @Test
    public void fileLong() {
        testParser(null, new String[]{"-d", "outdir", "--file", "in.html"},
                "outdir", "utf-8",
                new File("in.html").getAbsolutePath());
    }

    @Test
    public void outputLong() {
        testParser(null, new String[]{"--destination", "outdir", "--file", "in.html"},
                "outdir", "utf-8",
                new File("in.html").getAbsolutePath());
    }

    @Test
    public void multipleFiles() {
        testParser(null, new String[]{"-d", "outdir", "--file", "in.html", "-f", "file1.html", "--file", "other file.html"},
                "outdir", "utf-8",
                new File("in.html").getAbsolutePath(), new File("file1.html").getAbsolutePath(),
                new File("other file.html").getAbsolutePath());
    }

    @Test
    public void setEncoding() {
        testParser(null, new String[]{"-d", "outdir", "-f", "in.html", "-e", "latin-1"},
                "outdir", "latin-1",
                new File("in.html").getAbsolutePath());
    }

    @Test
    public void setEncodingLong() {
        testParser(null, new String[]{"-d", "outdir", "-f", "in.html", "--encoding", "latin-1"},
                "outdir", "latin-1",
                new File("in.html").getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEncodingTwice() {
        new ArgumentParser(null, fsHelper).parse(
                new String[]{"-d", "outdir", "-f", "in.html", "-e", "latin-1", "-e", "utf-8"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOutputTwice() {
        new ArgumentParser(null, fsHelper).parse(
                new String[]{"-d", "outdir", "-f", "in.html", "-e", "latin-1", "--destination", "other"});
    }

    @Test
    public void addDirectory() {
        File dir = mockDirectory(PATTERN, "dir1/f1.html", "dir2/f2.html", "dir2/f3.html");

        File dir1 = getMockedFile(dir, "dir1");
        File dir2 = getMockedFile(dir, "dir2");
        when(fsHelper.rel2abs(dir.getAbsolutePath(), "dir1")).thenReturn(dir1);
        when(fsHelper.rel2abs(dir.getAbsolutePath(), "dir2")).thenReturn(dir2);

        ArgumentParser parser = testParser(dir, new String[]{"-d", "outdir2", "-s", "dir1"},
                "outdir2", "utf-8", "dir1/f1.html");
        assertThat(parser.getBaseDir().getName(), equalTo("dir1"));

    }

    @Test
    public void addDirectoryLong() {
        File dir = mockDirectory(PATTERN, "dir1/f1.html", "dir2/f2.html", "dir2/f3.html");

        File dir1 = getMockedFile(dir, "dir1");
        File dir2 = getMockedFile(dir, "dir2");
        when(fsHelper.rel2abs(dir.getAbsolutePath(), "dir1")).thenReturn(dir1);
        when(fsHelper.rel2abs(dir.getAbsolutePath(), "dir2")).thenReturn(dir2);

        ArgumentParser parser = testParser(dir, new String[]{"-d", "outdir", "--source", "dir2"},
                "outdir", "utf-8", "dir2/f2.html", "dir2/f3.html");
        assertThat(parser.getBaseDir().getName(), equalTo("dir2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addDirectoryTwice() {
        File dir = mockDirectory(PATTERN, "dir1/f1.html", "dir2/f2.html", "dir2/f3.html");

        File dir2 = getMockedFile(dir, "dir2");
        when(fsHelper.rel2abs(dir.getAbsolutePath(), "dir2")).thenReturn(dir2);

        new ArgumentParser(dir, fsHelper).parse(new String[]{"-d", "outdir", "-s", "dir2", "--source", "dir1"});
    }

    @Test
    public void addDirectoryWithLimit() {
        File dir = mockDirectory(PATTERN, "dir1/f1.html", "dir2/f2.html", "dir2/f3.html", "dir2/f4.html");

        File dir2 = getMockedFile(dir, "dir2");
        File file2 = getMockedFile(dir, "dir2", "f2.html");
        File file4 = getMockedFile(dir, "dir2", "f4.html");
        when(fsHelper.rel2abs(dir.getAbsolutePath(), "dir2")).thenReturn(dir2);
        when(fsHelper.rel2abs(dir2.getAbsolutePath(), "f2.html")).thenReturn(file2);
        when(fsHelper.rel2abs(dir2.getAbsolutePath(), "f4.html")).thenReturn(file4);

        testParser(dir, new String[]{"-d", "outdir", "-s", "dir2", "--only", "f2.html", "-o", "f4.html"},
                "outdir", "utf-8", "dir2/f2.html", "dir2/f4.html");
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLimitInWrongPosition1() {
        new ArgumentParser(null, fsHelper).parse(new String[]{"-d", "outdir", "--only", "file1", "-o", "limit"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLimitInWrongPosition2() {
        File dir = mockDirectory(PATTERN, "dir/f.html");
        File subdir = getMockedFile(dir, "dir");
        when(fsHelper.rel2abs(dir.getAbsolutePath(), "dir")).thenReturn(subdir);
        new ArgumentParser(dir, fsHelper).parse(new String[]{"-d", "outdir", "-s", "dir", "--file", "file1", "-o", "limit"});
    }

    private ArgumentParser testParser(File baseDir, String[] arguments, String destinationDir, String encoding, String... files) {
        ArgumentParser parser = new ArgumentParser(baseDir, fsHelper);
        parser.parse(arguments);

        assertThat(parser.getDestinationDir().getAbsolutePath(), is(equalTo(new File(destinationDir).getAbsolutePath())));
        assertThat(parser.getEncoding(), is(equalTo(encoding)));

        List<Matcher<FileInformation>> matchers = new ArrayList<>();
        for (String file : files) {
            matchers.add(equalToFile(file));
        }

        assertThat(parser.getFiles(), contains(matchers.toArray(new Matcher[matchers.size()])));
        return parser;
    }


    private Matcher<FileInformation> equalToFile(final String name) {
        return new BaseMatcher<FileInformation>() {
            @Override
            public boolean matches(Object item) {
                return ((FileInformation) item).getFile().getPath().equals(name);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("<" + name + ">");
            }
        };
    }
}
