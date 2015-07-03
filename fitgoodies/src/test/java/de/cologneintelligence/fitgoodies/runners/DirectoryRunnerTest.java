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

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Ignore;


@Ignore
public class DirectoryRunnerTest extends FitGoodiesTestCase {
//	private DirectoryRunner runner;
//	private DirectoryHelperMock helper;
//
//	@Before
//	public void setupMocks() throws Exception {
//		super.setUp();
//		helper = new DirectoryHelperMock();
//		runner = new DirectoryRunner(mockFiles(),
//				"/dest", "utf-8", helper);
//	}
//
//	public final DirectoryProvider mockFiles() throws FileNotFoundException {
//		final DirectoryProvider provider1 =
//			mock(DirectoryProvider.class, "provider1");
//		final DirectoryProvider provider2 =
//			mock(DirectoryProvider.class, "provider2");
//		final DirectoryProvider provider3 =
//			mock(DirectoryProvider.class, "provider3");
//		final DirectoryProvider provider4 =
//			mock(DirectoryProvider.class, "provider4");
//
//		final List<FileInformation> dir1 = new ArrayList<FileInformation>();
//		final List<FileInformation> dir2 = new ArrayList<FileInformation>();
//		final List<FileInformation> dir3 = new ArrayList<FileInformation>();
//		final List<FileInformation> dir4 = new ArrayList<FileInformation>();
//
//		dir1.add(new FileInformationMock("/src/tests/", "99_teardown.htm", null));
//		dir1.add(new FileInformationMock("/src/tests/", "01_setup.html", null));
//		dir1.add(new FileInformationMock("/src/tests/", "readme.txt", null));
//		dir2.add(new FileInformationMock("/src/tests/02_tests/", "01_file1.html", null));
//		dir2.add(new FileInformationMock("/src/tests/02_tests/", "03_file2.HTM", null));
//		dir3.add(new FileInformationMock("/src/tests/02_tests/02_subtests/",
//				"testcase.html", null));
//		dir3.add(new FileInformationMock("/src/tests/02_tests/02_subtests/",
//				"myfixture.class", null));
//		dir3.add(new FileInformationMock("/src/tests/02_tests/02_subtests/", "test.html", null));
//		dir4.add(new FileInformationMock("/src/tests/03_tests/", "99_lasttest.html", null));
//
//		when(provider1.getPath()).thenReturn("/src");
//		when(provider1.getFiles()).thenReturn(dir2.iterator());
//		when(provider2.getFiles()).thenReturn(dir2.iterator());
//		when(provider3.getFiles()).thenReturn(dir3.iterator());
//		when(provider4.getFiles()).thenReturn(dir4.iterator());
//		when(provider1.getDirectories()).thenReturn(Arrays.asList(provider2, provider4).iterator());
//		when(provider2.getDirectories()).thenReturn(Arrays.asList(provider3).iterator());
//		when(provider3.getDirectories()).thenReturn(Arrays.<DirectoryProvider>asList().iterator());
//		when(provider4.getDirectories()).thenReturn(Arrays.<DirectoryProvider>asList().iterator());
//		return provider1;
//	}
//
//	@Test
//	@Ignore
//	public void testGetRelevantFiles() {
//		//FileInformation[] files = runner.getRelevantFiles();
//		FileInformation[] files = null;
//
//		final int numberOfFiles = 7;
//		Assert.assertEquals(numberOfFiles, files.length);
//		Assert.assertEquals("/src/tests/01_setup.html", files[0].fullname());
//		Assert.assertEquals("/src/tests/02_tests/02_subtests/test.html", files[2].fullname());
//		Assert.assertEquals("/src/tests/02_tests/03_file2.HTM", files[4].fullname());
//		Assert.assertEquals("/src/tests/99_teardown.htm", files[6].fullname());
//	}
//
//	@Test
//	@Ignore
//	public void testPrepareDirectories() {
//		//runner.prepareDirectories(runner.getRelevantFiles());
//
//		final int numberOfFiles = 7;
//		//String[] paths = helper.getPathes();
//		String[] paths = null;
//		Assert.assertEquals(numberOfFiles, paths.length);
//		Assert.assertThat(Arrays.asList(paths), Matchers.hasItem("/dest/tests/02_tests/"));
//		Assert.assertThat(Arrays.asList(paths), Matchers.hasItem("/dest/tests/02_tests/02_subtests/"));
//		Assert.assertThat(Arrays.asList(paths), Matchers.hasItem("/dest/tests/03_tests/"));
//		Assert.assertThat(Arrays.asList(paths), not(hasItem("/dst/tests/02_tests/")));
//	}
//
//	@Test
//	@Ignore
//	public void testPrepareDirectories2() {
//		//runner.prepareDirectories(runner.getRelevantFiles());
//
//		final int numberOfFiles = 7;
//		//String[] paths = helper.getPathes();
//		String[] paths = null;
//		Assert.assertEquals(numberOfFiles, paths.length);
//		Assert.assertThat(Arrays.asList(paths), Matchers.hasItem("/dest/tests/02_tests/"));
//		assertThat(Arrays.asList(paths), Matchers.hasItem("/dest/tests/02_tests/02_subtests/"));
//		assertThat(Arrays.asList(paths), Matchers.hasItem("/dest/tests/03_tests/"));
//		assertThat(Arrays.asList(paths), not(hasItem("/destination/tests/02_tests/")));
//	}
//
//	public final fit.Counts[] prepareRun(final Runner fileRunner) {
//		final Counts count1 = new fit.Counts();
//		count1.exceptions = 1;
//		count1.ignores = 2;
//		count1.right = 42;
//		count1.wrong = 4;
//
//		final Counts count2 = new fit.Counts();
//		count2.right = 23;
//
//		final Counts count3 = new fit.Counts();
//		count3.exceptions = 4;
//
//			when(fileRunner.run(new File("/src/tests/99_teardown.htm"),
//					new File("/dest/tests/99_teardown.htm")))
//				.thenReturn(null);
//			when(fileRunner.run(new File("/src/tests/01_setup.html"),
//					new File("/dest/tests/01_setup.html")))
//				.thenReturn(count1);
//			when(fileRunner.run(new File("/src/tests/02_tests/01_file1.html"),
//					new File("/dest/tests/02_tests/01_file1.html")))
//				.thenReturn(count2);
//			when(fileRunner.run(new File("/src/tests/02_tests/03_file2.HTM"),
//					new File("/dest/tests/02_tests/03_file2.HTM")))
//				.thenReturn(null);
//			when(fileRunner.run(new File("/src/tests/02_tests/02_subtests/testcase.html"),
//					new File("/dest/tests/02_tests/02_subtests/testcase.html")))
//				.thenReturn(null);
//			when(fileRunner.run(new File("/src/tests/02_tests/02_subtests/test.html"),
//					new File("/dest/tests/02_tests/02_subtests/test.html")))
//				.thenReturn(count3);
//			when(fileRunner.run(new File("/src/tests/03_tests/99_lasttest.html"),
//					new File("/dest/tests/03_tests/99_lasttest.html")))
//				.thenReturn(null);
//
//		return new Counts[]{null, count1, count2, null, null, count3, null};
//	}
//
//	@Test
//	@Ignore
//	public void testRunFile() {
//		final Runner fileRunner = mock(Runner.class);
//		prepareRun(fileRunner);
//
//		//runner.runFiles(fileRunner, null, null);
//	}
//
//	@Test
//	public void testRunFilesWithResults() {
//		final Runner fileRunner = mock(Runner.class);
//		final FitResult fitResult = mock(FitResult.class);
//		final Counts[] counts = prepareRun(fileRunner);
//
//		checking(new Expectations() {{
//			oneOf(fitResult).put("tests/01_setup.html", counts[1]);
//			oneOf(fitResult).put("tests/02_tests/01_file1.html", counts[2]);
//			oneOf(fitResult).put("tests/02_tests/03_file2.HTM", counts[3]);
//			oneOf(fitResult).put("tests/02_tests/02_subtests/testcase.html", counts[4]);
//			oneOf(fitResult).put("tests/02_tests/02_subtests/test.html", counts[5]);
//			oneOf(fitResult).put("tests/03_tests/99_lasttest.html", counts[6]);
//			oneOf(fitResult).put("tests/99_teardown.htm", counts[0]);
//		}});
//
//		runner.runFiles(fileRunner, fitResult, null);
//	}
//
//	@Test
//	public void testRunFilesReturnWhenRight() {
//		final Runner fileRunner = mock(Runner.class);
//		final FitResult fitResult = mock(FitResult.class);
//		final Counts counts = new Counts();
//		boolean result;
//
//		counts.right = 4;
//		result = runWithSimpleResults(fileRunner, fitResult, counts);
//		assertTrue(result);
//	}
//
//	@Test
//	public void testRunFilesReturnWhenExceptions() {
//		final Runner fileRunner = mock(Runner.class);
//		final FitResult fitResult = mock(FitResult.class);
//		final Counts counts = new Counts();
//		boolean result;
//
//		counts.right = 1;
//		counts.exceptions = 1;
//		result = runWithSimpleResults(fileRunner, fitResult, counts);
//		assertFalse(result);
//	}
//
//	@Test
//	public void testRunFilesReturnWhenIgnored() {
//		final Runner fileRunner = mock(Runner.class);
//		final FitResult fitResult = mock(FitResult.class);
//		final Counts counts = new Counts();
//		boolean result;
//
//		counts.right = 1;
//		counts.ignores = 1;
//		result = runWithSimpleResults(fileRunner, fitResult, counts);
//		assertTrue(result);
//	}
//
//	@Test
//	public void testRunFilesReturnWhenWrong() {
//		final Runner fileRunner = mock(Runner.class);
//		final FitResult fitResult = mock(FitResult.class);
//		final Counts counts = new Counts();
//		boolean result;
//
//		counts.right = 1;
//		counts.wrong = 7;
//		result = runWithSimpleResults(fileRunner, fitResult, counts);
//		assertFalse(result);
//	}
//
//	private boolean runWithSimpleResults(final Runner fileRunner,
//			final FitResult fitResult, final Counts counts) {
//		checking(new Expectations() {{
//			oneOf(fitResult).put("tests/01_setup.html", counts);
//			oneOf(fitResult).put("tests/02_tests/01_file1.html", counts);
//			oneOf(fitResult).put("tests/02_tests/03_file2.HTM", counts);
//			oneOf(fitResult).put("tests/02_tests/02_subtests/testcase.html", counts);
//			oneOf(fitResult).put("tests/02_tests/02_subtests/test.html", counts);
//			oneOf(fitResult).put("tests/03_tests/99_lasttest.html", counts);
//			oneOf(fitResult).put("tests/99_teardown.htm", counts);
//		}});
//
//		checking(new Expectations() {{
//			oneOf(fileRunner).run("/src/tests/99_teardown.htm",
//					"/dest/tests/99_teardown.htm");
//				will(returnValue(counts));
//			oneOf(fileRunner).run("/src/tests/01_setup.html",
//					"/dest/tests/01_setup.html");
//				will(returnValue(counts));
//			oneOf(fileRunner).run("/src/tests/02_tests/01_file1.html",
//					"/dest/tests/02_tests/01_file1.html");
//				will(returnValue(counts));
//			oneOf(fileRunner).run("/src/tests/02_tests/03_file2.HTM",
//					"/dest/tests/02_tests/03_file2.HTM");
//				will(returnValue(counts));
//			oneOf(fileRunner).run("/src/tests/02_tests/02_subtests/testcase.html",
//					"/dest/tests/02_tests/02_subtests/testcase.html");
//				will(returnValue(counts));
//			oneOf(fileRunner).run("/src/tests/02_tests/02_subtests/test.html",
//					"/dest/tests/02_tests/02_subtests/test.html");
//				will(returnValue(counts));
//			oneOf(fileRunner).run("/src/tests/03_tests/99_lasttest.html",
//					"/dest/tests/03_tests/99_lasttest.html");
//				will(returnValue(counts));
//		}});
//
//		boolean result = runner.runFiles(fileRunner, fitResult, null);
//
//
//		return result;
//	}
}
