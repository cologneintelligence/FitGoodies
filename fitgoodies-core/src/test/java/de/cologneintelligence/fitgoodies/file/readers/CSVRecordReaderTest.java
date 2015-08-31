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

package de.cologneintelligence.fitgoodies.file.readers;

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;


public final class CSVRecordReaderTest extends FitGoodiesTestCase {
	public BufferedReader mkReader(final String content) {
		final StringReader sr = new StringReader(content);
		return new BufferedReader(sr);
	}

	@Test
	public void testReading() throws IOException {
		final FileRecordReader reader = new CSVRecordReader(
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

	@Test
	public void testReadingNoTrim() throws IOException {
		final CSVRecordReader reader = new CSVRecordReader(
				mkReader("this; is ;' a ';test\n"), ';', '\'');

		assertTrue(reader.canRead());
		assertEquals("this", reader.nextField());
		assertEquals(" is ", reader.nextField());
		assertEquals(" a ", reader.nextField());
		assertEquals("test", reader.nextField());
		assertNull(reader.nextField());
		assertFalse(reader.nextRecord());
		assertFalse(reader.canRead());
		reader.close();
	}

	@Test
	public void testReadingNewlines() throws IOException {
		final CSVRecordReader reader = new CSVRecordReader(
				mkReader("this;is;\"more\ntricky\"\nthan;it;looks"), ';', '"');

		assertTrue(reader.canRead());
		assertEquals("this", reader.nextField());
		assertEquals("is", reader.nextField());
		assertEquals("more\ntricky", reader.nextField());
		assertNull(reader.nextField());
		assertTrue(reader.nextRecord());
		assertEquals("than", reader.nextField());
		reader.close();
	}

	@Test(expected = RuntimeException.class)
	public void testReadingWithErrors1() throws IOException {
		new CSVRecordReader(mkReader("this;is;\"more\ntricky\nthan;it;looks"), ';', '"');
	}

	@Test(expected = RuntimeException.class)
	public void testReadingWithErrors2() throws IOException {
		final CSVRecordReader reader = new CSVRecordReader(mkReader(
				"x\nthis;is;\"more\ntricky\nthan;it;looks"), ';', '"');

		reader.nextField();
		reader.nextRecord();
	}
}
