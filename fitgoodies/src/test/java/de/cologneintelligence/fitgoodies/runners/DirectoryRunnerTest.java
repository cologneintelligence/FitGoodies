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


package de.cologneintelligence.fitgoodies.runners;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.DirectoryHelperMock;
import de.cologneintelligence.fitgoodies.file.DirectoryProvider;
import de.cologneintelligence.fitgoodies.file.FileInformation;
import de.cologneintelligence.fitgoodies.file.FileInformationMock;
import de.cologneintelligence.fitgoodies.runners.DirectoryRunner;
import de.cologneintelligence.fitgoodies.runners.FitResult;
import de.cologneintelligence.fitgoodies.runners.Runner;

import fit.Counts;

/**
 * $Id$
 * @author jwierum
 */
public class DirectoryRunnerTest extends FitGoodiesTestCase {
	private DirectoryRunner runner;
	private DirectoryHelperMock helper;

	@Override
	public final void setUp() throws Exception {
		super.setUp();
		helper = new DirectoryHelperMock();
		runner = new DirectoryRunner(mockFiles(),
				"/dest", "utf-8", helper);
	}

	public final DirectoryProvider mockFiles() throws FileNotFoundException {
		final DirectoryProvider provider1 =
			mock(DirectoryProvider.class, "provider1");
		final DirectoryProvider provider2 =
			mock(DirectoryProvider.class, "provider2");
		final DirectoryProvider provider3 =
			mock(DirectoryProvider.class, "provider3");
		final DirectoryProvider provider4 =
			mock(DirectoryProvider.class, "provider4");

		final List<FileInformation> dir1 = new ArrayList<FileInformation>();
		final List<FileInformation> dir2 = new ArrayList<FileInformation>();
		final List<FileInformation> dir3 = new ArrayList<FileInformation>();
		final List<FileInformation> dir4 = new ArrayList<FileInformation>();

		dir1.add(new FileInformationMock("/src/tests/", "99_teardown.htm", null));
		dir1.add(new FileInformationMock("/src/tests/", "01_setup.html", null));
		dir1.add(new FileInformationMock("/src/tests/", "readme.txt", null));
		dir2.add(new FileInformationMock("/src/tests/02_tests/", "01_file1.html", null));
		dir2.add(new FileInformationMock("/src/tests/02_tests/", "03_file2.HTM", null));
		dir3.add(new FileInformationMock("/src/tests/02_tests/02_subtests/",
				"testcase.html", null));
		dir3.add(new FileInformationMock("/src/tests/02_tests/02_subtests/",
				"myfixture.class", null));
		dir3.add(new FileInformationMock("/src/tests/02_tests/02_subtests/", "test.html", null));
		dir4.add(new FileInformationMock("/src/tests/03_tests/", "99_lasttest.html", null));

		checking(new Expectations() {{
			allowing(provider1).getPath(); will(returnValue("/src"));
			oneOf(provider1).getFiles(); will(returnValue(dir1.iterator()));
			oneOf(provider2).getFiles(); will(returnValue(dir2.iterator()));
			oneOf(provider3).getFiles(); will(returnValue(dir3.iterator()));
			oneOf(provider4).getFiles(); will(returnValue(dir4.iterator()));
			oneOf(provider1).getDirectories(); will(returnIterator(provider2, provider4));
			oneOf(provider2).getDirectories(); will(returnIterator(provider3));
			oneOf(provider3).getDirectories(); will(returnIterator(new DirectoryProvider[]{}));
			oneOf(provider4).getDirectories(); will(returnIterator(new DirectoryProvider[]{}));
		}});
		return provider1;
	}

