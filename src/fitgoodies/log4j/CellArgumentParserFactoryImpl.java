/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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

package fitgoodies.log4j;

import fit.Parse;

/**
 * Implementation of {@link CellArgumentParserFactory} which generates a
 * {@link CellArgumentParserImpl} for each cell.
 *
 * @author jwierum
 * @version $Id: CellArgumentParserFactoryImpl.java 197 2009-08-21 12:30:26Z jwierum $
 */
public final class CellArgumentParserFactoryImpl implements CellArgumentParserFactory {
	@Override
	public CellArgumentParser getParserFor(final Parse cell) {
		return new CellArgumentParserImpl(cell);
	}
}
