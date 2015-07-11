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

import de.cologneintelligence.fitgoodies.date.SetupHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.TypeAdapter;

import java.text.ParseException;
import java.util.Date;

/**
 * TypeAdapter which is able to convert a string into a {@code java.util.Date}.
 * This class uses the {@link de.cologneintelligence.fitgoodies.date.SetupHelper} to allow the user
 * to set individual formats.
 * <p>
 *
 * If the cell is parameterized, the date format can be set individually.
 * The parameter must have the format &quot;{@code locale, format}&quot;.
 * Example: &quot;{@code en_US, MM/dd/yyyy}&quot;.
 *
 */
public class DateTypeAdapter extends AbstractTypeAdapter<Date> {
    private final SetupHelper dateSetupHelper;

    /**
     * Creates a new TypeAdapter which bases on {@code ta}.
     * @param ta TypeAdapter to use as source
     * @param convertParameter a parameter in the format [locale, format] which
     * 		represents the format to use
     */
    public DateTypeAdapter(final TypeAdapter ta, final String convertParameter) {
        super(ta, convertParameter);
        this.dateSetupHelper = DependencyManager.getOrCreate(SetupHelper.class);
    }

    /**
     * Parses a string and converts it into a {@code java.util.Date} object.
     * @param s {@code String} which will be converted
     * @return {@code java.util.Date} object which is represented by {@code s}.
     * @throws ParseException if the date could not be parsed
     */
    @Override
    public final Date parse(final String s) throws ParseException {
        if (getParameter() == null) {
            return dateSetupHelper.getDate(s);
        } else {
            final String[] parameters = getParameter().split("\\s*,\\s*", 2);
            if (parameters.length < 2) {
                throw new ParseException(
                        "Parameter must have the format [localname, format]", 0);
            }
            return dateSetupHelper.getDate(s, parameters[0], parameters[1]);
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
