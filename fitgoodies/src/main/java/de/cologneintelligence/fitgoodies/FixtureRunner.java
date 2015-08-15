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

import de.cologneintelligence.fitgoodies.util.FitUtils;
import de.cologneintelligence.fitgoodies.util.RunTime;

import java.util.*;

public class FixtureRunner {

	public Map<String, Object> summary = new HashMap<>();
	public Counts counts = new Counts();

	/* Altered by Rick Mugridge to dispatch on the first Fixture */
	public void doTables(Parse tables) {
		summary.put("run date", new Date());
		summary.put("run elapsed time", new RunTime());
		if (tables != null) {
			interpretFollowingTables(tables);
		}
	}

	/* Added by Rick Mugridge */
	private void interpretFollowingTables(Parse tables) {
		while (tables != null) {
			Parse fixtureName = fixtureName(tables);
			if (fixtureName != null) {
				try {
				    Fixture fixture = getLinkedFixtureWithArgs(tables);
					fixture.doTable(tables);
					counts.tally(fixture.counts());
				} catch (Throwable e) {
					FitUtils.exception(fixtureName, e);
                    counts.exceptions++;
				}
			}
			//listener.tableFinished(tables);
			tables = tables.more;
		}
	}

	/* Added from FitNesse*/
	protected Fixture getLinkedFixtureWithArgs(Parse tables) {
		Parse header = tables.at(0, 0, 0);
		Fixture fixture = loadFixture(header.text());
		fixture.setParams(getArgsForTable(tables));
		return fixture;
	}

	public Parse fixtureName(Parse tables) {
		return tables.at(0, 0, 0);
	}

	public Fixture loadFixture(String fixtureName) {
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
	protected String[] getArgsForTable(Parse table) {
		List<String> argumentList = new ArrayList<>();
		Parse parameters = table.parts.parts.more;
		for (; parameters != null; parameters = parameters.more)
			argumentList.add(parameters.text());
		return argumentList.toArray(new String[argumentList.size()]);
	}
}
