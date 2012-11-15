/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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

import com.thoughtworks.selenium.SeleniumException;

import de.cologneintelligence.fitgoodies.selenium.Retry;
import de.cologneintelligence.fitgoodies.selenium.RetryException;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;



public class RetryCommand extends WrappedCommand {
    private final SetupHelper helper;

    public RetryCommand(final String command, final String[] args, final SetupHelper helper) {
        super(command, args, helper.getCommandProcessor());
        this.helper = helper;
    }

    @Override
    public String execute() {
        final Retry retry = new Retry(helper.getRetryTimeout(), helper.getRetryInterval()) {
            private String result;
            @Override
            public boolean execute() {
                try{
                    final String seleniumCommand = command.substring(0, command.indexOf("AndRetry"));
                    result = commandProcessor.doCommand(seleniumCommand, args);
                } catch (final SeleniumException e) {
                    throw new RetryException(e.getMessage() + "; " + attemptMessage(attemptCounter()));
                }
                return result.startsWith("OK");
            }
        };
        return (retry.start() ? "OK; " : "NOK; ") + attemptMessage(retry.attemptCounter());
    }

    private String attemptMessage(final int count) {
        return "attempts: " + count + " times";
    }
}
