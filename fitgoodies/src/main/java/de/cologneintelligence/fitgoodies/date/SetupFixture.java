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


package de.cologneintelligence.fitgoodies.date;

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import java.text.SimpleDateFormat;


/**
 * This fixture allows it to set the locale and format of dates.
 * {@link TypeHandler} and {@link Parser} use these formats
 * when they parse dates.
 * <p>
 *
 * Example to allow German dates:
 * <p>
 *
 * <table border="1" summary="">
 * <tr><td>fitgoodies.date.SetupFixture</td></tr>
 * <tr><td>locale</td><td>de_DE</td></tr>
 * <tr><td>format</td><td>dd.MM.yyyy hh:mm:ss</td></tr>
 * </table>
 *
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
     * Sets the locale value to {@code locale}.
     * @param locale name of the local to use
     * @see FitDateHelper#setLocale(String) SetupHelper.setLocale(String)
     * @see FitDateHelper#getLocale() SetupHelper.getLocale()
     */
    public void locale(final String locale) {
        FitDateHelper helper = DependencyManager.getOrCreate(FitDateHelper.class);
        helper.setLocale(locale);
    }

    /**
     * Sets the format value to {@code format}.
     * @param format format string which is parsable by {@link SimpleDateFormat}
     * @see FitDateHelper#setFormat(String) SetupHelper.setFormat(String)
     * @see FitDateHelper#getFormat() SetupHelper.getFormat()
     */
    public void format(final String format) {
        FitDateHelper helper = DependencyManager.getOrCreate(FitDateHelper.class);
        helper.setFormat(format);
    }
}
