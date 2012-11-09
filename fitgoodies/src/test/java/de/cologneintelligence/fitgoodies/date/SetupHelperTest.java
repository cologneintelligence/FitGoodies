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


package de.cologneintelligence.fitgoodies.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.date.SetupHelper;


/**
 * @author jwierum
 */
public class SetupHelperTest extends FitGoodiesTestCase {
    private SetupHelper helper;

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        helper = new SetupHelper();
    }

    public final void testSetLocale() {
        helper.setLocale("de_DE");
        assertEquals(Locale.GERMANY, helper.getLocale());
        helper.setLocale("de");
        assertEquals(Locale.GERMAN, helper.getLocale());
        helper.setLocale("en_US");
        assertEquals(Locale.US, helper.getLocale());

        try {
            helper.setLocale("this_is_an_error");
            fail("Missing exception: invalid locale");
        } catch (IllegalArgumentException e) {
        }
        assertEquals(Locale.US, helper.getLocale());

        helper.setLocale("ja_JP_JP");
        assertEquals(new Locale("ja", "JP", "JP"), helper.getLocale());
    }

    public final void testSetFormat() {
        helper.setFormat("dd.MM.yyyy");
        assertEquals("dd.MM.yyyy", helper.getFormat());

        helper.setFormat("hh:mm:ss");
        assertEquals("hh:mm:ss", helper.getFormat());
    }

    public final void testGetDate() throws ParseException {
        assertEquals(
                DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"),
                helper.getDate("01/18/1987"));

        helper.setLocale("de_DE");
        helper.setFormat("dd.MM.yyyy");
        assertEquals(
                DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("08.03.1989"),
                helper.getDate("08.03.1989"));
    }

    public final void testGetDateWithShortYear() throws ParseException {
        helper.setLocale("de_DE");
        helper.setFormat("dd.MM.yy");
        assertEquals(
                DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"),
                helper.getDate("18.01.87"));
    }

    public final void testGetDateWithFormat() throws ParseException {
        helper.setLocale("de_DE");
        helper.setFormat("dd.MM.yy");

        assertEquals(
                DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"),
                helper.getDate("18.01.1987", "de_DE", "dd.MM.yyyy"));

        assertEquals(
                DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"),
                helper.getDate("01/18/1987", "en_US", "MM/dd/yyyy"));

        assertEquals("de_DE", helper.getLocale().toString());
        assertEquals("dd.MM.yy", helper.getFormat());
    }
}
