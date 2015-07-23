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

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.Parse;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class FixedLengthFileRecordFixtureTest extends FitGoodiesTestCase {
    @Test
    public void testExtractWidth() {
        FixedLengthFileRecordFixture fixture = new FixedLengthFileRecordFixture();

        Parse row = parseTr("1", "7", "4");

        int[] actual = fixture.extractWidth(row);

        assertThat(actual.length, is(equalTo((Object) 3)));
        assertThat(actual[0], is(equalTo((Object) 1)));
        assertThat(actual[1], is(equalTo((Object) 7)));
        assertThat(actual[2], is(equalTo((Object) 4)));

        row = parseTr("3", "1", "9", "0");

        actual = fixture.extractWidth(row);

        assertThat(actual.length, is(equalTo((Object) 4)));
        assertThat(actual[0], is(equalTo((Object) 3)));
        assertThat(actual[1], is(equalTo((Object) 1)));
        assertThat(actual[2], is(equalTo((Object) 9)));
        assertThat(actual[3], is(equalTo((Object) 0)));
    }

    @Test
    public void testExtractWidthWithCrossRefs() throws Exception {
        FixedLengthFileRecordFixture fixture = new FixedLengthFileRecordFixture();

        Parse row = parseTr("1", "${width.get(col2)}");

        CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
        helper.parseBody("${width.put(col2)}", 23);
        int[] actual = fixture.extractWidth(row);

        assertThat(actual.length, is(equalTo((Object) 2)));
        assertThat(actual[0], is(equalTo((Object) 1)));
        assertThat(actual[1], is(equalTo((Object) 23)));
    }

    @Test
    public void testErrors() {
        FixedLengthFileRecordFixture fixture = new FixedLengthFileRecordFixture();

        Parse row = parseTr("1", "error", "4");

        int[] actual = fixture.extractWidth(row);

        assertThat(actual, is(nullValue()));
        assertThat(fixture.counts().exceptions, is(equalTo((Object) 1)));
        assertThat(row.parts.more.text(), containsString("NumberFormatException"));
    }
}
