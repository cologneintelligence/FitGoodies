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


package de.cologneintelligence.fitgoodies.adapters;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Fixture;
import fit.Parse;
import fit.TypeAdapter;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class SetupFixtureTest extends FitGoodiesTestCase {
    private Fixture fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new SetupFixture();
    }

    @Test
    public void testParse() throws Exception {
        final Parse table = new Parse("<table>"
                + "<tr><td>ignore</td></tr>"
                + "<tr><td>load</td><td>de.cologneintelligence.fitgoodies.adapters.DummyTypeAdapter</td></tr>"
                + "</table>");

        fixture.doTable(table);
        assertThat(fixture.counts.exceptions, is(equalTo((Object) 0)));
        assertThat(fixture.counts.wrong, is(equalTo((Object) 0)));

        final TypeAdapter ta = new TypeAdapter();
        ta.type = BigInteger.class;

        TypeAdapterHelper helper = DependencyManager.getOrCreate(
                TypeAdapterHelper.class);
        assertThat(helper.getAdapter(ta, null).getClass(), (Matcher) is(equalTo(DummyTypeAdapter.class)));
    }
}