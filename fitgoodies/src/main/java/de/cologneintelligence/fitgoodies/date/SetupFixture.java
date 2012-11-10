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


package de.cologneintelligence.fitgoodies.date;

import java.text.SimpleDateFormat;

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.adapters.AbstractTypeAdapter;
import de.cologneintelligence.fitgoodies.parsers.Parser;
import de.cologneintelligence.fitgoodies.util.DependencyManager;


/**
 * This fixture allows it to set the locale and format of dates.
 * {@link AbstractTypeAdapter} and {@link Parser} use these formats
 * when they parse dates.<br /><br />
 *
 * Example to allow German dates:<br />
 * <table border="1">
 * <tr><td colspan="2">fitgoodies.date.SetupFixture</td></tr>
 * <tr><td>locale</td><td>de_DE</td></tr>
 * <tr><td>format</td><td>dd.MM.yyyy hh:mm:ss</td></tr>
 * </table>
 *
 * @author jwierum
 */
public class SetupFixture extends ActionFixture {
    /**
     * Calls {@link #locale(String)}, using the next cell as its parameter.
     * @throws Exception propagated to fit
     */
    public void locale() throws Exception {
        transformAndEnter();
    }

    /**
     * Calls {@link #format(String)}, using the next cell as its parameter.
     * @throws Exception propagated to fit
     */
    public void format() throws Exception {
        transformAndEnter();
    }

    /**
     * Sets the locale value to <code>locale</code>.
     * @param locale name of the local to use
     * @see de.cologneintelligence.fitgoodies.date.SetupHelper#setLocale(String) SetupHelper.setLocale(String)
     * @see de.cologneintelligence.fitgoodies.date.SetupHelper#getLocale() SetupHelper.getLocale()
     */
    public final void locale(final String locale) {
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        helper.setLocale(locale);
    }

    /**
     * Sets the format value to <code>format</code>.
     * @param format format string which is parsable by {@link SimpleDateFormat}
     * @see de.cologneintelligence.fitgoodies.date.SetupHelper#setFormat(String) SetupHelper.setFormat(String)
     * @see de.cologneintelligence.fitgoodies.date.SetupHelper#getFormat() SetupHelper.getFormat()
     */
    public final void format(final String format) {
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        helper.setFormat(format);
    }
}
