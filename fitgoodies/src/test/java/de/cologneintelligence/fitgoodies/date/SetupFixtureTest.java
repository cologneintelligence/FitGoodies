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


package de.cologneintelligence.fitgoodies.date;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.Parse;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class SetupFixtureTest extends FitGoodiesTestCase {
    private SetupFixture fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new SetupFixture();
    }

    @Test
    public void testSetup() {
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);

        Parse table = parseTable(
                tr("locale", "de_DE"),
                tr("format", "hh:mm:ss"));

        fixture.doTable(table);

        assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
        assertThat(helper.getFormat(), is(equalTo("hh:mm:ss")));
        assertThat(helper.getLocale(), is(equalTo(Locale.GERMANY)));

        table = parseTable(tr("locale", "en_US"),
                tr("format", "MM/dd/yyyy"));

        fixture.doTable(table);

        assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
        assertThat(helper.getFormat(), is(equalTo("MM/dd/yyyy")));
        assertThat(helper.getLocale(), is(equalTo(Locale.US)));
    }
}