	public final void testGetRelevantFiles() {
		FileInformation[] files = runner.getRelevantFiles();

		final int numberOfFiles = 7;
		assertEquals(numberOfFiles, files.length);
		assertEquals("/src/tests/01_setup.html", files[0].fullname());
		assertEquals("/src/tests/02_tests/02_subtests/test.html", files[2].fullname());
		assertEquals("/src/tests/02_tests/03_file2.HTM", files[4].fullname());
		assertEquals("/src/tests/99_teardown.htm", files[6].fullname());
	}

	public final void testPrepareDirectories() {
		runner.prepareDirectories(runner.getRelevantFiles());

		final int numberOfFiles = 7;
		String[] pathes = helper.getPathes();
		assertEquals(numberOfFiles, pathes.length);
		assertContains(pathes, "/dest/tests/02_tests/");
		assertContains(pathes, "/dest/tests/02_tests/02_subtests/");
		assertContains(pathes, "/dest/tests/03_tests/");
		assertNotContains(pathes, "/dst/tests/02_tests/");
	}

	public final void testPrepareDirectories2() {
		runner.prepareDirectories(runner.getRelevantFiles());

		final int numberOfFiles = 7;
		String[] pathes = helper.getPathes();
		assertEquals(numberOfFiles, pathes.length);
		assertContains(pathes, "/dest/tests/02_tests/");
		assertContains(pathes, "/dest/tests/02_tests/02_subtests/");
		assertContains(pathes, "/dest/tests/03_tests/");
		assertNotContains(pathes, "/destination/tests/02_tests/");
	}

	public final fit.Counts[] prepareRun(final Runner fileRunner) {
		final Counts count1 = new fit.Counts();
		count1.exceptions = 1;
		count1.ignores = 2;
		count1.right = 42;
		count1.wrong = 4;

		final Counts count2 = new fit.Counts();
		count2.right = 23;

		final Counts count3 = new fit.Counts();
		count3.exceptions = 4;

		checking(new Expectations() {{
			oneOf(fileRunner).run("/src/tests/99_teardown.htm",
					"/dest/tests/99_teardown.htm");
				will(returnValue(null));
			oneOf(fileRunner).run("/src/tests/01_setup.html",
					"/dest/tests/01_setup.html");
				will(returnValue(count1));
			oneOf(fileRunner).run("/src/tests/02_tests/01_file1.html",
					"/dest/tests/02_tests/01_file1.html");
				will(returnValue(count2));
			oneOf(fileRunner).run("/src/tests/02_tests/03_file2.HTM",
					"/dest/tests/02_tests/03_file2.HTM");
				will(returnValue(null));
			oneOf(fileRunner).run("/src/tests/02_tests/02_subtests/testcase.html",
					"/dest/tests/02_tests/02_subtests/testcase.html");
				will(returnValue(null));
			oneOf(fileRunner).run("/src/tests/02_tests/02_subtests/test.html",
					"/dest/tests/02_tests/02_subtests/test.html");
				will(returnValue(count3));
			oneOf(fileRunner).run("/src/tests/03_tests/99_lasttest.html",
					"/dest/tests/03_tests/99_lasttest.html");
				will(returnValue(null));
		}});

		return new Counts[]{null, count1, count2, null, null, count3, null};
	}

	public final void testRunFile() {
		final Runner fileRunner = mock(Runner.class);
		prepareRun(fileRunner);

		runner.runFiles(fileRunner, null, null);
	}

	public final void testRunFilesWithResults() {
		final Runner fileRunner = mock(Runner.class);
		final FitResult fitResult = mock(FitResult.class);
		final Counts[] counts = prepareRun(fileRunner);

		checking(new Expectations() {{
			oneOf(fitResult).put("tests/01_setup.html", counts[1]);
			oneOf(fitResult).put("tests/02_tests/01_file1.html", counts[2]);
			oneOf(fitResult).put("tests/02_tests/03_file2.HTM", counts[3]);
			oneOf(fitResult).put("tests/02_tests/02_subtests/testcase.html", counts[4]);
			oneOf(fitResult).put("tests/02_tests/02_subtests/test.html", counts[5]);
			oneOf(fitResult).put("tests/03_tests/99_lasttest.html", counts[6]);
			oneOf(fitResult).put("tests/99_teardown.htm", counts[0]);
		}});

		runner.runFiles(fileRunner, fitResult, null);
	}
	
