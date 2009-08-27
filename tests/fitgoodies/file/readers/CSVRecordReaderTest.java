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


package fitgoodies.file.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

/**
 * $Id: CSVRecordReaderTest.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public class CSVRecordReaderTest extends TestCase {
	public final BufferedReader mkReader(final String content) {
		StringReader sr = new StringReader(content);
		return new BufferedReader(sr);
	}

	public final void testReading() throws IOException {
		FileRecordReader reader = new CSVRecordReader(
				mkReader("this|is|a|test\n"
						+ "with|\"some|special\"|cases\n"
						+ "three|\"\"lines\"\""), '|', '"');

		assertTrue(reader.canRead());
		assertEquals("this", reader.nextField());
		assertEquals("is", reader.nextField());
		assertEquals("a", reader.nextField());
		assertEquals("test", reader.nextField());
		assertNull(reader.nextField());
		assertNull(reader.nextField());

		assertTrue(reader.nextRecord());
		assertTrue(reader.canRead());
		assertEquals("with", reader.nextField());
		assertEquals("some|special", reader.nextField());
		assertEquals("cases", reader.nextField());
		assertNull(reader.nextField());

		assertTrue(reader.nextRecord());
		assertTrue(reader.canRead());
		assertEquals("three", reader.nextField());
		assertEquals("\"lines\"", reader.nextField());
		assertNull(reader.nextField());

		assertFalse(reader.nextRecord());
		assertFalse(reader.canRead());
		reader.close();
	}
}
