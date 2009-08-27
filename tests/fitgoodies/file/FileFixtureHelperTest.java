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

import fitgoodies.FitGoodiesTestCase;

/**
 * $Id: FileFixtureHelperTest.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public class FileFixtureHelperTest extends FitGoodiesTestCase {
	public final void testSingleton() {
		FileFixtureHelper expected = FileFixtureHelper.instance();
		assertSame(expected, FileFixtureHelper.instance());

		FileFixtureHelper.reset();
		assertNotSame(expected, FileFixtureHelper.instance());
	}

	public final void testSelector() throws FileNotFoundException {
		FileFixtureHelper.instance().setEncoding("utf-8");
		FileFixtureHelper.instance().setPattern(".*");
		FileFixtureHelper.instance().setProvider(new DirectoryProviderMock());

		FileSelector fs = FileFixtureHelper.instance().getSelector();
		assertEquals("file1.txt", fs.getFirstFile().filename());

		fs = FileFixtureHelper.selector();
		assertEquals("file1.txt", fs.getFirstFile().filename());

		FileFixtureHelper.instance().setPattern(".*\\.bat");
		fs = FileFixtureHelper.instance().getSelector();
		assertEquals("f.txt.bat", fs.getFirstFile().filename());

		fs = FileFixtureHelper.selector();
		assertEquals("f.txt.bat", fs.getFirstFile().filename());
	}

    public final void testEncoding() {
		FileFixtureHelper.instance().setEncoding("utf-8");
		assertEquals("utf-8", FileFixtureHelper.instance().getEncoding());
		assertEquals("utf-8", FileFixtureHelper.encoding());

		FileFixtureHelper.instance().setEncoding("latin-1");
		assertEquals("latin-1", FileFixtureHelper.instance().getEncoding());
		assertEquals("latin-1", FileFixtureHelper.encoding());
	}

	public final void testPattern() {
		FileFixtureHelper.instance().setPattern("*\\.txt");
		assertEquals("*\\.txt", FileFixtureHelper.instance().getPattern());
		assertEquals("*\\.txt", FileFixtureHelper.pattern());

		FileFixtureHelper.instance().setPattern("*\\.bat");
		assertEquals("*\\.bat", FileFixtureHelper.instance().getPattern());
		assertEquals("*\\.bat", FileFixtureHelper.pattern());
	}

	public final void testDirectory() {
		FileFixtureHelper.instance().setProvider(new DirectoryProviderMock());
		assertEquals("/test",
				FileFixtureHelper.instance().getProvider().getPath());
	}
}
