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


package de.cologneintelligence.fitgoodies.parsers;

import de.cologneintelligence.fitgoodies.ScientificDouble;

/**
 * Parser which is able to convert a string into a <code>ScientificDoubleParser</code>.
 *
 * @author jwierum
 * @version $Id$
 */
public class ScientificDoubleParser implements Parser<ScientificDouble> {
	/**
	 * Returns the destination class which is managed by this parser.
	 * @return ScientificDouble.class
	 */
	@Override
	public Class<ScientificDouble> getType() {
		return ScientificDouble.class;
	}

	/**
	 * Parses a string and converts it into an <code>ScientificDouble</code>.
	 * @param s <code>String</code> which will be converted
	 * @param parameter ignored
	 * @return <code>ScientificDouble</code> object which is represented by <code>s</code>
	 *
	 * @throws Exception if the value can not be parsed
	 */
	@Override
	public ScientificDouble parse(final String s, final String parameter)
			throws Exception {
		double value = Double.parseDouble(s);
        double tolerance = precision(s);
        return new ScientificDouble(value, tolerance);
	}

    private static double precision(final String s) {
        double value = Double.parseDouble(s);
        double bound = Double.parseDouble(tweak(s.trim()));
        return Math.abs(bound - value);
    }

    private static String tweak(final String s) {
        int pos = s.toLowerCase().indexOf("e");
        if (pos >= 0) {
            return tweak(s.substring(0, pos)) + s.substring(pos);
        }
        if (s.indexOf(".") >= 0) {
            return s + "5";
        }
        return s + ".5";
    }
}
