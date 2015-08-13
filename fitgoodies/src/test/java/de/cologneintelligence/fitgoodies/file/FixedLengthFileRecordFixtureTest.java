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


package de.cologneintelligence.fitgoodies.file;

import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
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
        Parse row = parseTr("1", "7", "4");

        preparePreprocess("1", "1");
        preparePreprocess("7", "7");
        preparePreprocess("4", "4");

        int[] actual = fixture.extractWidth(row);

        assertThat(actual.length, is(equalTo((Object) 3)));
        assertThat(actual[0], is(equalTo((Object) 1)));
        assertThat(actual[1], is(equalTo((Object) 7)));
        assertThat(actual[2], is(equalTo((Object) 4)));
    }

    @Test
    public void testExtractWidth2() {
        Parse row = parseTr("3", "1", "9", "0");

        preparePreprocess("3", "4");
        preparePreprocess("1", "2");
        preparePreprocess("9", "10");
        preparePreprocess("0", "0");

        int[] actual = fixture.extractWidth(row);

        assertThat(actual.length, is(equalTo((Object) 4)));
        assertThat(actual[0], is(equalTo((Object) 3)));
        assertThat(actual[1], is(equalTo((Object) 1)));
        assertThat(actual[2], is(equalTo((Object) 9)));
        assertThat(actual[3], is(equalTo((Object) 0)));

    }

    @Test
    public void testErrors() {
        Parse table = parseTable(tr("1", "error", "4"));

        int[] actual = fixture.extractWidth(table.at(0, 1));

        assertThat(actual, is(nullValue()));

        assertCounts(fixture.counts(), table, 0, 0, 0, 1);
        assertThat(table.at(0, 1, 1).text(), containsString("NumberFormatException"));
    }

}
