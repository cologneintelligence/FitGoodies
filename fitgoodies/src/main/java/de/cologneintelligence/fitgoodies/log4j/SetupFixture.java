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

package de.cologneintelligence.fitgoodies.log4j;

import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import java.util.List;


/**
 * This fixture is used to start captures of log4j logger.
 * To capture the output, the logger <em>and</em> the specific appender must be
 * provided. This means, that the appender must have a name.
 * <p/>
 * Example to monitor the output of the appender &quot;R&quot; of the root logger,
 * to monitor the output the output of the appender &quot;stdout&quot; of the
 * org.example.myclass and do clear the cached log entries of &quot;stderr&quot;:
 * <p/>
 * <table border="1" summary="">
 * <tr><td>fitgoodies.log4j.SetupFixture</td></tr>
 * <tr><td>monitorRoot</td><td>R</td></tr>
 * <tr><td>monitor</td><td>org.example.myclass</td><td>stdout</td></tr>
 * <tr><td>clear</td><td>org.example.myclass</td><td>stderr</td></tr>
 * </table>
 * <p/>
 * And to stop monitoring the appender &quot;stderr&quot; of the root logger
 * <p/>
 * <table border="1" summary="">
 * <tr><td>fitgoodies.log4j.SetupFixture</td></tr>
 * <tr><td>clear</td><td>org.example.myclass</td><td>stderr</td></tr>
 * </table>
 */
public class SetupFixture extends Fixture {
	private final LoggerProvider loggerProvider;

	/**
	 * Creates a new SetupFixture which uses {@code provider} to get
	 * loggers. This method is mainly used for testing.
	 *
	 * @param provider provider to get logger instances
	 */
	public SetupFixture(LoggerProvider provider) {
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
	 *
	 * @param cells cells to process
	 */
	@Override
	protected void doCells(List<FitCell> cells) {
		String name = cells.get(0).getFitValue();

		processRowWithCommand(cells, name);
	}

	private void processRowWithCommand(List<FitCell> cells, String name) {
		if ("monitorRoot".equalsIgnoreCase(name)) {
			processRowWithMonitorRoot(cells);
		} else if ("monitor".equalsIgnoreCase(name)) {
			processRowWithMonitor(cells);
		} else if ("clear".equalsIgnoreCase(name)) {
			processRowWithClear(cells);
		}
	}

	private void processRowWithClear(List<FitCell> cells) {
		String className = cells.get(1).getFitValue();
		String appenderName = cells.get(2).getFitValue();

		LogHelper helper = DependencyManager.getOrCreate(LogHelper.class);
		helper.clear(loggerProvider.getLogger(className), appenderName);
	}

	private void processRowWithMonitor(List<FitCell> cells) {
		String className = cells.get(1).getFitValue();
		String appenderName = cells.get(2).getFitValue();

		addMonitor(className, appenderName);
	}

	private void processRowWithMonitorRoot(List<FitCell> cells) {
		String appenderName = cells.get(1).getFitValue();
		addRootMonitor(appenderName);
	}

	private void addRootMonitor(String appenderName) {
		LogHelper helper = DependencyManager.getOrCreate(LogHelper.class);
		helper.addCaptureToLogger(loggerProvider.getRootLogger(),
				appenderName);
	}

	private void addMonitor(String className, String appenderName) {
		LogHelper helper = DependencyManager.getOrCreate(LogHelper.class);
		helper.addCaptureToLogger(
				loggerProvider.getLogger(className), appenderName);
	}
}
