/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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

import java.text.ParseException;
import java.util.Locale;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.date.SetupFixture;
import de.cologneintelligence.fitgoodies.date.SetupHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import fit.Parse;

/**
 * @author jwierum
 */
public class SetupFixtureTest extends FitGoodiesTestCase {
    private SetupFixture fixture;

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        fixture = new SetupFixture();
    }

    public final void testSetup() throws ParseException {
        SetupHelper helper = DependencyManager.INSTANCE.getOrCreate(SetupHelper.class);

        Parse table = new Parse("<table>"
                + "<tr><td>ignore</td></tr>"
                + "<tr><td>locale</td><td>de_DE</td></tr>"
                + "<tr><td>format</td><td>hh:mm:ss</td></tr>"
                + "</table>");

        fixture.doTable(table);

        assertEquals(0, fixture.counts.exceptions);
        assertEquals("hh:mm:ss", helper.getFormat());
        assertEquals(Locale.GERMANY, helper.getLocale());

        table = new Parse("<table>"
                + "<tr><td>ignore</td></tr>"
                + "<tr><td>locale</td><td>en_US</td></tr>"
                + "<tr><td>format</td><td>MM/dd/yyyy</td></tr>"
                + "</table>");

        fixture.doTable(table);

        assertEquals(0, fixture.counts.exceptions);
        assertEquals("MM/dd/yyyy", helper.getFormat());
        assertEquals(Locale.US, helper.getLocale());
    }
}
