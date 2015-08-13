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

package de.cologneintelligence.fitgoodies.runners.selectors;

import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;
import de.cologneintelligence.fitgoodies.runners.DirectoryFilter;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DirectoryFilterTest extends FitGoodiesTestCase {
    private FileSystemDirectoryHelper fsHelper;

    @Before
    public void setup() {
        fsHelper = mock(FileSystemDirectoryHelper.class);
    }

    @Test
    public void returnsAllFiles() {
        File dir = mockDirectory("(?i).*\\.html?",
                "file1.html",
                "file2.html",
                "file3.htm"
        );

        assertThat(new DirectoryFilter(dir, fsHelper).getSelectedFiles(),
                contains(
                        equalToFile("file1.html"),
                        equalToFile("file2.html"),
                        equalToFile("file3.htm")));

    }

    @Test
    public void returnsOrderedFiles() {
        File dir = mockDirectory("(?i).*\\.html?",
                "file1.html",
                "setup.html",
                "teardown.html"
        );

        assertThat(new DirectoryFilter(dir, fsHelper).getSelectedFiles(), contains(
                equalToFile("setup.html"),
                equalToFile("file1.html"),
                equalToFile("teardown.html")));

    }

    @Test
    public void returnsOrderedSubdirectories() {
        File dir = mockDirectory("(?i).*\\.html?",
                "file1.html",
                "a/setup.html",
                "a/file3.html",
                "a/b/file0.html",
                "a/teardown.html",
                "setup.html",
                "teardown.html"
        );

        assertThat(new DirectoryFilter(dir, fsHelper).getSelectedFiles(), contains(
                equalToFile("setup.html"),
                equalToFile("a/setup.html"),
                equalToFile("a/b/file0.html"),
                equalToFile("a/file3.html"),
                equalToFile("a/teardown.html"),
                equalToFile("file1.html"),
                equalToFile("teardown.html")));

    }

    @Test
    public void limitingToUnknownFileReturnsEmptyList() {
        File dir = mockDirectory("(?i).*\\.html?", "file1.html");

        final DirectoryFilter directoryFilter = new DirectoryFilter(dir, fsHelper);
        directoryFilter.addLimit(new File("non-existend"));
        assertThat(directoryFilter.getSelectedFiles(), Matchers.empty());
    }

    @Test
    public void returnsOrderedFilteredFiles() {
        File dir = mockDirectory("(?i).*\\.html?",
                "file1.html",
                "a/file3.html",
                "a/b/file0.html");

        final DirectoryFilter directoryFilter = new DirectoryFilter(dir, fsHelper);
        directoryFilter.addLimit(getMockedFile(dir, "file1.html"));
        directoryFilter.addLimit(getMockedFile(dir, "a", "b", "file0.html"));
        assertThat(directoryFilter.getSelectedFiles(), contains(
                equalToFile("a/b/file0.html"),
                equalToFile("file1.html")));
    }

    @Test
    public void filteredFilesIncludesSetupFiles() throws Exception {
        File dir = mockDirectory("(?i).*\\.html?",
                "file1.html",
                "file2.html",
                "a/file3.html",
                "a/b/file0.html",
                "a/b/file7.html",
                "a/setup.html",
                "setup.html",
                "teardown.html",
                "a/setup.html",
                "a/b/teardown.html",
                "b/setup.html");

        when(fsHelper.isSubDir(getMockedFile(dir, "a", "b", "file0.html").getParentFile(), getMockedFile(dir, "setup.html").getParentFile())).thenReturn(true);
        when(fsHelper.isSubDir(getMockedFile(dir, "a", "b", "file0.html").getParentFile(), getMockedFile(dir, "a", "setup.html").getParentFile())).thenReturn(true);
        when(fsHelper.isSubDir(getMockedFile(dir, "a", "b", "teardown.html").getParentFile(), getMockedFile(dir, "a", "b", "file7.html").getParentFile())).thenReturn(true);
        when(fsHelper.isSubDir(getMockedFile(dir, "teardown.html").getParentFile(), getMockedFile(dir, "file1.html").getParentFile())).thenReturn(true);

        final DirectoryFilter directoryFilter = new DirectoryFilter(dir, fsHelper);
        directoryFilter.addLimit(getMockedFile(dir, "file1.html"));
        directoryFilter.addLimit(getMockedFile(dir, "a", "b", "file0.html"));
        directoryFilter.addLimit(getMockedFile(dir, "a", "b", "file7.html"));
        assertThat(directoryFilter.getSelectedFiles(), contains(
                equalToFile("setup.html"),
                equalToFile("a/setup.html"),
                equalToFile("a/b/file0.html"),
                equalToFile("a/b/file7.html"),
                equalToFile("a/b/teardown.html"),
                equalToFile("file1.html"),
                equalToFile("teardown.html")));

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
