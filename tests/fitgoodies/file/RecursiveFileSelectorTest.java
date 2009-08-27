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


package fitgoodies.file;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jmock.Expectations;

import fitgoodies.FitGoodiesTestCase;

/**
 * $Id: RecursiveFileSelectorTest.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public class RecursiveFileSelectorTest extends FitGoodiesTestCase {
	public final void testIterator() throws FileNotFoundException {
		final DirectoryProvider dirMock = prepareMock();

		RecursiveFileSelector selector = new RecursiveFileSelector(dirMock, ".*2.*");

		assertEquals("d/file2.txt", selector.next().fullname());
		assertEquals("d/s1/file2.bat", selector.next().fullname());
		assertEquals("d/s1/s2/src2.java", selector.next().fullname());
		assertFalse(selector.hasNext());
	}

	public final void testIterator2() throws FileNotFoundException {
		final DirectoryProvider dirMock = prepareMock();

		RecursiveFileSelector selector = new RecursiveFileSelector(dirMock, ".*\\.java");

		assertEquals("d/src3.java", selector.next().fullname());
		assertEquals("d/s1/s2/src1.java", selector.next().fullname());
		assertEquals("d/s1/s2/src2.java", selector.next().fullname());
		assertEquals("d/a/src3.java", selector.next().fullname());
		assertFalse(selector.hasNext());
	}

	private DirectoryProvider prepareMock() throws FileNotFoundException {
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

		dir1.add(new FileInformationMock("d/", "file1.txt", null));
		dir1.add(new FileInformationMock("d/", "file2.txt", null));
		dir1.add(new FileInformationMock("d/", "src3.java", null));
		dir2.add(new FileInformationMock("d/s1/", "file1.bat", null));
		dir2.add(new FileInformationMock("d/s1/", "file2.bat", null));
		dir3.add(new FileInformationMock("d/s1/s2/", "src1.java", null));
		dir3.add(new FileInformationMock("d/s1/s2/", "src2.java", null));
		dir3.add(new FileInformationMock("d/a/", "src3.java", null));
		dir4.add(new FileInformationMock("d/a/", "program.class", null));

		checking(new Expectations() {{
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

	public final void testErrorHandling() {
		RecursiveFileSelector selector = new RecursiveFileSelector(
				new DirectoryProvider() {
					@Override public Iterator<DirectoryProvider> getDirectories()
							throws FileNotFoundException {
						throw new FileNotFoundException();
					}

					@Override public Iterator<FileInformation> getFiles()
							throws FileNotFoundException {
						throw new FileNotFoundException();
					}

					@Override public String getPath() { return null; }
				}, "");

		try {
			selector.next();
			fail("expected FileNotFoundException");
		} catch (RuntimeException e) {
		}
	}
}
