/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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

package fitgoodies.log4j;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;

/**
 * Sinlgeton class which helps to manage loggers.
 *
 * @author jwierum
 * @version $Id$
 *
 */
public final class LogHelper {
	private LogHelper() { }

	private static LogHelper instance;

	/**
	 * Returns the singleton instance of the LogHelper.
	 * @return an instance of LogHelper
	 */
	public static LogHelper instance() {
		if (instance == null) {
			instance = new LogHelper();
		}
		return instance;
	}

	/**
	 * Resets the internal state of the singleton.
	 */
	public static void reset() {
		instance = null;
	}

	/**
	 * Adds a new CaptureAppender to an existing logger.
	 * @param logger logger to use
	 * @param appenderName name of an existing appender which will be captured
	 */
	public void addCaptureToLogger(final AppenderAttachable logger,
			final String appenderName) {
		Appender currentAppender = logger.getAppender(appenderName);
		Appender captureAppender = CaptureAppender.newAppenderFrom(currentAppender);
		logger.addAppender(captureAppender);
	}

	/**
	 * Returns the CaptureAppender which captures <code>appenderName</code>.
	 * @param logger logger to use
	 * @param appenderName name of existing appender
	 * @return responsible CaptureAppender
	 */
	public Appender getCaptureAppender(final AppenderAttachable logger,
			final String appenderName) {
		return logger.getAppender(CaptureAppender.getAppenderNameFor(appenderName));
	}

	/**
	 * Removes a CaptureAppender from a logger.
	 * @param logger logger to use
	 * @param appenderName name of the appender which is captured
	 */
	public void remove(final AppenderAttachable logger, final String appenderName) {
		logger.removeAppender(CaptureAppender.getAppenderNameFor(appenderName));
	}

	/**
	 * Deletes all cached log entries.
	 * @param logger logger to use
	 * @param appenderName name of the appender which will be cleared
	 */
	public void clear(final AppenderAttachable logger, final String appenderName) {
		((CaptureAppender) logger.getAppender(CaptureAppender.getAppenderNameFor(
				appenderName))).clear();
	}
}