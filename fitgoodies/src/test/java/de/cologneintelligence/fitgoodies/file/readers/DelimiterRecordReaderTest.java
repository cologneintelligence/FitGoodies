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


package de.cologneintelligence.fitgoodies.file.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import de.cologneintelligence.fitgoodies.file.readers.DelimiterRecordReader;
import de.cologneintelligence.fitgoodies.file.readers.FileRecordReader;

import junit.framework.TestCase;

/**
 *
 * @author jwierum
 */
public class DelimiterRecordReaderTest extends TestCase {
	public final BufferedReader mkReader(final String content) {
		StringReader sr = new StringReader(content);
		return new BufferedReader(sr);
	}

	public final void testReading() throws IOException {
		FileRecordReader reader = new DelimiterRecordReader(
				mkReader("this|is|a|test\nwith|four|columns|and\nthree|rows"), "|");

		assertTrue(reader.canRead());
		assertEquals("this", reader.nextField());
		assertEquals("is", reader.nextField());
		assertEquals("a", reader.nextField());
		assertEquals("test", reader.nextField());
		assertNull(reader.nextField());
		assertNull(reader.nextField());
		assertTrue(reader.nextRecord());

		assertTrue(reader.nextRecord());
		assertTrue(reader.canRead());
		assertEquals("three", reader.nextField());
		assertEquals("rows", reader.nextField());
		assertNull(reader.nextField());
		assertFalse(reader.nextRecord());
		assertFalse(reader.canRead());

		reader.close();
	}
}