	public final void testRunFilesReturnWhenRight() {
		final Runner fileRunner = mock(Runner.class);
		final FitResult fitResult = mock(FitResult.class);
		final Counts counts = new Counts();
		boolean result;
		
		counts.right = 4;
		result = runWithSimpleResults(fileRunner, fitResult, counts);
		assertTrue(result);
	}
	
	public final void testRunFilesReturnWhenExceptions() {
		final Runner fileRunner = mock(Runner.class);
		final FitResult fitResult = mock(FitResult.class);
		final Counts counts = new Counts();
		boolean result;
		
		counts.right = 1;
		counts.exceptions = 1;
		result = runWithSimpleResults(fileRunner, fitResult, counts);
		assertFalse(result);
	}

	public final void testRunFilesReturnWhenIgnored() {
		final Runner fileRunner = mock(Runner.class);
		final FitResult fitResult = mock(FitResult.class);
		final Counts counts = new Counts();
		boolean result;
		
		counts.right = 1;
		counts.ignores = 1;
		result = runWithSimpleResults(fileRunner, fitResult, counts);
		assertTrue(result);
	}
	
	public final void testRunFilesReturnWhenWrong() {
		final Runner fileRunner = mock(Runner.class);
		final FitResult fitResult = mock(FitResult.class);
		final Counts counts = new Counts();
		boolean result;
		
		counts.right = 1;
		counts.wrong = 7;
		result = runWithSimpleResults(fileRunner, fitResult, counts);
		assertFalse(result);
	}
	
	private boolean runWithSimpleResults(final Runner fileRunner,
			final FitResult fitResult, final Counts counts) {
		checking(new Expectations() {{
			oneOf(fitResult).put("tests/01_setup.html", counts);
			oneOf(fitResult).put("tests/02_tests/01_file1.html", counts);
			oneOf(fitResult).put("tests/02_tests/03_file2.HTM", counts);
			oneOf(fitResult).put("tests/02_tests/02_subtests/testcase.html", counts);
			oneOf(fitResult).put("tests/02_tests/02_subtests/test.html", counts);
			oneOf(fitResult).put("tests/03_tests/99_lasttest.html", counts);
			oneOf(fitResult).put("tests/99_teardown.htm", counts);
		}});
		
		checking(new Expectations() {{
			oneOf(fileRunner).run("/src/tests/99_teardown.htm",
					"/dest/tests/99_teardown.htm");
				will(returnValue(counts));
			oneOf(fileRunner).run("/src/tests/01_setup.html",
					"/dest/tests/01_setup.html");
				will(returnValue(counts));
			oneOf(fileRunner).run("/src/tests/02_tests/01_file1.html",
					"/dest/tests/02_tests/01_file1.html");
				will(returnValue(counts));
			oneOf(fileRunner).run("/src/tests/02_tests/03_file2.HTM",
					"/dest/tests/02_tests/03_file2.HTM");
				will(returnValue(counts));
			oneOf(fileRunner).run("/src/tests/02_tests/02_subtests/testcase.html",
					"/dest/tests/02_tests/02_subtests/testcase.html");
				will(returnValue(counts));
			oneOf(fileRunner).run("/src/tests/02_tests/02_subtests/test.html",
					"/dest/tests/02_tests/02_subtests/test.html");
				will(returnValue(counts));
			oneOf(fileRunner).run("/src/tests/03_tests/99_lasttest.html",
					"/dest/tests/03_tests/99_lasttest.html");
				will(returnValue(counts));
		}});
		
		boolean result = runner.runFiles(fileRunner, fitResult, null);
		
		
		return result;
	}
}
