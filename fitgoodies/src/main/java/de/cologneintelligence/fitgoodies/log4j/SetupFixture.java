/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
 * This file is part of FitGoodies.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.cologneintelligence.fitgoodies.log4j;

import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.Parse;


/**
 * This fixture is used to start captures of log4j logger.
 * To capture the output, the logger <em>and</em> the specific appender must be
 * provided. This means, that the appender must have a name.
 * <p>
 *
 * Example to monitor the output of the appender &quot;R&quot; of the root logger,
 * to monitor the output the output of the appender &quot;stdout&quot; of the
 * org.example.myclass and do clear the cached log entries of &quot;stderr&quot;:
 * <p>
 *
 *  <!-- and to stop monitoring the appender &quot;stderr&quot; of the root logger-->
 *
 * <table border="1" summary="">
 * <tr><td>fitgoodies.log4j.SetupFixture</td></tr>
 * <tr><td>monitorRoot</td><td>R</td></tr>
 * <tr><td>monitor</td><td>org.example.myclass</td><td>stdout</td></tr>
 * <tr><td>clear</td><td>org.example.myclass</td><td>stderr</td></tr>
 * </table>
 *
 */
public class SetupFixture extends Fixture {
    private final LoggerProvider loggerProvider;

    /**
     * Creates a new SetupFixture which uses {@code provider} to get
     * loggers. This method is mainly used for testing.
     * @param provider provider to get logger instances
     */
    public SetupFixture(final LoggerProvider provider) {
        loggerProvider = provider;
    }

    /**
     * Create a new SetupFixutre which captures log4j loggers.
     */
    public SetupFixture() {
        this(new LoggerProvider());
    }

    /**
     * Processes the content of {@code cells}.
     * @param cells cells to process
     */
    @Override
    protected void doCells(final Parse cells) {
        String name = cells.text();

        processRowWithCommand(cells, name);
    }

    private void processRowWithCommand(final Parse row, final String name) {
        if ("monitorRoot".equalsIgnoreCase(name)) {
            processRowWithMonitorRoot(row);
        } else if ("monitor".equalsIgnoreCase(name)) {
            processRowWithMonitor(row);
        } else if("clear".equalsIgnoreCase(name)) {
            processRowWithClear(row);
        }
    }

    private void processRowWithClear(final Parse row) {
        String className = row.more.text();
        String appenderName = row.more.more.text();

        LogHelper helper = DependencyManager.getOrCreate(LogHelper.class);
        helper.clear(loggerProvider.getLogger(className), appenderName);
    }

    private void processRowWithMonitor(final Parse row) {
        String className = row.more.text();
        String appenderName = row.more.more.text();

        addMonitor(className, appenderName);
    }

    private void processRowWithMonitorRoot(final Parse row) {
        String appenderName = row.more.text();
        addRootMonitor(appenderName);
    }

    private void addRootMonitor(final String appenderName) {
        LogHelper helper = DependencyManager.getOrCreate(LogHelper.class);
        helper.addCaptureToLogger(loggerProvider.getRootLogger(),
                appenderName);
    }

    private void addMonitor(final String className, final String appenderName) {
        LogHelper helper = DependencyManager.getOrCreate(LogHelper.class);
        helper.addCaptureToLogger(
                loggerProvider.getLogger(className), appenderName);
    }
}
