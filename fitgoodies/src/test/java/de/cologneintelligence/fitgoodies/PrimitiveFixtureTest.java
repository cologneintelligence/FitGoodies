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

import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import org.junit.Test;

public class PrimitiveFixtureTest extends FitGoodiesFixtureTestCase<PrimitiveFixture> {

    @Override
    protected Class<PrimitiveFixture> getFixtureClass() {
        return PrimitiveFixture.class;
    }

	@Test
	public void testLongRight() {
        FitCell cell = parseTd("-15");
		fixture.check(cell, -15L);
		assertResult(true);
	}

	@Test
	public void testLongWrong() {
		FitCell cell = parseTd("12");
		fixture.check(cell, 42L);
		assertResult(false);
	}

	@Test
	public void testIntRight() {
		FitCell cell = parseTd("-15");
		fixture.check(cell, -15);
		assertResult(true);
	}

	@Test
	public void testIntWrong() {
		FitCell cell = parseTd("12");
		fixture.check(cell, 42);
		assertResult(false);
	}

	@Test
	public void testBoolRight() {
		FitCell cell = parseTd("true");
		fixture.check(cell, true);
		assertResult(true);
	}

	@Test
	public void testBoolWrong() {
		FitCell cell = parseTd("false");
		fixture.check(cell, true);
		assertResult(false);
	}

	@Test
	public void testDoubleRight() {
		FitCell cell = parseTd("1");
		fixture.check(cell, 1.0);
		assertResult(true);
	}

	@Test
	public void testDoubleWrong() {
		FitCell cell = parseTd("2");
		fixture.check(cell, 2.5);
		assertResult(false);
	}

	@Test
	public void testStringRight() {
		FitCell cell = parseTd("bla");
		fixture.check(cell, "bla");
		assertResult(true);
	}

	@Test
	public void testStringWrong() {
		FitCell cell = parseTd("foo");
		fixture.check(cell, "bar");
		assertResult(false);
	}

	private void assertResult(boolean correct) {
        lastFitTable.finishExecution();
		assertCounts(correct ? 1 : 0, correct ? 0 : 1, 0, 0);
	}

}
