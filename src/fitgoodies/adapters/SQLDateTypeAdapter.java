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
import java.text.ParseException;

import fit.TypeAdapter;

/**
 * Parser which is able to convert a string into a <code>java.sql.Date</code>.
 * It uses the {@link Date#valueOf(String)} method first. If it fails, the
 * class uses the {@link fitgoodies.date.SetupHelper} to allow the user
 * to set individual formats.
 *
 * If the cell is parameterized, the date format can be set individually.
 * The parameter must have the format &quot;<code>locale, format</code>&quot;.
 * Example: &quot;<code>en_US, MM/dd/yyyy</code>&quot;.
 *
 * @author jwierum
 * @version $Id: SQLDateTypeAdapter.java 185 2009-08-17 13:47:24Z jwierum $
 */
public class SQLDateTypeAdapter extends AbstractTypeAdapter<Date> {

	/**
	 * Creates a new TypeAdapter which bases on <code>ta</code>.
	 * @param ta ta TypeAdapter to use as source
	 * @param convertParameter a parameter in the format [locale, format] which
	 * 		represents the format to use
	 */
	public SQLDateTypeAdapter(final TypeAdapter ta, final String convertParameter) {
		super(ta, convertParameter);
	}

	/**
	 * Returns the destination class which is managed by this parser.
	 * @return java.sql.Date.class
	 */
	@Override
	public final Class<Date> getType() {
		return Date.class;
	}

	/**
	 * Parses a string and converts it into a <code>java.sql.Date</code> object.
	 * @param s <code>String</code> which will be converted
	 * @return <code>java.sql.Date</code> object which is represented by <code>s</code>
	 * @throws ParseException if the date could not be parsed
	 */
	@Override
	public final Date parse(final String s) throws ParseException {
		try {
			return Date.valueOf(s);
		} catch (IllegalArgumentException e) {
			return new Date(new DateTypeAdapter(this, getParameter()).parse(s).getTime());
		}
	}
}
