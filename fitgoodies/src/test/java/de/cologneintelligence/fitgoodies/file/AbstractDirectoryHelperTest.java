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


package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.file.AbstractDirectoryHelper;

/**
 * $Id$
 * @author jwierum
 */
public final class AbstractDirectoryHelperTest extends FitGoodiesTestCase {
	private AbstractDirectoryHelper helper;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		helper = new DirectoryHelperMock();
	}

	public void testJoin() {
		assertEquals("a/b", helper.join("a", "b"));
		assertEquals("a/b/c", helper.join("a/", "b/c"));
		assertEquals("x/y/b/c", helper.join("x/y", "b/c"));
	}

	public void testRemovePrefix() {
		assertEquals("long/path", helper.removePrefix("/a/long/path", "/a"));
		assertEquals("long/path", helper.removePrefix("/a/long/path", "/a/"));
		assertEquals("different", helper.removePrefix("something/different", "something/"));
		assertEquals("different", helper.removePrefix("something/different", "something"));
		assertEquals("different", helper.removePrefix("something///different", "something"));
		assertEquals("/a/long/path", helper.removePrefix("/a/long/path", "/x"));
	}

	public void testIsSubdir() {
		assertTrue(helper.isSubDir("/a/b/c/d", "/a/b/c"));
		assertTrue(helper.isSubDir("/a/b/c/d", "/a"));
		assertFalse(helper.isSubDir("/a/b/c/d", "/abc"));
		assertFalse(helper.isSubDir("/test/dir", "/test/other"));
		assertTrue(helper.isSubDir("/test/dir/other", "/test/dir"));
		assertTrue(helper.isSubDir("a/b/c/d", "a/b"));

		assertFalse(helper.isSubDir("/a", "/a/b/c"));

		assertTrue(helper.isSubDir("/a", ""));
		assertFalse(helper.isSubDir("", "/a"));
		assertTrue(helper.isSubDir("", ""));

		assertTrue(helper.isSubDir("dir", "dir"));
	}

	/*
	public void testCommonDirs() {
		assertEquals("a/b/", helper.getCommonDir("a/b/c", "a/b/f"));
		assertEquals("/a/", helper.getCommonDir("/a/b/c", "/a/f/c"));
		assertEquals("/a/b/c/", helper.getCommonDir("/a/b/c/", "/a/b/c/"));

		assertEquals("", helper.getCommonDir("a", "b"));
		assertEquals("a/", helper.getCommonDir("a", "a"));
		assertEquals("/", helper.getCommonDir("/a", "/b"));
	}*/

	public void testParentDirs() {
		String[] expected;
		String[] actual;

		actual = helper.getParentDirs("", "a/b/c");
		expected = new String[]{"a/", "a/b/", "a/b/c/"};
		assertArray(expected, actual);

		actual = helper.getParentDirs("", "x/y/");
		expected = new String[]{"x/", "x/y/"};
		assertArray(expected, actual);

		actual = helper.getParentDirs("dir1/dir2/", "dir1/dir2/dir3/dir4");
		expected = new String[]{"dir1/dir2/dir3/", "dir1/dir2/dir3/dir4/"};
		assertArray(expected, actual);

		actual = helper.getParentDirs("dir1/dir2", "dir1/dir2/dir3/dir4");
		expected = new String[]{"dir1/dir2/dir3/", "dir1/dir2/dir3/dir4/"};
		assertArray(expected, actual);

		actual = helper.getParentDirs("/dir1/dir2/", "/dir1/dir2/dir3/dir4");
		expected = new String[]{"/dir1/dir2/dir3/", "/dir1/dir2/dir3/dir4/"};
		assertArray(expected, actual);
	}

	public void testAbs2Rel() {
		// test if path already is relative
		// test with empty parameters?
		assertEquals("path/of/file",
				helper.abs2rel("/my/path", "/my/path/path/of/file"));
		assertEquals("path/of/another/file",
				helper.abs2rel("/my/path", "/my/path/path/of/another/file"));

		assertEquals("../projects/file",
				helper.abs2rel("/my/path", "/my/projects/file"));

		assertEquals("..",
				helper.abs2rel("/a/b/c/d", "/a/b/c/"));

		assertEquals("c:/test", helper.abs2rel("d:/projects", "c:/test"));
		assertEquals("../../y/x", helper.abs2rel("/x/y", "/y/x"));

		assertEquals("x/y",
				helper.abs2rel("/please/ignore/me", "x/y"));
	}

	public void testRel2Abs() {
		// test if path already is absolute
		// test with empty parameters?
		assertEquals("/this/is/my/project",
				helper.rel2abs("/this/is/a/test", "../../my/project"));

		assertEquals("/files/documents/projects",
				helper.rel2abs("/files/documents/", "projects"));

		assertEquals("/problem",
				helper.rel2abs("/no/", "../../../../problem"));

		assertEquals("c:/problem",
				helper.rel2abs("c:/no/", "../../../../problem"));

		assertEquals("/test",
				helper.rel2abs("/please/ignore/me", "/test"));
	}
}
