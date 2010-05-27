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


package fitgoodies.adapters;

import java.sql.Date;

import fit.TypeAdapter;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.date.SetupHelper;

public class SQLDateTypeAdapterTest extends FitGoodiesTestCase {

	public final void testGetType() {
		TypeAdapter ta = new TypeAdapter();

		SQLDateTypeAdapter p = new SQLDateTypeAdapter(ta, null);
		assertEquals(Date.class, p.getType());
	}

	public final void testParse() throws Exception {
		TypeAdapter ta = new TypeAdapter();

		Date d = Date.valueOf("1987-12-01");
		SQLDateTypeAdapter p = new SQLDateTypeAdapter(ta, null);
		assertEquals(d, p.parse("1987-12-01"));
	}

	public final void testDateFormat() throws Exception {
		TypeAdapter ta = new TypeAdapter();

		SetupHelper.instance().setLocale("de_DE");
		SetupHelper.instance().setFormat("dd.MM.yyyy");

		SQLDateTypeAdapter p = new SQLDateTypeAdapter(ta, null);

		Date d = Date.valueOf("1987-12-01");
		assertEquals(d, p.parse("1987-12-01"));

		d = Date.valueOf("1989-03-08");
		assertEquals(d, p.parse("08.03.1989"));
	}
}
