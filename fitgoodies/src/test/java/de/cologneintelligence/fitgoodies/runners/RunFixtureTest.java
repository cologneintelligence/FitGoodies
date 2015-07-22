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
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.ParseException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public final class RunFixtureTest extends FitGoodiesTestCase {

    private Parse table;

    @Before
    public void setup() {
        RunnerHelper helper = DependencyManager.getOrCreate(RunnerHelper.class);
        final Runner runner = mock(Runner.class);

        File f1 = mock(File.class, "f1");
        File f2 = mock(File.class, "f2");
        File f3 = mock(File.class, "f3");
        File f4 = mock(File.class, "f4");

        when(runner.run(f1, f2)).thenReturn(mkCounts(1, 2, 3, 0));
        when(runner.run(f3, f4)).thenReturn(mkCounts(5, 0, 7, 0));

        FileSystemDirectoryHelper dirHelper = mock(FileSystemDirectoryHelper.class);
        helper.setFile(mock(File.class));
        helper.setResultFile(mock(File.class));
        helper.setHelper(dirHelper);
        helper.setRunner(runner);

        File inputDir = mock(File.class, "inputDir");
        File outputDir = mock(File.class, "outputDir");

        when(helper.getResultFile().getParentFile()).thenReturn(outputDir);
        when(helper.getFile().getParentFile()).thenReturn(inputDir);

        when(inputDir.getAbsolutePath()).thenReturn("abspath");
        when(outputDir.getAbsoluteFile()).thenReturn(outputDir);

        when(f1.getName()).thenReturn("file1.html");
        when(f3.getName()).thenReturn("file2.html");
        when(dirHelper.rel2abs("abspath", "file1.html")).thenReturn(f1);
        when(dirHelper.subdir(outputDir, "file1.html")).thenReturn(f2);
        when(dirHelper.rel2abs("abspath", "../tests2/file2.html")).thenReturn(f3);
        when(dirHelper.subdir(outputDir, "file2.html")).thenReturn(f4);

        table = parseTable(
                tr("file", "file1.html"),
                tr("file", "../tests2/file2.html</td></tr></table>"));
    }

    @Test
    public void testFile() {
        RunFixture fixture = new RunFixture();
        fixture.doTable(table);

        assertCounts(fixture.counts, table, 6, 2, 10, 0);

        assertThat(table.parts.more.parts.more.tag.contains("ffcfcf"), is(true));
        assertThat(table.parts.more.more.parts.more.tag.contains("cfffcf"), is(true));
    }

    @Test
    public void testFileTableProcessing() {
        RunFixture fixture = new RunFixture();
        fixture.doTable(table);

        assertThat(table.parts.more.parts.body, is(equalTo("<a href=\"file1.html\">file1.html</a>")));
        assertThat(table.parts.more.parts.more.body, is(equalTo("1 right, 2 wrong, 3 ignored, 0 exceptions")));
        assertThat(table.parts.more.more.parts.body, is(equalTo("<a href=\"file2.html\">file2.html</a>")));
        assertThat(table.parts.more.more.parts.more.body, is(equalTo("5 right, 0 wrong, 7 ignored, 0 exceptions")));

        assertThat(table.parts.more.parts.more.tag.contains("ffcfcf"), is(true));
        assertThat(table.parts.more.more.parts.more.tag.contains("cfffcf"), is(true));
    }
}
