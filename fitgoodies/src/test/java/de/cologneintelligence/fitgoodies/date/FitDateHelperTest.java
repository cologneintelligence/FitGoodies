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


package de.cologneintelligence.fitgoodies.date;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class FitDateHelperTest extends FitGoodiesTestCase {
    private FitDateHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new FitDateHelper();
    }

    @Test
    public void testSetLocale() {
        helper.setLocale("de_DE");
        assertThat(helper.getLocale(), is(equalTo(Locale.GERMANY)));
        helper.setLocale("de");
        assertThat(helper.getLocale(), is(equalTo(Locale.GERMAN)));
        helper.setLocale("en_US");
        assertThat(helper.getLocale(), is(equalTo(Locale.US)));

        try {
            helper.setLocale("this_is_an_error");
            Assert.fail("Missing exception: invalid locale");
        } catch (IllegalArgumentException ignored) {
        }
        assertThat(helper.getLocale(), is(equalTo(Locale.US)));

        helper.setLocale("ja_JP_JP");
        assertThat(helper.getLocale(), is(equalTo(new Locale("ja", "JP", "JP"))));
    }

    @Test
    public void testSetFormat() {
        helper.setFormat("dd.MM.yyyy");
        assertThat(helper.getFormat(), is(equalTo("dd.MM.yyyy")));

        helper.setFormat("hh:mm:ss");
        assertThat(helper.getFormat(), is(equalTo("hh:mm:ss")));
    }

    @Test
    public void testGetDate() throws ParseException {
        assertThat(helper.getDate("01/18/1987"), is(equalTo(DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"))));

        helper.setLocale("de_DE");
        helper.setFormat("dd.MM.yyyy");
        assertThat(helper.getDate("08.03.1989"), is(equalTo(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("08.03.1989"))));
    }

    @Test
    public void testGetDateWithShortYear() throws ParseException {
        helper.setLocale("de_DE");
        helper.setFormat("dd.MM.yy");
        assertThat(helper.getDate("18.01.87"), is(equalTo(DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"))));
    }

    @Test
    public void testGetDateWithFormat() throws ParseException {
        helper.setLocale("de_DE");
        helper.setFormat("dd.MM.yy");

        assertThat(helper.getDate("18.01.1987", "dd.MM.yyyy", "de_DE"), is(equalTo(DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"))));

        assertThat(helper.getDate("01/18/1987", "MM/dd/yyyy", "en_US"), is(equalTo(DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/18/1987"))));

        assertThat(helper.getLocale().toString(), is(equalTo("de_DE")));
        assertThat(helper.getFormat(), is(equalTo("dd.MM.yy")));
    }
}
