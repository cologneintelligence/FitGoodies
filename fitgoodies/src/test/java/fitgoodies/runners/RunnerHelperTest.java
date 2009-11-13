/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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


package fitgoodies.runners;

import fit.Counts;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.file.AbstractDirectoryHelper;
import fitgoodies.file.DirectoryHelperMock;

/**
 * $Id$
 * @author jwierum
 */
public final class RunnerHelperTest extends FitGoodiesTestCase {
	public void testSingleton() {
		RunnerHelper expected = RunnerHelper.instance();

		expected = RunnerHelper.instance();
		assertSame(expected, RunnerHelper.instance());

		RunnerHelper.reset();
		assertNotSame(expected, RunnerHelper.instance());
	}

	public void testFilePath() {
		RunnerHelper.instance().setFilePath("/path/to/test1.html");
		assertEquals("/path/to/test1.html", RunnerHelper.instance().getFilePath());

		RunnerHelper.instance().setFilePath("/dir/file2.html");
		assertEquals("/dir/file2.html", RunnerHelper.instance().getFilePath());
	}

	public void testResultPath() {
		RunnerHelper.instance().setResultFilePath("/path/to/test1.html");
		assertEquals("/path/to/test1.html", RunnerHelper.instance().getResultFilePath());

		RunnerHelper.instance().setResultFilePath("/dir/file2.html");
		assertEquals("/dir/file2.html", RunnerHelper.instance().getResultFilePath());
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

		RunnerHelper.instance().setRunner(runner);
		assertSame(runner, RunnerHelper.instance().getRunner());
	}

	public void testHelper() {
		AbstractDirectoryHelper helper = new DirectoryHelperMock();
		RunnerHelper.instance().setHelper(helper);
		assertSame(helper, RunnerHelper.instance().getHelper());
	}

	public void testStream() {
		RunnerHelper.instance().setLog(System.err);
		assertSame(System.err, RunnerHelper.instance().getLog());

		RunnerHelper.instance().setLog(System.out);
		assertSame(System.out, RunnerHelper.instance().getLog());
	}
}
