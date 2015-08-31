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

package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesFixtureTestCase;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class FixedLengthFileRecordFixtureTest extends FitGoodiesFixtureTestCase<FixedLengthFileRecordFixture> {

	@Override
	protected Class<FixedLengthFileRecordFixture> getFixtureClass() {
		return FixedLengthFileRecordFixture.class;
	}

	@Test
	public void testExtractWidth1() {
		useTable(tr("1", "7", "4"));

		preparePreprocessWithConversion(Integer.class, "1", 1);
		preparePreprocessWithConversion(Integer.class, "7", 7);
		preparePreprocessWithConversion(Integer.class, "4", 4);

		int[] actual = fixture.extractWidth(lastFitTable.rows().get(0));

        lastFitTable.finishExecution();

        assertThat(actual.length, is(equalTo((Object) 3)));
		assertThat(actual[0], is(equalTo((Object) 1)));
		assertThat(actual[1], is(equalTo((Object) 7)));
		assertThat(actual[2], is(equalTo((Object) 4)));
	}

	@Test
	public void testExtractWidth2() {
		useTable(tr("3", "1", "9", "0"));

		preparePreprocessWithConversion(Integer.class, "3", 4);
		preparePreprocessWithConversion(Integer.class, "1", 2);
		preparePreprocessWithConversion(Integer.class, "9", 10);
		preparePreprocessWithConversion(Integer.class, "0", 0);

		int[] actual = fixture.extractWidth(lastFitTable.rows().get(0));

		assertThat(actual.length, is(equalTo((Object) 4)));
		assertThat(actual[0], is(equalTo((Object) 4)));
		assertThat(actual[1], is(equalTo((Object) 2)));
		assertThat(actual[2], is(equalTo((Object) 10)));
		assertThat(actual[3], is(equalTo((Object) 0)));

	}

	@Test
	public void testErrors() {
		useTable(tr("1", "error", "4"));

        preparePreprocessWithConversion(Integer.class, "1", 1);
        preparePreprocessWithConversionError(Integer.class, "error");
        preparePreprocessWithConversion(Integer.class, "4", 4);

        int[] actual = fixture.extractWidth(lastFitTable.rows().get(0));
        lastFitTable.finishExecution();

		assertThat(actual, is(nullValue()));

		assertCounts(0, 0, 0, 1);
        assertThat(htmlAt(0, 1), containsString("class=\"fit-expected\""));
	}

}
