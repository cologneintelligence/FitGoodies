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


package de.cologneintelligence.fitgoodies.alias;

import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.FixtureRunner;

/**
 * This class is for internal use only. <strong>Do not use it.</strong>
 *
 * This class is used by {@link de.cologneintelligence.fitgoodies.runners.FitRunner} to
 * resolve aliases.
 *
 */
public final class AliasEnabledFixtureRunner extends FixtureRunner {


    /**
     * Loads a fixutre by its fully quallified {@code className}.
     * If the {@code className} is an alias, the referenced class is used.
     *
     * @param fixtureName name of the fixture to load.
     * @return instance of the class which is referenced by {@code fixtureName}
     *
     * @throws RuntimeException propagated to fit,
     * 		thrown if something different went wrong
     */
    @Override
    public fit.Fixture loadFixture(final String fixtureName) {
        AliasHelper helper = DependencyManager.getOrCreate(AliasHelper.class);
        String realName = helper.getClazz(fixtureName);

        return super.loadFixture(realName);
    }
}
