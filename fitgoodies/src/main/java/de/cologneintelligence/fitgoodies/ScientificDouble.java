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


package de.cologneintelligence.fitgoodies;


/**
 * Dropin replacement for <code>fit.ScientificDouble</code>
 * which works with RowFixtures.<br /><br />
 *
 * The original ScientificDouble does not override <code>hashCode</code> but equals.
 * There is a problem, because equal objects must return the an equal code.
 * However, the precision makes it impossible to generate a good hash.
 * So <code>1</code> is returned, which does not boost the performance, but
 * at least, it works.
 *
 * @author jwierum
 * @version $Id$
 */
public class ScientificDouble extends fit.ScientificDouble {
	private static final long serialVersionUID = 516631298066483532L;

	/**
	 * Generates a new ScientificDouble object.
	 * @param value represented value
	 */
	public ScientificDouble(final double value) {
		super(value);
	}

	/**
	 * Constructs a ScientificDouble object.
	 *
	 * @param value to set
	 * @param tolerance allowed tolerance
	 */
	public ScientificDouble(final double value, final double tolerance) {
		super(value);
		this.precision = tolerance;
	}

	@Override
	public int hashCode() {
		return 1;
	}
}
