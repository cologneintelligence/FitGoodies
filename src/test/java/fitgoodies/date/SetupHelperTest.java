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


package fitgoodies.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

import fitgoodies.FitGoodiesTestCase;

/**
 * $Id$
 * @author jwierum
 */
public class SetupHelperTest extends FitGoodiesTestCase {
	public final void testSingleton() {
		SetupHelper expected = SetupHelper.instance();
		assertNotNull(expected);
		assertSame(expected, SetupHelper.instance());

		SetupHelper.reset();
		assertNotSame(expected, SetupHelper.instance());
	}

	public final void testSetLocale() {
		SetupHelper.instance().setLocale("de_DE");
		assertEquals(Locale.GERMANY, SetupHelper.instance().getLocale());
		SetupHelper.instance().setLocale("de");
		assertEquals(Locale.GERMAN, SetupHelper.instance().getLocale());
		SetupHelper.instance().setLocale("en_US");
		assertEquals(Locale.US, SetupHelper.instance().getLocale());

		try {
			SetupHelper.instance().setLocale("this_is_an_error");
			fail("Missing exception: invalid locale");
		} catch (IllegalArgumentException e) {
		}
		assertEquals(Locale.US, SetupHelper.instance().getLocale());

		SetupHelper.instance().setLocale("ja_JP_JP");
		assertEquals(new Locale("ja", "JP", "JP"), SetupHelper.instance().getLocale());
	}

	public final void testSetFormat() {
		SetupHelper.instance().setFormat("dd.MM.yyyy");
		assertEquals("dd.MM.yyyy", SetupHelper.instance().getFormat());

		SetupHelper.instance().setFormat("hh:mm:ss");
		assertEquals("hh:mm:ss", SetupHelper.instance().getFormat());
	}

	public final void testGetDate() throws ParseException {
		assertEquals(
				DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"),
				SetupHelper.instance().getDate("01/18/1987"));

		SetupHelper.instance().setLocale("de_DE");
		SetupHelper.instance().setFormat("dd.MM.yyyy");
		assertEquals(
				DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("08.03.1989"),
				SetupHelper.instance().getDate("08.03.1989"));
	}

	public final void testGetDateWithShortYear() throws ParseException {
		SetupHelper.instance().setLocale("de_DE");
		SetupHelper.instance().setFormat("dd.MM.yy");
		assertEquals(
				DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"),
				SetupHelper.instance().getDate("18.01.87"));
	}

	public final void testGetDateWithFormat() throws ParseException {
		SetupHelper.instance().setLocale("de_DE");
		SetupHelper.instance().setFormat("dd.MM.yy");

		assertEquals(
				DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"),
				SetupHelper.instance().getDate("18.01.1987", "de_DE", "dd.MM.yyyy"));

		assertEquals(
				DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"),
				SetupHelper.instance().getDate("01/18/1987", "en_US", "MM/dd/yyyy"));

		assertEquals("de_DE", SetupHelper.instance().getLocale().toString());
		assertEquals("dd.MM.yy", SetupHelper.instance().getFormat());
	}
}
