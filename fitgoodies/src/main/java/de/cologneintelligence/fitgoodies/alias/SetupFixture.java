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

import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.ValueReceiver;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

/**
 * Fixture to register aliases at runtime.
 *
 */
public class SetupFixture extends Fixture {
    /** alias name to use. */
    public String alias;

    /** class name to use. */
    public String className;

    private ValueReceiver aliasValueReceiver;
    private ValueReceiver classNameValueReceiver;

    /**
     * Default constructor.
     */
    public SetupFixture() {
        try {
            aliasValueReceiver = ValueReceiver.on(this, this.getClass().getField("alias"));
            classNameValueReceiver = ValueReceiver.on(this, this.getClass().getField("className"));
        } catch (final SecurityException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads a row which contains two cells and registers an alias.
     *
     * The first cell must contain the alias, the second one must contains the
     * fully qualified class name. Using another alias as class name is not
     * permitted.
     * <p>
     *
     * Cross references are resolved.
     *
     * @param row row to parse
     */
    @Override
    protected void doRow(final Parse row) {
        if (row.parts.more == null) {
            ignore(row);
            return;
        }

        alias = row.parts.text();
        className = row.parts.more.text();

        processCell(row.parts, aliasValueReceiver);
        processCell(row.parts.more, classNameValueReceiver);

        final AliasHelper aliasHelper = DependencyManager.getOrCreate(AliasHelper.class);
        aliasHelper.register(alias, className);
    }
}
