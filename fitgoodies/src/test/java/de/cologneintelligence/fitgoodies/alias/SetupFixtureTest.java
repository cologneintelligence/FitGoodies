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


package de.cologneintelligence.fitgoodies.alias;

import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SetupFixtureTest extends FitGoodiesFixtureTestCase<SetupFixture> {

    private AliasHelper helper;

    @Before
    public void setUp() {
        helper = DependencyManager.getOrCreate(AliasHelper.class);
    }

    @Override
    protected Class<SetupFixture> getFixtureClass() {
        return SetupFixture.class;
    }

    @Test
	public void testParsing1() {
        useTable(tr("asdf", "java.lang.String"));

        preparePreprocess(cellAt(0, 0), "asdf2");
        preparePreprocess(cellAt(0, 1), "java.lang.Long");

        run();

        assertCounts(0, 0, 0, 0);
        assertThat(helper.getClazz("asdf2"), is(equalTo("java.lang.Long")));
    }

    @Test
    public void testParsing2() {
		useTable(tr("i", "java.lang.Integer"));

        preparePreprocess(cellAt(0, 0), "i");
        preparePreprocess(cellAt(0, 1), "java.lang.Integer");

		run();
		assertThat(helper.getClazz("i"), is(equalTo("java.lang.Integer")));
	}

	@Test
    public void testError() throws Exception {
		useTable(tr("x"));

		run();

        assertCounts(0, 0, 1, 0);
	}

}
