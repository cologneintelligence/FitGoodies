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


package de.cologneintelligence.fitgoodies.typehandler;

import de.cologneintelligence.fitgoodies.date.FitDateHelper;
import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SqlDateTypeHandlerTest extends FitGoodiesTestCase {

	private SqlDateTypeHandler handler;

	@Before
	public void setup() {
		handler = new SqlDateTypeHandler(null);
	}

	@Test
	public void testGetType() {
		assertThat(handler.getType(), is(equalTo(Date.class)));
	}

	@Test
	public void testParse() throws Exception {
		final Date d = Date.valueOf("1987-12-01");
		assertThat(handler.unsafeParse("1987-12-01"), is(equalTo(d)));
	}

	@Test
	public void testDateFormat() throws Exception {
		final FitDateHelper helper = DependencyManager.getOrCreate(FitDateHelper.class);
		helper.setLocale("de_DE");
		helper.setFormat("dd.MM.yyyy");

		Date d = Date.valueOf("1987-12-01");
		assertThat(handler.unsafeParse("1987-12-01"), is(equalTo(d)));

		d = Date.valueOf("1989-03-08");
		assertThat(handler.unsafeParse("08.03.1989"), is(equalTo(d)));
	}

	// TODO: test equals, parse with parameter, toString
}
