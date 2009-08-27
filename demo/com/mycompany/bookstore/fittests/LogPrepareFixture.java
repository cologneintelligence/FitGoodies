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

package com.mycompany.bookstore.fittests;

import java.io.PrintWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import fitgoodies.ActionFixture;

/**
 * @author jwierum
 * @version $Id$
 *
 */
public final class LogPrepareFixture extends ActionFixture {
	public void initializeLogging() {
		ConsoleAppender appender = new ConsoleAppender();
		appender.setWriter(new PrintWriter(System.out));
		appender.setLayout(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
		appender.setName("stdout");
		Logger.getRootLogger().addAppender(appender);
	}
}
