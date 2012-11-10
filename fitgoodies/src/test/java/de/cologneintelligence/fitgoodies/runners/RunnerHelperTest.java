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

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.AbstractDirectoryHelper;
import de.cologneintelligence.fitgoodies.file.DirectoryHelperMock;
import de.cologneintelligence.fitgoodies.runners.Runner;
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Counts;

/**
 * @author jwierum
 */
public final class RunnerHelperTest extends FitGoodiesTestCase {
    private RunnerHelper helper;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        helper = DependencyManager.getOrCreate(RunnerHelper.class);
    }

    public void testFilePath() {
        helper.setFilePath("/path/to/test1.html");
        assertEquals("/path/to/test1.html", helper.getFilePath());

        helper.setFilePath("/dir/file2.html");
        assertEquals("/dir/file2.html", helper.getFilePath());
    }

    public void testResultPath() {
        helper.setResultFilePath("/path/to/test1.html");
        assertEquals("/path/to/test1.html", helper.getResultFilePath());

        helper.setResultFilePath("/dir/file2.html");
        assertEquals("/dir/file2.html", helper.getResultFilePath());
    }

    public void testEncoding() {
        Runner runner = new Runner() {
            @Override
            public Counts run(final String inputFile, final String outputFile) {
                return null;
            }

            @Override
            public void setEncoding(final String encoding) { }

            @Override
            public String getEncoding() { return null; }
        };

        helper.setRunner(runner);
        assertSame(runner, helper.getRunner());
    }

    public void testHelper() {
        AbstractDirectoryHelper dirHelper = new DirectoryHelperMock();
        helper.setHelper(dirHelper);
        assertSame(dirHelper, helper.getHelper());
    }

    public void testStream() {
        helper.setLog(System.err);
        assertSame(System.err, helper.getLog());

        helper.setLog(System.out);
        assertSame(System.out, helper.getLog());
    }
}
