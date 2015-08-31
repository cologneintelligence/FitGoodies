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

package de.cologneintelligence.fitgoodies.database;

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.types.ScientificDouble;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


public class TableFixtureTest extends FitGoodiesFixtureTestCase<TableFixture> {
    @Mock
    private SetupHelper setupHelper;

    @Mock
    private Connection connection;

    @Override
    protected Class<TableFixture> getFixtureClass() {
        return TableFixture.class;
    }

	@Before
	public void setUp() throws Exception {
        DependencyManager.inject(SetupHelper.class, setupHelper);
    }

    @After
    public void cleanupDBDriverMock() throws Exception {
        de.cologneintelligence.fitgoodies.database.DriverMock.cleanup();
    }

    @After
    public void tearDown() {
        DriverMock.cleanup();
    }

	@Test
	public void testDoTable2() throws Exception {
		useTable(tr("age"), tr("42.3"));

        Map<String, String> params = new HashMap<>();
        params.put("table", "$table");
        fixture.setParams(params);

		ResultSetMockGenerator mocker = new ResultSetMockGenerator(
				"table1",
				new String[]{"age"},
				new Object[][]{
						new Object[]{42.3}
				});

        prepareParameterApply("table", "$table", "table1");
        fixture.table = "table1";

        when(setupHelper.getConnection()).thenReturn(mocker.getConnection());

		run();
        assertThat(fixture.getTable(), is(equalTo("table1")));

		Class<?> c = fixture.getTargetClass();
		assertThat(c.getField("age").getType(), (Matcher) is(sameInstance(ScientificDouble.class)));
		mocker.verifyInteractions();
	}

	@Test
	public void testWhereClause() throws Exception {
		useTable(tr("ignore"), tr("x"));

		ResultSetMockGenerator mocker = new ResultSetMockGenerator(
				"tbl4", "x > 7",
				new String[]{},
				new Object[][]{});

        Map<String, String> params = new HashMap<>();
        params.put("table", "$tbl4");
        params.put("where", "x > 7");
        fixture.setParams(params);

        expectParameterApply("table", "$tbl4", "tbl4");
        expectParameterApply("where", "x > 7", "x > 7");

        fixture.table = "tbl4";
        fixture.where = "x > 7";

        when(setupHelper.getConnection()).thenReturn(mocker.getConnection());

		run();

		mocker.verifyInteractions();
	}

	@Test
	public void testError() throws Exception {
		useTable(tr("ignore"), tr("x"));
		run();
        assertCounts(0, 0, 0, 1);
	}

}
