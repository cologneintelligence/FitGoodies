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

import java.text.ParseException;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.SystemPropertyProvider;
import fit.Parse;

public class SetupFixtureTest extends FitGoodiesTestCase {
    SystemPropertyProvider propertyProvider;

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
    
    public final void testParsing() throws ParseException {
        Parse table = new Parse("<table><tr><td>ignore</td></tr>"
                + "<tr><td>addProperty</td><td>-DtestKey=${System.getProperty(testSetupFixtureKey)}</td></tr>"
                + "</table>");

        SetupFixture fixture = new SetupFixture();
        fixture.doTable(table);

        assertEquals(0, fixture.counts.exceptions);
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        assertEquals("-DtestKey=testValue", helper.getProperties().get(0));
    }

}
