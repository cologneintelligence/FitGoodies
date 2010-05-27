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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;

import fitgoodies.FitGoodiesTestCase;

public class FileInformationTest extends FitGoodiesTestCase {
	public final void testOpenBufferedReader() throws IOException {
		FileFixtureHelper.instance().setEncoding("iso-8859-1");

		FileInformationMock file = new FileInformationMock("C:\\dir",
				"test.txt", "line1\nline2\näöü".getBytes(Charset.forName("iso-8859-1")));

		BufferedReader br = file.openBufferedReader();
		assertEquals("line1", br.readLine());
		assertEquals("line2", br.readLine());
		assertEquals("äöü", br.readLine());
		br.close();

		FileFixtureHelper.instance().setEncoding("utf-16");
		file = new FileInformationMock("C:\\dir",
				"test.txt", "äöü".getBytes(Charset.forName("utf-16")));
		br = file.openBufferedReader();
		assertEquals("äöü", br.readLine());
		br.close();
	}

	public final void testOpenBufferedReaderString() throws IOException {
		FileInformationMock file = new FileInformationMock("C:\\dir",
				"test.txt", "line1\nüß".getBytes(Charset.forName("iso-8859-1")));

		BufferedReader br = file.openBufferedReader("iso-8859-1");
		assertEquals("line1", br.readLine());
		assertEquals("üß", br.readLine());
		br.close();

		file = new FileInformationMock("C:\\dir",
				"test.txt", "äöü".getBytes(Charset.forName("utf-16")));
		br = file.openBufferedReader("utf-16");
		assertEquals("äöü", br.readLine());
		br.close();
	}

}
