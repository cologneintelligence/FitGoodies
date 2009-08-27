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


package fitgoodies.alias;

import fit.Parse;
import fit.TypeAdapter;
import fitgoodies.Fixture;
import fitgoodies.util.FixtureTools;

/**
 * Fixture to register aliases at runtime.
 *
 * @author jwierum
 * @version $Id: SetupFixture.java 203 2009-08-24 12:03:16Z jwierum $
 */
public class SetupFixture extends Fixture {
	/** alias name to use. */
	public String alias;

	/** class name to use. */
	public String className;

	private TypeAdapter aliasTypeAdapter;
	private TypeAdapter classNameTypeAdapter;

	/**
	 * Default constructor.
	 */
	public SetupFixture() {
		try {
			aliasTypeAdapter = TypeAdapter.on(this, this.getClass().getField("alias"));
			classNameTypeAdapter = TypeAdapter.on(this, this.getClass().getField("className"));
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads a row which contains two cells and registers an alias.
	 *
	 * The first cell must contain the alias, the second one must contains the
	 * fully qualified class name. Using another alias as class name is not
	 * permitted.<br /><br />
	 *
	 * Cross references are resolved.
	 *
	 * @param row row to parse
	 */
	@Override
	public void doRow(final Parse row) {
		if (row.parts.more == null) {
			ignore(row);
			return;
		}

		alias = row.parts.text();
		className = row.parts.more.text();

		FixtureTools.processCell(row.parts, aliasTypeAdapter, this);
		FixtureTools.processCell(row.parts.more, classNameTypeAdapter, this);

		AliasHelper.instance().register(alias, className);
	}
}
