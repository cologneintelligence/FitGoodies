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
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SqlTimestampTypeHandlerTest extends FitGoodiesTestCase {

	private SqlTimestampTypeHandler handler;

	@Before
	public void setUp() {
		handler = new SqlTimestampTypeHandler(null);
	}

	@Test
	public void testGetType() {
		assertThat(handler.getType(), is(equalTo(Timestamp.class)));
	}

	@Test
	public void testParse() throws Exception {
		final Timestamp t = Timestamp.valueOf("1987-12-01 00:00:00");
		assertThat(handler.unsafeParse("1987-12-01 00:00:00"), is(equalTo(t)));
	}

	@Test
	public void testDateFormat() throws Exception {
		final FitDateHelper helper = DependencyManager.getOrCreate(FitDateHelper.class);
		helper.setLocale("de_DE");
		helper.setFormat("dd.MM.yyyy");

		Timestamp d = Timestamp.valueOf("1987-12-01 00:11:22");
		assertThat(handler.unsafeParse("1987-12-01 00:11:22"), is(equalTo(d)));

		d = Timestamp.valueOf("1989-03-08 00:00:00");
		assertThat(handler.unsafeParse("08.03.1989"), is(equalTo(d)));
	}

	// TODO: test equals, parse with parameter, toString
}
