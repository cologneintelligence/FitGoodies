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


package de.cologneintelligence.fitgoodies.alias;

import java.util.Date;

import de.cologneintelligence.fitgoodies.util.DependencyManager;

import fit.Fixture;
import fit.Parse;

/**
 * This class is for internal use only. <strong>Do not use it.</strong>
 *
 * This class is used by {@link de.cologneintelligence.fitgoodies.runners#FitFileRunner} to
 * resolve aliases.
 *
 * @author jwierum
 * @version $Id$
 */
public final class AliasEnabledFixture extends Fixture {
    /**
     * Initializes all fixtures and runs doTable on them.
     *
     * @param tables the tables to interpret
     */
    @SuppressWarnings("unchecked")
    @Override
    public void doTables(final Parse tables) {
        summary.put("run date", new Date());
        summary.put("run elapsed time", new RunTime());
        if (tables != null) {
            Parse nextTable = tables;
            while (nextTable != null) {
                Parse fixtureName = fixtureName(nextTable);
                if (fixtureName != null) {
                    try {
                        fit.Fixture fixture = getLinkedFixtureWithArgs(nextTable);
                        fixture.doTable(nextTable);
                    } catch (Throwable e) {
                        exception(fixtureName, e);
                    }
                }
                nextTable = nextTable.more;
            }
        }
    }

    /**
     * Loads a fixutre by its fully quallified <code>className</code>.
     * If the <code>className</code> is an alias, the referenced class is used.
     *
     * @param fixtureName name of the fixture to load.
     * @return instance of the class which is referenced by <code>fixtureName</code>
     *
     * @throws IllegalAccessException propagated to fit,
     * 		thrown if the class could not been accessed
     * @throws InstantiationException propagated to fit,
     * 		thrown if the class could not been instantiated
     * @throws RuntimeException propagated to fit,
     * 		thrown if something different went wrong
     */
    @Override
    public fit.Fixture loadFixture(final String fixtureName)
            throws InstantiationException, IllegalAccessException {
        AliasHelper helper = DependencyManager.INSTANCE.getOrCreate(AliasHelper.class);
        String realName = helper.getClazz(fixtureName);
        String notFound = "The fixture \"" + realName + "\" was not found.";
        try {
            return (fit.Fixture) (Class.forName(realName).newInstance());
        } catch (ClassCastException e) {
            throw new RuntimeException("\"" + realName
                    + "\" was found, but it's not a fixture.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(notFound, e);
        } catch (NoClassDefFoundError e) {
            throw new RuntimeException(notFound, e);
        }
    }
}
