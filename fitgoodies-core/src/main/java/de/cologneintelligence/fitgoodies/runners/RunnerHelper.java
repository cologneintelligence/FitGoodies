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

package de.cologneintelligence.fitgoodies.runners;

import de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper;

import java.io.File;
import java.io.PrintStream;


/**
 * Singleton helper class which holds information about the latest runner.
 */
public final class RunnerHelper {
	private File filePath;
	private Runner runner;
	private File resultFilePath;
	private FileSystemDirectoryHelper dirHelper;
	private PrintStream logStream;

	/**
	 * Sets the current processed file's path.
	 *
	 * @param path the path to the current processed file
	 * @see #getFile() getFilePath
	 */
	public void setFile(final File path) {
		filePath = path;
	}

	/**
	 * Gets the current processed file's path.
	 *
	 * @return the path to the current processed file.
	 * @see #setFile(File) setFile(File)
	 */
	public File getFile() {
		return filePath;
	}

	/**
	 * Sets the currently processed output file.
	 *
	 * @param path the path of the current output file
	 * @see #getResultFile() getResultFilePath()
	 */
	public void setResultFile(final File path) {
		resultFilePath = path;
	}

	/**
	 * Gets the currently processed output file.
	 *
	 * @return the path of the current output file
	 * @see #setResultFile(File) setResultFilePath(String)
	 */
	public File getResultFile() {
		return resultFilePath;
	}

	/**
	 * Sets the current processed file's directory helper.
	 *
	 * @param helper helper of the current processed file.
	 * @see #getHelper() getHelper()
	 */
	public void setHelper(final FileSystemDirectoryHelper helper) {
		dirHelper = helper;
	}

	/**
	 * Gets the current processed file's directory helper.
	 *
	 * @return the helper of the current processed file.
	 * @see #setHelper(de.cologneintelligence.fitgoodies.file.FileSystemDirectoryHelper) setHelper(AbstractDirectoryHelper)
	 */
	public FileSystemDirectoryHelper getHelper() {
		return dirHelper;
	}

	/**
	 * Gets the current <code>Runner</code>.
	 *
	 * @return the current runner
	 * @see #setRunner(Runner) setRunner(Runner)
	 */
	public Runner getRunner() {
		return runner;
	}

	/**
	 * Sets the current <code>Runner</code>.
	 *
	 * @param r the current runner
	 * @see #getRunner() getRunner()
	 */
	public void setRunner(final Runner r) {
		runner = r;
	}

	/**
	 * Sets the current log stream.
	 *
	 * @return the current stream
	 * @see #setLog(PrintStream) setLog(PrintStream)
	 */
	public PrintStream getLog() {
		return logStream;
	}

	/**
	 * Gets the current log stream.
	 *
	 * @param stream the current stream
	 * @see #getLog() getLog()
	 */
	public void setLog(final PrintStream stream) {
		logStream = stream;
	}
}
