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

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;


public class FixedLengthRecordReaderTest extends FitGoodiesTestCase {
	public final BufferedReader mkReader(final String content) {
		StringReader sr = new StringReader(content);
		return new BufferedReader(sr);
	}

	@Test
	public void testReadingWithoutNewlines() throws IOException {
		FileRecordReader reader = new FixedLengthRecordReader(
				//        1       8    12      18
				mkReader("this    is   record  1"
					   + "and     this record  2"
					   + "and     a    record  3"),
				new int[]{8, 5, 8, 1}, false);

		assertTrue(reader.canRead());
		assertEquals("this    ", reader.nextField());
		assertEquals("is   ", reader.nextField());
		assertEquals("record  ", reader.nextField());
		assertEquals("1", reader.nextField());
		assertNull(reader.nextField());
		assertNull(reader.nextField());

		assertTrue(reader.nextRecord());
		assertTrue(reader.canRead());
		assertEquals("and     ", reader.nextField());
		assertEquals("this ", reader.nextField());
		assertEquals("record  ", reader.nextField());
		assertEquals("2", reader.nextField());
		assertNull(reader.nextField());

		assertTrue(reader.nextRecord());
		assertTrue(reader.canRead());
		assertEquals("and     ", reader.nextField());
		assertEquals("a    ", reader.nextField());
		assertEquals("record  ", reader.nextField());
		assertEquals("3", reader.nextField());
		assertNull(reader.nextField());
		assertFalse(reader.nextRecord());
		assertFalse(reader.canRead());

		reader.close();
	}

	@Test
	public void testReadingWithNewlines() throws IOException {
		FileRecordReader reader = new FixedLengthRecordReader(
				mkReader("x y\n2 4\nj w"),
				new int[]{2, 1}, true);

		assertTrue(reader.canRead());
		assertEquals("x ", reader.nextField());
		assertEquals("y", reader.nextField());
		assertNull(reader.nextField());

		assertTrue(reader.nextRecord());
		assertTrue(reader.canRead());
		assertEquals("2 ", reader.nextField());
		assertEquals("4", reader.nextField());
		assertNull(reader.nextField());

		assertTrue(reader.nextRecord());
		assertTrue(reader.canRead());
		assertEquals("j ", reader.nextField());
		assertEquals("w", reader.nextField());
		assertNull(reader.nextField());
		assertFalse(reader.nextRecord());
		assertFalse(reader.canRead());

		reader.close();
	}
}
