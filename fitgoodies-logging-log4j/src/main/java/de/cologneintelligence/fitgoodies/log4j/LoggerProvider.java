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

import org.apache.log4j.Logger;
import org.apache.log4j.spi.AppenderAttachable;

/**
 * LoggerProvider which returns corresponding {@code org.apache.log4j.logger}
 * objects.
 */
public class LoggerProvider {
	/**
	 * Returns the logger that has the name {@code name}.
	 *
	 * @param name name of the logger
	 * @return instance of the logger
	 */
	public AppenderAttachable getLogger(final String name) {
		return Logger.getLogger(name);
	}

	/**
	 * Returns the root logger.
	 *
	 * @return instance of the root logger
	 */
	public AppenderAttachable getRootLogger() {
		return Logger.getRootLogger();
	}
}
