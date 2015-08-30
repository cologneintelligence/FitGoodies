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

import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;
import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public final class RunFixtureTest extends FitGoodiesFixtureTestCase<RunFixture> {

    @Override
    protected Class<RunFixture> getFixtureClass() {
        return RunFixture.class;
    }

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

		useTable(
            tr("file", "file1.html"),
            tr("file", "$file2"));

        preparePreprocessWithConversion(String.class, "file1.html", "file1.html");
        preparePreprocessWithConversion(String.class, "$file2", "../tests2/file2.html");
	}

	@Test
	public void testFile() {
		run();

		assertCounts(6, 2, 10, 0);
	}

	@Test
	public void testFileTableProcessing() {
		run();

        System.out.println(lastFitTable.pretty());
        assertThat(htmlAt(0, 1), is(equalTo("<a href=\"file1.html\">file1.html</a>")));
		assertThat(htmlAt(0, 2), is(equalTo("1 right, 2 wrong, 3 ignored, 0 exceptions")));
		assertThat(htmlAt(1, 1), is(equalTo("<a href=\"file2.html\">file2.html</a>")));
		assertThat(htmlAt(1, 2), is(equalTo("5 right, 0 wrong, 7 ignored, 0 exceptions")));
	}
}
