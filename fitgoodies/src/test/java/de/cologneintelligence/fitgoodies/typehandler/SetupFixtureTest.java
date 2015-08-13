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


package de.cologneintelligence.fitgoodies.typehandler;

import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class SetupFixtureTest extends FitGoodiesTestCase {
    private Fixture fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new SetupFixture();
    }

    @Test
    public void testParse() throws Exception {
        TypeHandlerFactory helper = DependencyManager.getOrCreate(TypeHandlerFactory.class);
        Parse table = parseTable(tr("load", "de.cologneintelligence.fitgoodies.typehandler.LongParserMock"));

        assertThat(helper.getHandler(Long.class, null), is(not(instanceOf(LongParserMock.class))));

        fixture.doTable(table);
        assertThat(fixture.counts().exceptions, is(equalTo((Object) 0)));
        assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));

        assertThat(helper.getHandler(Long.class, null), is(instanceOf(LongParserMock.class)));
    }
}
