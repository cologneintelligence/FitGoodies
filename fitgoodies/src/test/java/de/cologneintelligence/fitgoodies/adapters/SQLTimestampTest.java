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

/*
 * Copyright (c) 2011  Cologne Intelligence GmbH
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

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.date.SetupHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.TypeAdapter;
import org.junit.Test;

import java.sql.Timestamp;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SQLTimestampTest extends FitGoodiesTestCase {
    @Test
    public void testGetType() {
        final TypeAdapter ta = new TypeAdapter();

        final SQLTimestampTypeAdapter p = new SQLTimestampTypeAdapter(ta, null);
        assertThat(p.getType(), is(equalTo(Timestamp.class)));
    }

    @Test
    public void testParse() throws Exception {
        final TypeAdapter ta = new TypeAdapter();

        final Timestamp t = Timestamp.valueOf("1987-12-01 00:00:00");
        final SQLTimestampTypeAdapter d = new SQLTimestampTypeAdapter(ta, null);
        assertThat(d.parse("1987-12-01 00:00:00"), is(equalTo(t)));
    }

    @Test
    public void testDateFormat() throws Exception {
        final TypeAdapter ta = new TypeAdapter();

        final SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        helper.setLocale("de_DE");
        helper.setFormat("dd.MM.yyyy");

        final SQLTimestampTypeAdapter p = new SQLTimestampTypeAdapter(ta, null);

        Timestamp d = Timestamp.valueOf("1987-12-01 00:11:22");
        assertThat(p.parse("1987-12-01 00:11:22"), is(equalTo(d)));

        d = Timestamp.valueOf("1989-03-08 00:00:00");
        assertThat(p.parse("08.03.1989"), is(equalTo(d)));
    }
}
