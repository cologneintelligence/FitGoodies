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


package de.cologneintelligence.fitgoodies.date;

import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Helper class which holds information about date formats and
 * allows to parse date string.
 * <p>
 * This class should be used whenever a {@link TypeHandler} parses a date.
 */
public final class FitDateHelper {
	private Locale locale;
    private TimeZone timezone;
	private String format;

	public FitDateHelper() {
		locale = Locale.getDefault();
		format = "MM/dd/yyyy";
        timezone = TimeZone.getDefault();
	}

	/**
	 * Sets the locale to be used.
	 * <p>
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
		locale = parseLocale(localeName);
	}

	public Locale parseLocale(String localeName) {
		String[] parts = localeName.split("_");
		Locale result;
		if (parts.length == 1) {
			result = new Locale(parts[0]);
		} else if (parts.length == 2) {
			result = new Locale(parts[0], parts[1]);
		} else if (parts.length == 3) {
			result = new Locale(parts[0], parts[1], parts[2]);
		} else {
			throw new IllegalArgumentException("Locale " + localeName + " not valid");
		}
		return result;
	}

	/**
	 * Returns the selected locale.
	 *
	 * @return the selected locale.
	 * @see #setLocale(String) setLocale(String)
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the format which is used to parse dates to {@code formatString}.
	 * The string must be valid as a {@link SimpleDateFormat} format string.
	 *
	 * @param formatString the format string
	 * @see #getFormat() getFormat()
	 */
	public void setFormat(final String formatString) {
		format = formatString;
	}

	/**
	 * Gets the selected format.
	 *
	 * @return the used format string
	 * @see #setFormat(String) setFormat(String)
	 */
	public String getFormat() {
		return format;
	}

    public void setTimezone(String timezone) {
        this.timezone = TimeZone.getTimeZone(timezone);
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    /**
	 * Parses {@code string} and returns a valid Date object.
	 * For parsing, a {@link SimpleDateFormat} object is used.
	 *
	 * @param string the string which represents the date
	 * @return the date which is represented by {@code string}
	 * @throws ParseException thrown, if {@code string} can not be parsed.
	 */
	public Date getDate(final String string) throws ParseException {
		return getDateFormat(format, locale).parse(string);
	}

	private SimpleDateFormat getDateFormat(String dateFormat, Locale dateLocale) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, dateLocale);
        simpleDateFormat.setTimeZone(timezone);
        return simpleDateFormat;
	}

	/**
	 * Parses {@code string} and returns a valid Date object.
	 * For parsing, a {@link SimpleDateFormat} object is used.
	 *
	 * @param string       the string which represents the date
	 * @param formatString a valid SimpleDateFormat format string
	 * @param localeName   the name of the locale to use, for example en_US
	 * @return the date which is represented by {@code string}
	 * @throws ParseException thrown, if {@code string} can not be parsed.
	 */
	public Date getDate(final String string, final String formatString, final String localeName) throws ParseException {
		return getDateFormat(formatString, parseLocale(localeName)).parse(string);
	}

	public Date parse(String s, String parameter) throws ParseException {
		if (parameter == null) {
			return getDate(s);
		} else {
			final String[] parameters = parameter.split("\\s*,\\s*", 2);
			if (parameters.length < 2) {
				throw new ParseException("Parameter must have the format [localname, format]", 0);
			}
			return getDate(s, parameters[1], parameters[0]);
		}
	}

	public String toString(Date s) {
		return getDateFormat(format, locale).format(s);
	}
}
