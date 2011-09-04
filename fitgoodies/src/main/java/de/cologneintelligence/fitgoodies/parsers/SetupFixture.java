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


package de.cologneintelligence.fitgoodies.parsers;

import de.cologneintelligence.fitgoodies.ActionFixture;

/**
 * Fixture which allows the user to register new parsers via HTML.
 * To register a parser, simply have the parser in your classpath and load
 * the parser by specifying its fully qualified class name. <br /><br />
 *
 * Example to load com.example.myParser:<br />
 * <table>
 * 	<tr><td colspan="2">fitgoodies.parsers.SetupFixture</td></tr>
 *	<tr><td>load</td><td>com.example.myParser</td></tr>
 * </table>
 *
 * @see ParserHelper ParserHelper
 * @author jwierum
 * @version $Id$
 */
public class SetupFixture extends ActionFixture {
	/**
	 * Calls {@link #load(String)}, using the next cell as its parameter.
	 * @throws Exception propagated to fit
	 * @see #load(String) load(String)
	 */
	public void load() throws Exception {
		transformAndEnter();
	}

	/**
	 * Registers the parser which is identified by its fully qualified class
	 * name <code>className</code>.
	 * @param className fully qualified class name to register
	 * @throws Exception thrown in case of errors. Propagate it to fit.
	 * @see #load() load()
	 */
	public final void load(final String className) throws Exception {
		Parser<?> p = (Parser<?>) Class.forName(className).newInstance();
		ParserHelper.instance().registerParser(p);
	}
}
