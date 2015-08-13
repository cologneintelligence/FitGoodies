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


package de.cologneintelligence.fitgoodies.typehandler;

import de.cologneintelligence.fitgoodies.date.FitDateHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

/**
 * Parser which is able to convert a string into a {@code java.sql.Timestamp}.
 * It uses the {@link Timestamp#valueOf(String)} method first. If it fails, the
 * class uses the {@link FitDateHelper} to allow the user
 * to set individual formats.
 *
 * If the cell is parameterized, the date format can be set individually.
 * The parameter must have the format &quot;{@code locale, format}&quot;.
 * Example: &quot;{@code en_US, MM/dd/yyyy}&quot;.
 *
 */
public class SqlTimestampTypeHandler extends TypeHandler<Timestamp> {
    private final FitDateHelper dateFitDateHelper;

    /**
     * Creates a new TypeAdapter which bases on {@code ta}.
     * @param convertParameter a parameter in the format [locale, format] which
     *      represents the format to use
     */
    public SqlTimestampTypeHandler(final String convertParameter) {
        super(convertParameter);
        this.dateFitDateHelper = DependencyManager.getOrCreate(FitDateHelper.class);
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
     * Parses a string and converts it into a {@code java.sql.Timestamp} object.
     * @param s {@code String} which will be converted
     * @return {@code java.sql.Timestamp} object which is represented by {@code s}
     * @throws ParseException if the date could not be parsed
     */
    @Override
    public final Timestamp unsafeParse(final String s) throws ParseException {
        try {
            return Timestamp.valueOf(s);
        } catch (final IllegalArgumentException e) {
            return new Timestamp(dateFitDateHelper.parse(s, parameter).getTime());
        }
    }

    @Override
    public String toString(Timestamp s) {
        return dateFitDateHelper.toString(new Date(s.getTime()));
    }
}
