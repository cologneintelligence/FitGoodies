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

package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.alias.AliasHelper;
import de.cologneintelligence.fitgoodies.htmlparser.FitDocument;
import de.cologneintelligence.fitgoodies.htmlparser.FitTable;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.RunTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FixtureRunner {

	public Map<String, Object> summary = new HashMap<>();
	public Counts counts = new Counts();

	/* Altered by Rick Mugridge to dispatch on the first Fixture */
	public void doDocument(FitDocument document) {
		summary.put("run date", new Date());
		summary.put("run elapsed time", new RunTime());
		if (document != null) {
			interpretTables(document);
		}
	}

	/* Added by Rick Mugridge */
	private void interpretTables(FitDocument documents) {
        for (FitTable table : documents.tables()) {
            try {
                Fixture fixture = getLinkedFixtureWithArgs(table);
                fixture.doTable(table);
                counts.tally(table.getCounts());
            } catch (Throwable e) {
                table.exception(e);
                counts.exceptions++;
            }
		}
	}

	/* Added from FitNesse*/
	protected Fixture getLinkedFixtureWithArgs(FitTable table) {
		Fixture fixture = loadFixture(table.getFixtureClass());
		fixture.setParams(getArgsForTable(table));
		return fixture;
	}

    /**
     * Loads a fixutre by its fully quallified {@code className}.
     * If the {@code className} is an alias, the referenced class is used.
     *
     * @param fixtureName name of the fixture to load.
     * @return instance of the class which is referenced by {@code fixtureName}
     * @throws RuntimeException propagated to fit,
     *                          thrown if something different went wrong
     */
    private Fixture loadFixture(final String fixtureName) {
        AliasHelper helper = DependencyManager.getOrCreate(AliasHelper.class);
        String realName = helper.getClazz(fixtureName);

        return loadFixtureByName(realName);
    }

	private Fixture loadFixtureByName(String fixtureName) {
		try {
			return (Fixture) (Class.forName(fixtureName).newInstance());
		} catch (ClassCastException e) {
			throw new RuntimeException("\"" + fixtureName + "\" was found, but it's not a fixture.", e);
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			throw new RuntimeException("The fixture \"" + fixtureName + "\" was not found.", e);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("The fixture \"" + fixtureName + "\" could not be instantiated.", e);
		}
	}


	/* Added by Rick Mugridge, from FitNesse */
	protected Map<String, String> getArgsForTable(FitTable table) {
        return table.getArguments();
	}
}
