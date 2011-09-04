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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.adapters.AbstractTypeAdapter;
import de.cologneintelligence.fitgoodies.adapters.DateTypeAdapter;

import fit.TypeAdapter;

/**
 * $Id$
 * @author jwierum
 */
public class DateTypeAdapterTest extends FitGoodiesTestCase {
	public final void testParser() throws Exception {
		TypeAdapter ta = new TypeAdapter();

		AbstractTypeAdapter<Date> p = new DateTypeAdapter(ta, null);
		assertEquals(
				DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(
						"01/18/1987"), p.parse("01/18/1987"));

		p = new DateTypeAdapter(ta, "de_DE, dd.MM.yyyy");
		assertEquals(
				DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(
						"01/18/1987"), p.parse("18.01.1987")
				);

		p = new DateTypeAdapter(ta, "de_DE, dd.MM.yyyy");
		assertEquals(
				DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(
						"03/08/1989"), p.parse("08.03.1989")
				);

		p = new DateTypeAdapter(ta, null);
		assertEquals(
				DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(
						"03/08/1989"), p.parse("03/08/1989"));
	}

	public final void testException() {
		TypeAdapter ta = new TypeAdapter();
		try {
			new DateTypeAdapter(ta, "invalid").parse("01/01/1970");
			fail("could set invalid parameter");
		} catch (ParseException e) {
		}
	}

	public final void testType() {
		TypeAdapter ta = new TypeAdapter();

		assertNotNull(new DateTypeAdapter(ta, null).getType());
	}
}
