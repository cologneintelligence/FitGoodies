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
import fit.Parse;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class FixedLengthFileRecordFixtureTest extends FitGoodiesTestCase {
    @Test
    public void testExtractWidth() throws ParseException {
        FixedLengthFileRecordFixture fixture = new FixedLengthFileRecordFixture();

        Parse row = new Parse("<tr><td>1</td><td>7</td><td>4</td></tr>",
                new String[]{"tr", "td"});

        int[] actual = fixture.extractWidth(row);

        assertThat(actual.length, is(equalTo((Object) 3)));
        assertThat(actual[0], is(equalTo((Object) 1)));
        assertThat(actual[1], is(equalTo((Object) 7)));
        assertThat(actual[2], is(equalTo((Object) 4)));

        row = new Parse("<tr><td>3</td><td>1</td><td>9</td><td>0</td></tr>",
                new String[]{"tr", "td"});

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

        Parse row = new Parse("<tr><td>1</td><td>${width.get(col2)}</td></tr>",
                new String[]{"tr", "td"});

        CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
        helper.parseBody("${width.put(col2)}", 23);
        int[] actual = fixture.extractWidth(row);

        assertThat(actual.length, is(equalTo((Object) 2)));
        assertThat(actual[0], is(equalTo((Object) 1)));
        assertThat(actual[1], is(equalTo((Object) 23)));
    }

    @Test
    public void testErrors() throws ParseException {
        FixedLengthFileRecordFixture fixture = new FixedLengthFileRecordFixture();

        Parse row = new Parse("<tr><td>1</td><td>error</td><td>4</td></tr>",
                new String[]{"tr", "td"});

        int[] actual = fixture.extractWidth(row);

        assertThat(actual, is(nullValue()));
        assertThat(fixture.counts.exceptions, is(equalTo((Object) 1)));
        assertThat(row.parts.more.text(), containsString("NumberFormatException"));
    }
}
