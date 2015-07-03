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


package de.cologneintelligence.fitgoodies.external;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.SystemPropertyProvider;
import fit.Parse;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SetupFixtureTest extends FitGoodiesTestCase {
    private SystemPropertyProvider propertyProvider;

    @Before
    public void setUp() {
        DependencyManager.clear();
        propertyProvider = new SystemPropertyProvider() {
            @Override
            public String getProperty(String key) {
                return "testValue";
            }
        };
        DependencyManager.inject(SystemPropertyProvider.class, propertyProvider);
    }

    @Test
    public void testParsing() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>addProperty</td><td>-DtestKey=${System.getProperty(testSetupFixtureKey)}</td></tr>"
                + "</table>");

        SetupFixture fixture = new SetupFixture();
        fixture.doTable(table);

        assertThat(fixture.counts.exceptions, is(equalTo((Object) 0)));
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        assertThat(helper.getProperties().get(0), is(equalTo("-DtestKey=testValue")));
    }

}
