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


package fitgoodies.runners;

import java.io.PrintStream;

import fitgoodies.file.AbstractDirectoryHelper;

/**
 * Singleton helper class which holds information about the latest runner.
 *
 * @author jwierum
 * @version $Id$
 */
public final class RunnerHelper {
	private static RunnerHelper instance;
	private String filePath;
	private Runner runner;
	private String resultFilePath;
	private AbstractDirectoryHelper dirHelper;
	private PrintStream logStream;

	private RunnerHelper() { }

	/**
	 * Returns a valid instance of this class.
	 * @return an instance of <code>RunnerHelper</code>
	 */
	public static RunnerHelper instance() {
		if (instance == null) {
			instance = new RunnerHelper();
		}
		return instance;
	}

	/**
	 * Resets all data.
	 */
	public static void reset() {
		instance = null;
	}

	/**
	 * Sets the current processed file's path.
	 * @param path the path to the current processed file
	 * @see #getFilePath() getFilePath
	 */
	public void setFilePath(final String path) {
		filePath = path;
	}

	/**
	 * Gets the current processed file's path.
	 * @return the path to the current processed file.
	 * @see #setFilePath(String) setFilePath(String)
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Sets the currently processed output file.
	 * @param path the path of the current output file
	 * @see #getResultFilePath() getResultFilePath()
	 */
	public void setResultFilePath(final String path) {
		resultFilePath = path;
	}

	/**
	 * Gets the currently processed output file.
	 * @return the path of the current output file
	 * @see #setResultFilePath(String) setResultFilePath(String)
	 */
	public String getResultFilePath() {
		return resultFilePath;
	}

	/**
	 * Sets the current processed file's directory helper.
	 * @param helper helper of the current processed file.
	 * @see #getHelper() getHelper()
	 */
	public void setHelper(final AbstractDirectoryHelper helper) {
		dirHelper = helper;
	}

	/**
	 * Gets the current processed file's directory helper.
	 * @return the helper of the current processed file.
	 * @see #setHelper(AbstractDirectoryHelper) setHelper(AbstractDirectoryHelper)
	 */
	public AbstractDirectoryHelper getHelper() {
		return dirHelper;
	}

	/**
	 * Gets the current <code>Runner</code>.
	 * @return the current runner
	 * @see #setRunner(Runner) setRunner(Runner)
	 */
	public Runner getRunner() {
		return runner;
	}

	/**
	 * Sets the current <code>Runner</code>.
	 * @param r the current runner
	 * @see #getRunner() getRunner()
	 */
	public void setRunner(final Runner r) {
		runner = r;
	}

	/**
	 * Sets the current log stream.
	 * @return the current stream
	 * @see #setLog(PrintStream) setLog(PrintStream)
	 */
	public PrintStream getLog() {
		return logStream;
	}

	/**
	 * Gets the current log stream.
	 * @param stream the current stream
	 * @see #getLog() getLog()
	 */
	public void setLog(final PrintStream stream) {
		logStream = stream;
	}
}
