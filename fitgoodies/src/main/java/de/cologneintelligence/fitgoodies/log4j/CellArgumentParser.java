/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
 * This file is part of FitGoodies.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.cologneintelligence.fitgoodies.log4j;

import java.util.Map;

/**
 * Parser which extracts a <code>Map</code> from a cell.
 * The parameter format must be &quot;[key1=value1, key2=value2, ...]&quot;
 *
 */
public interface CellArgumentParser {
	/**
	 * Gets the extracted parameters.
	 * @return key/value pair of parameters
	 */
	Map<String, String> getExtractedCommandParameters();
}
