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

package de.cologneintelligence.fitgoodies.selenium.command;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.SeleniumException;

public class OpenCommand extends WrappedCommand {
	public OpenCommand(final String command, final String[] args,
	                   final CommandProcessor commandProcessor) {
		super(command, args, commandProcessor);
	}

	@Override
	public String execute() {
		String returnValue;
		try {
			returnValue = commandProcessor.doCommand(command, args);
		} catch (final SeleniumException e) {
			returnValue = commandProcessor.doCommand("waitForPageToLoad", new String[]{"50000",});
		}
		return returnValue;
	}

}
