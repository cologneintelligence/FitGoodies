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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Helper class which holds information about date formats and
 * allows to parse date string. <br /><br />
 *
 * This class should be used whenever a
 * {@link de.cologneintelligence.fitgoodies.adapters.AbstractTypeAdapter} or a {@link de.cologneintelligence.fitgoodies.parsers.Parser}
 * parses a date.
 *
 * @author jwierum
 */
public final class SetupHelper {
    private Locale locale;
    private String format;

    public SetupHelper() {
        locale = Locale.getDefault();
        format = "MM/dd/yyyy";
    }

    /**
     * Sets the locale to be used.
     *
     * Accepts the formats:
     * <ul>
     * <li>en</li>
     * <li>en_US</li>
     * <li>en_US_WIN</li>
     * </ul>
     *
     * @param localeName name of the locale to use
     * @throws IllegalArgumentException if the localeName could not be parsed
     * @see #getLocale() getLocale
     */
    public void setLocale(final String localeName) {
        String[] parts = localeName.split("_");

        if (parts.length == 1) {
            locale = new Locale(parts[0]);
        } else if (parts.length == 2) {
            locale = new Locale(parts[0], parts[1]);
        } else if (parts.length == 3) {
            locale = new Locale(parts[0], parts[1], parts[2]);
        } else {
            throw new IllegalArgumentException("Locale " + localeName + " not valid");
        }
    }

    /**
     * Returns the selected locale.
     * @return the selected locale.
     * @see #setLocale(String) setLocale(String)
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the format which is used to parse dates to <code>formatString</code>.
     * The string must be valid as a {@link SimpleDateFormat} format string.
     * @param formatString the format string
     * @see #getFormat() getFormat()
     */
    public void setFormat(final String formatString) {
        format = formatString;
    }

    /**
     * Gets the selected format.
     * @return the used format string
     * @see #setFormat(String) setFormat(String)
     */
    public String getFormat() {
        return format;
    }

    /**
     * Parses <code>string</code> and returns a valid Date object.
     * For parsing, a {@link SimpleDateFormat} object is used.
     * @param string the string which represents the date
     * @return the date which is represented by <code>string</code>
     * @throws ParseException thrown, if <code>string</code> can not be parsed.
     */
    public Date getDate(final String string) throws ParseException {
        return new SimpleDateFormat(format, locale).parse(string);
    }

    /**
     * Parses <code>string</code> and returns a valid Date object.
     * For parsing, a {@link SimpleDateFormat} object is used.
     * @param string the string which represents the date
     * @param localeName the name of the locale to use, for example en_US
     * @param formatString a valid SimpleDateFormat format string
     * @return the date which is represented by <code>string</code>
     * @throws ParseException thrown, if <code>string</code> can not be parsed.
     */
    public Date getDate(final String string, final String localeName,
            final String formatString) throws ParseException {
        Locale oldLocale = locale;
        String oldFormat = format;

        try {
            setLocale(localeName);
            setFormat(formatString);

            return getDate(string);
        } finally {
            locale = oldLocale;
            format = oldFormat;
        }
    }
}
