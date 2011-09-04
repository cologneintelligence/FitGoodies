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

import java.text.ParseException;
import java.util.Date;

import de.cologneintelligence.fitgoodies.date.SetupHelper;

import fit.TypeAdapter;

/**
 * TypeAdapter which is able to convert a string into a <code>java.util.Date</code>.
 * This class uses the {@link de.cologneintelligence.fitgoodies.date.SetupHelper} to allow the user
 * to set individual formats.<br /><br />
 *
 * If the cell is parameterized, the date format can be set individually.
 * The parameter must have the format &quot;<code>locale, format</code>&quot;.
 * Example: &quot;<code>en_US, MM/dd/yyyy</code>&quot;.
 *
 * @author jwierum
 * @version $Id$
 */
public class DateTypeAdapter extends AbstractTypeAdapter<Date> {
	/**
	 * Creates a new TypeAdapter which bases on <code>ta</code>.
	 * @param ta TypeAdapter to use as source
	 * @param convertParameter a parameter in the format [locale, format] which
	 * 		represents the format to use
	 */
	public DateTypeAdapter(final TypeAdapter ta, final String convertParameter) {
		super(ta, convertParameter);
	}

	/**
	 * Parses a string and converts it into a <code>java.util.Date</code> object.
	 * @param s <code>String</code> which will be converted
	 * @return <code>java.util.Date</code> object which is represented by <code>s</code>.
	 * @throws ParseException if the date could not be parsed
	 */
	@Override
	public final Date parse(final String s) throws ParseException {
		if (getParameter() == null) {
			return SetupHelper.instance().getDate(s);
		} else {
			String[] parameters = getParameter().split("\\s*,\\s*", 2);
			if (parameters.length < 2) {
				throw new ParseException(
					"Parameter must have the format [localname, format]", 0);
			}
			return SetupHelper.instance().getDate(s, parameters[0], parameters[1]);
		}
	}

	/**
	 * Returns the destination class which is managed by this parser.
	 * @return java.util.Date.class
	 */
	@Override
	public final Class<Date> getType() {
		return Date.class;
	}
}
