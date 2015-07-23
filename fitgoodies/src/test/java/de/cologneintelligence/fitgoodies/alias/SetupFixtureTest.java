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


package de.cologneintelligence.fitgoodies.alias;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.Parse;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SetupFixtureTest extends FitGoodiesTestCase {
    @Test
    public void testParsing() {
        Parse table = parseTable(tr("asdf", "java.lang.String"));

        SetupFixture fixture = new SetupFixture();
        fixture.doTable(table);

        assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
        AliasHelper helper = DependencyManager.getOrCreate(AliasHelper.class);
        assertThat(helper.getClazz("asdf"), is(equalTo("java.lang.String")));

        table = parseTable(tr("i", "java.lang.Integer"));

        fixture.doTable(table);
        assertThat(helper.getClazz("i"), is(equalTo("java.lang.Integer")));
    }

    @Test
    public void testError() {
        Parse table = parseTable(tr("x"));

        SetupFixture fixture = new SetupFixture();
        fixture.doTable(table);

        assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
        assertThat(fixture.counts().ignores, is(equalTo((Object) 1)));
    }
}
