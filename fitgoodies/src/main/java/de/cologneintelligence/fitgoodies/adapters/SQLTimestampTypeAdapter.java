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


package de.cologneintelligence.fitgoodies.adapters;

import java.sql.Timestamp;
import java.text.ParseException;

import fit.TypeAdapter;

/**
 * Parser which is able to convert a string into a <code>java.sql.Timestamp</code>.
 * It uses the {@link Timestamp#valueOf(String)} method first. If it fails, the
 * class uses the {@link de.cologneintelligence.fitgoodies.date.SetupHelper} to allow the user
 * to set individual formats.
 *
 * If the cell is parameterized, the date format can be set individually.
 * The parameter must have the format &quot;<code>locale, format</code>&quot;.
 * Example: &quot;<code>en_US, MM/dd/yyyy</code>&quot;.
 *
 */
public class SQLTimestampTypeAdapter extends AbstractTypeAdapter<Timestamp> {
    /**
     * Creates a new TypeAdapter which bases on <code>ta</code>.
     * @param ta ta TypeAdapter to use as source
     * @param convertParameter a parameter in the format [locale, format] which
     *      represents the format to use
     */
    public SQLTimestampTypeAdapter(final TypeAdapter ta, final String convertParameter) {
        super(ta, convertParameter);
    }

    /**
     * Returns the destination class which is managed by this parser.
     * @return java.sql.Timestamp.class
     */
    @Override
    public Class<Timestamp> getType() {
        return Timestamp.class;
    }

    /**
     * Parses a string and converts it into a <code>java.sql.Timestamp</code> object.
     * @param s <code>String</code> which will be converted
     * @return <code>java.sql.Timestamp</code> object which is represented by <code>s</code>
     * @throws ParseException if the date could not be parsed
     */
    @Override
    public final Timestamp parse(final String s) throws ParseException {
        try {
            return Timestamp.valueOf(s);
        } catch (final IllegalArgumentException e) {
            return new Timestamp(new DateTypeAdapter(this, getParameter()).parse(s).getTime());
        }
    }
}
