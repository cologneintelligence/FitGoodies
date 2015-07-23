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
import de.cologneintelligence.fitgoodies.Counts;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public final class RunnerHelperTest extends FitGoodiesTestCase {
    private RunnerHelper helper;

    @Before
    public void prepareMocks()  {
        helper = DependencyManager.getOrCreate(RunnerHelper.class);
    }

    @Test
    public void testFilePath() {
        helper.setFile(new File("/path/to/test1.html"));
        assertThat(helper.getFile(), is(equalTo(new File("/path/to/test1.html"))));

        helper.setFile(new File("/dir/file2.html"));
        assertThat(helper.getFile(), is(equalTo(new File("/dir/file2.html"))));
    }

    @Test
    public void testResultPath() {
        helper.setResultFile(new File("/path/to/test1.html"));
        assertThat(helper.getResultFile(), is(equalTo(new File("/path/to/test1.html"))));

        helper.setResultFile(new File("/dir/file2.html"));
        assertThat(helper.getResultFile(), is(equalTo(new File("/dir/file2.html"))));
    }

    @Test
    public void testEncoding() {
        Runner runner = new Runner() {
            @Override
            public Counts run(final File inputFile, final File outputFile) {
                return null;
            }

            @Override
            public void setEncoding(final String encoding) { }

            @Override
            public String getEncoding() { return null; }
        };

        helper.setRunner(runner);
        assertThat(helper.getRunner(), is(sameInstance(runner)));
    }

    @Test
    public void testHelper() {
        FileSystemDirectoryHelper dirHelper = Mockito.mock(FileSystemDirectoryHelper.class);
        helper.setHelper(dirHelper);
        assertThat(helper.getHelper(), is(sameInstance(dirHelper)));
    }

    @Test
    public void testStream() {
        helper.setLog(System.err);
        assertThat(helper.getLog(), is(sameInstance(System.err)));

        helper.setLog(System.out);
        assertThat(helper.getLog(), is(sameInstance(System.out)));
    }
}
