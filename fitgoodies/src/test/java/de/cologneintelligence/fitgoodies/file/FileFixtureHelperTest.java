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

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;


public class FileFixtureHelperTest extends FitGoodiesTestCase {
    private FileFixtureHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new FileFixtureHelper();
    }

    @Test
    public void testSelector() throws FileNotFoundException {
        helper.setEncoding("utf-8");
        helper.setPattern(".*");

        File directory = mockDirectory(null,
                "file1.txt",
                "file2.txt",
                "f.txt.bat");

        when(directory.listFiles(argThat(any(FilenameFilter.class))))
                .thenReturn(new File[]{
                        new File("file1.txt"),
                        new File("file2.txt"),
                });

        helper.setDirectory(directory);

        FileSelector fs = helper.getSelector();
        assertThat(fs.getFirstFile().toString(), is(equalTo("file1.txt")));

        fs = helper.getSelector();
        assertThat(fs.getFirstFile().toString(), is(equalTo("file1.txt")));
    }

    @Test
    public void testSelectorWithRegex() throws FileNotFoundException {
        final String pattern = ".*\\.bat";
        final File directory = mockDirectory(pattern, "f.txt.bat");

        helper.setDirectory(directory);

        helper.setPattern(pattern);
        FileSelector fs = helper.getSelector();
        assertThat(fs.getFirstFile().toString(), is(equalTo("f.txt.bat")));

        fs = helper.getSelector();
        assertThat(fs.getFirstFile().toString(), is(equalTo("f.txt.bat")));
    }

    @Test
    public void testEncoding() {
        helper.setEncoding("utf-8");
        assertThat(helper.getEncoding(), is(equalTo("utf-8")));

        helper.setEncoding("latin-1");
        assertThat(helper.getEncoding(), is(equalTo("latin-1")));
    }

    @Test
    public void testPattern() {
        helper.setPattern("*\\.txt");
        assertThat(helper.getPattern(), is(equalTo("*\\.txt")));

        helper.setPattern("*\\.bat");
        assertThat(helper.getPattern(), is(equalTo("*\\.bat")));
    }

    @Test
    public void testDirectory() {
        helper.setDirectory(new File("test"));
        assertThat(helper.getDirectory().getPath(), is(equalTo("test")));
    }
}
