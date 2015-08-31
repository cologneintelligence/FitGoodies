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


package de.cologneintelligence.fitgoodies.alias;

import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.htmlparser.FitRow;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

/**
 * Fixture to register aliases at runtime.
 */
public class SetupFixture extends Fixture {
	/**
	 * alias name to use.
	 */
	public String alias;

	/**
	 * class name to use.
	 */
	public String className;

	/**
	 * Reads a row which contains two cells and registers an alias.
	 * <p>
	 * The first cell must contain the alias, the second one must contains the
	 * fully qualified class name. Using another alias as class name is not
	 * permitted.
	 * <p>
	 * Cross references are resolved.
	 *
	 * @param row row to parse
	 */
	@Override
	protected void doRow(FitRow row) {
        if (row.size() < 2) {
            row.cells().get(0).ignore();
            return;
        }

		alias = validator.preProcess(row.cells().get(0));
		className = validator.preProcess(row.cells().get(1));

		AliasHelper aliasHelper = DependencyManager.getOrCreate(AliasHelper.class);
		aliasHelper.register(alias, className);
	}
}
