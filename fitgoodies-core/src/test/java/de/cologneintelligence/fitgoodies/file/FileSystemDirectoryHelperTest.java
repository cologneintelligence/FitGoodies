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

package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public final class FileSystemDirectoryHelperTest extends FitGoodiesTestCase {
	private FileSystemDirectoryHelper helper;

	@Before
	public void setUpMocks() throws Exception {
		helper = new FileSystemDirectoryHelper();
	}

	@Test
	public void testRemovePrefix() {
		assertThat(helper.removePrefix(path("/a/long/path"), path("/a")), is(equalTo(path("long/path"))));
		assertThat(helper.removePrefix(path("/a/long/path"), path("/a/")), is(equalTo(path("long/path"))));
		assertThat(helper.removePrefix(path("something/different"), path("something/")), is(equalTo(path("different"))));
		assertThat(helper.removePrefix(path("something/different"), path("something")), is(equalTo(path("different"))));
		assertThat(helper.removePrefix(path("something///different"), path("something")), is(equalTo(path("different"))));
		assertThat(helper.removePrefix(path("/a/long/path"), path("/x")), is(equalTo(path("/a/long/path"))));
	}

	@Test
	public void testIsSubdir() throws IOException {
		assertThat(helper.isSubDir(new File("/a/b/c/d"), new File("/a/b/c")), is(true));
		assertThat(helper.isSubDir(new File("/a/b/c/d"), new File("/a")), is(true));
		assertThat(helper.isSubDir(new File("/a/b/c/d"), new File("/abc")), is(false));

		assertThat(helper.isSubDir(new File("/test/dir"), new File("/test/other")), is(false));

		assertThat(helper.isSubDir(new File("/test/dir/other"), new File("/test/dir")), is(true));
		assertThat(helper.isSubDir(new File("a/b/c/d"), new File("a/b")), is(true));

		assertThat(helper.isSubDir(new File("/a"), new File("/a/b/c")), is(false));

		assertThat(helper.isSubDir(new File("dir"), new File("dir")), is(true));
	}

	@Test
	public void testCommonDirs() throws IOException {
		assertThat(helper.getCommonDir(new File("a/b/c"), new File("a/b/f")),
				is(equalTo(new File("a/b/").getAbsoluteFile())));
		assertThat(helper.getCommonDir(new File("/a/b/c"), new File("/a/f/c")),
				is(equalTo(new File("/a/").getAbsoluteFile())));
		assertThat(helper.getCommonDir(new File("/a/b/c/"), new File("/a/b/c/")),
				is(equalTo(new File("/a/b/c/").getAbsoluteFile())));

		assertThat(helper.getCommonDir(new File("a"), new File("b")),
				is(equalTo(new File("").getAbsoluteFile())));

		assertThat(helper.getCommonDir(new File("a"), new File("a")),
				is(equalTo(new File("a/").getAbsoluteFile())));

		assertThat(helper.getCommonDir(new File("/a"), new File("/b")),
				is(equalTo(new File("/").getAbsoluteFile())));


		assertThat(helper.getCommonDir(new File("/a/b"), new File("/a/c/d")),
				is(equalTo(new File("/a").getAbsoluteFile())));
	}

	@Test
	public void testParentDirs() throws IOException {
		File[] actual;
		File[] expected;

		actual = helper.getParentDirs(new File("a"), new File("a/b/c"));
		expected = new File[]{new File("a/b/").getCanonicalFile(), new File("a/b/c/").getCanonicalFile()};
		assertThat(Arrays.asList(actual), is(equalTo(Arrays.asList(expected))));

		actual = helper.getParentDirs(new File("x"), new File("x/y/"));
		expected = new File[]{new File("x/y/").getCanonicalFile()};
		assertThat(Arrays.asList(actual), is(equalTo(Arrays.asList(expected))));

		actual = helper.getParentDirs(new File("dir1/dir2/"), new File("dir1/dir2/dir3/dir4"));
		expected = new File[]{new File("dir1/dir2/dir3/").getCanonicalFile(),
				new File("dir1/dir2/dir3/dir4/").getCanonicalFile()};
		assertThat(Arrays.asList(actual), is(equalTo(Arrays.asList(expected))));

		actual = helper.getParentDirs(new File("dir1/dir2"), new File("dir1/dir2/dir3/dir4"));
		expected = new File[]{new File("dir1/dir2/dir3/").getCanonicalFile(),
				new File("dir1/dir2/dir3/dir4/").getCanonicalFile()};
		assertThat(Arrays.asList(actual), is(equalTo(Arrays.asList(expected))));

		actual = helper.getParentDirs(new File("/dir1/dir2/"), new File("/dir1/dir2/dir3/dir4"));
		expected = new File[]{new File("/dir1/dir2/dir3/").getCanonicalFile(),
				new File("/dir1/dir2/dir3/dir4/").getCanonicalFile()};
		assertThat(Arrays.asList(actual), is(equalTo(Arrays.asList(expected))));
	}

	@Test
	public void testAbs2Rel() {
		// test if path already is relative
		// test with empty parameters?
		assertThat(helper.abs2rel(path("/my/path"), path("/my/path/of/file")),
				is(equalTo(path("of/file"))));
		assertThat(helper.abs2rel(path("/my/path"), path("/my/path/of/another/file")),
				is(equalTo(path("of/another/file"))));

		assertThat(helper.abs2rel(path("/my/path"), path("/my/projects/file")),
				is(equalTo(path("../projects/file"))));

		assertThat(helper.abs2rel(path("/a/b/c/d"), path("/a/b/c/")),
				is(equalTo("..")));

		assertThat(helper.abs2rel(path("d:/projects"), path("c:/test")),
				is(equalTo(path("c:/test"))));
		assertThat(helper.abs2rel(path("/x/y"), path("/y/x")), is(equalTo(path("../../y/x"))));

		assertThat(helper.abs2rel(path("/please/ignore/me"), path("x/y")),
				is(equalTo(path("x/y"))));
	}

	private String path(String path) {
		return path.replace('/', File.separatorChar);
	}

	private File file(String path) {
		return new File(path(path));
	}

	@Test
	public void testRel2Abs() {
		// test if path already is absolute
		// test with empty parameters?
		assertThat(helper.rel2abs(path("/this/is/a/test"), path("../../my/project")),
				is(equalTo(file("/this/is/my/project"))));

		assertThat(helper.rel2abs(path("/files/documents/"), path("projects")),
				is(equalTo(file("/files/documents/projects"))));

		assertThat(helper.rel2abs(path("/no/"), path("../../../../problem")),
				is(equalTo(file("/problem"))));

		assertThat(helper.rel2abs(path("c:/no/"), path("../../../../problem")),
				is(equalTo(file("c:/problem"))));

		assertThat(helper.rel2abs(path("/please/ignore/me"), path("/test")),
				is(equalTo(file("/test"))));
	}
}
