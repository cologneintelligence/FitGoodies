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

import java.util.ArrayList;
import java.util.List;

public abstract class WrappedCommand {
	protected final String command;
	protected final String[] args;
	protected final CommandProcessor commandProcessor;

	public WrappedCommand(final String command, final String[] args,
	                      final CommandProcessor commandProcessor) {
		this.command = command;
		this.args = removeNulls(args);
		this.commandProcessor = commandProcessor;
	}

    private String[] removeNulls(String[] args) {
        List<String> list = new ArrayList<>();
        for (String arg : args) {
            if (arg != null) {
                list.add(arg);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public abstract String execute();


}
