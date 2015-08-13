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

package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Before;
import org.junit.Test;

public class PrimitiveFixtureTest extends FitGoodiesTestCase {

	private PrimitiveFixture fixture;

	@Before
	public void setUp() {
		fixture = new PrimitiveFixture();
	}

	@Test
	public void testLongRight() {
		Parse cell = parseTd("-15");
		fixture.check(cell, -15L);
		assertResult(cell, true);
	}

	@Test
	public void testLongWrong() {
		Parse cell = parseTd("12");
		fixture.check(cell, 42L);
		assertResult(cell, false);
	}

	@Test
	public void testIntRight() {
		Parse cell = parseTd("-15");
		fixture.check(cell, -15);
		assertResult(cell, true);
	}

	@Test
	public void testIntWrong() {
		Parse cell = parseTd("12");
		fixture.check(cell, 42);
		assertResult(cell, false);
	}

	@Test
	public void testBoolRight() {
		Parse cell = parseTd("true");
		fixture.check(cell, true);
		assertResult(cell, true);
	}

	@Test
	public void testBoolWrong() {
		Parse cell = parseTd("false");
		fixture.check(cell, true);
		assertResult(cell, false);
	}

	@Test
	public void testDoubleRight() {
		Parse cell = parseTd("1");
		fixture.check(cell, 1.0);
		assertResult(cell, true);
	}

	@Test
	public void testDoubleWrong() {
		Parse cell = parseTd("2");
		fixture.check(cell, 2.5);
		assertResult(cell, false);
	}

	@Test
	public void testStringRight() {
		Parse cell = parseTd("bla");
		fixture.check(cell, "bla");
		assertResult(cell, true);
	}

	@Test
	public void testStringWrong() {
		Parse cell = parseTd("foo");
		fixture.check(cell, "bar");
		assertResult(cell, false);
	}

	private void assertResult(Parse cell, boolean correct) {
		assertCounts(fixture.counts(), cell, correct ? 1 : 0, correct ? 0 : 1, 0, 0);
	}

}
