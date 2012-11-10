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


package de.cologneintelligence.fitgoodies.adapters;

import java.sql.Date;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.date.SetupHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.TypeAdapter;

public class SQLDateTypeAdapterTest extends FitGoodiesTestCase {
    public final void testGetType() {
        final TypeAdapter ta = new TypeAdapter();

        final SQLDateTypeAdapter p = new SQLDateTypeAdapter(ta, null);
        assertEquals(Date.class, p.getType());
    }

    public final void testParse() throws Exception {
        final TypeAdapter ta = new TypeAdapter();

        final Date d = Date.valueOf("1987-12-01");
        final SQLDateTypeAdapter p = new SQLDateTypeAdapter(ta, null);
        assertEquals(d, p.parse("1987-12-01"));
    }

    public final void testDateFormat() throws Exception {
        final TypeAdapter ta = new TypeAdapter();

        final SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        helper.setLocale("de_DE");
        helper.setFormat("dd.MM.yyyy");

        final SQLDateTypeAdapter p = new SQLDateTypeAdapter(ta, null);

        Date d = Date.valueOf("1987-12-01");
        assertEquals(d, p.parse("1987-12-01"));

        d = Date.valueOf("1989-03-08");
        assertEquals(d, p.parse("08.03.1989"));
    }
}
