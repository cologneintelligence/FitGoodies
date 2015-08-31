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


package de.cologneintelligence.fitgoodies.date;

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class SetupFixtureTest extends FitGoodiesFixtureTestCase<SetupFixture> {
	private SetupFixture fixture;
    private FitDateHelper helper;

    @Override
    protected Class<SetupFixture> getFixtureClass() {
        return SetupFixture.class;
    }

    @Before
	public void setUp() throws Exception {
		fixture = new SetupFixture();
        helper = DependencyManager.getOrCreate(FitDateHelper.class);
    }

	@Test
	public void testSetup1() {
        useTable(
            tr("locale", "de_DE"),
            tr("format", "hh:mm:ss"));

        preparePreprocessWithConversion(String.class, "de_DE", "de_DE");
        preparePreprocessWithConversion(String.class, "hh:mm:ss", "hh:mm:ss");

        run();

        assertCounts(0, 0, 0, 0);
        assertThat(helper.getFormat(), is(equalTo("hh:mm:ss")));
        assertThat(helper.getLocale(), is(equalTo(Locale.GERMANY)));
    }

    @Test
    public void testSetup2() {
		useTable(tr("locale", "$1"),
            tr("format", "$2"));


        preparePreprocessWithConversion(String.class, "$1", "en_US");
        preparePreprocessWithConversion(String.class, "$2", "MM/dd/yyyy");

		run();

        assertCounts(0, 0, 0, 0);
		assertThat(helper.getFormat(), is(equalTo("MM/dd/yyyy")));
		assertThat(helper.getLocale(), is(equalTo(Locale.US)));
	}
}
