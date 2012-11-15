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

import org.jmock.Expectations;

import com.thoughtworks.selenium.CommandProcessor;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.selenium.RetryException;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

public class RetryCommandTest extends FitGoodiesTestCase {
    private SetupHelper helper;
    private CommandProcessor commandProcessor;
    private WrappedCommand retryCommand;

    private final String[] args = new String[]{"arg1", "arg2"};

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final RunnerHelper runnerHelper = DependencyManager.getOrCreate(
                RunnerHelper.class);

        helper = DependencyManager.getOrCreate(SetupHelper.class);
        commandProcessor = mock(CommandProcessor.class);
        helper.setCommandProcessor(commandProcessor);
        helper.setTakeScreenshots(false);
        helper.setSleepBeforeScreenshotMillis(1L);
        runnerHelper.setResultFilePath("fixture.html");
        retryCommand = CommandFactory.createCommand("commandAndRetry", args, helper);

    }

    public void testDoCommand4Times() {
        helper.setRetryTimeout(200);
        helper.setRetryInterval(50);

        checking(new Expectations() {{
            exactly(4).of(commandProcessor).doCommand("command", args);
            will(returnValue("NOK"));
        }});
        try {
            retryCommand.execute();
        } catch (final RetryException e) {
            assertEquals("TimeoutError!; attempts: 4/4 times", e.getMessage());
        }
    }

    public void testDoCommand6Times() {

        final String[] args = new String[]{"arg1", "arg2"};
        helper.setRetryTimeout(600);
        helper.setRetryInterval(100);

        checking(new Expectations() {{
            exactly(6).of(commandProcessor).doCommand("command", args);
            will(returnValue("NOK"));
        }});

        try {
            retryCommand.execute();
        } catch (final RetryException e) {
            assertEquals("TimeoutError!; attempts: 6/6 times", e.getMessage());
        }
    }

    public void testDoCommandFirst5ReturnsNOKThenOK() {

        final String[] args = new String[]{"arg1", "arg2"};
        helper.setRetryTimeout(1600);
        helper.setRetryInterval(100);

        checking(new Expectations() {{
            exactly(3).of(commandProcessor).doCommand("command", args);
            will(returnValue("NOK"));
            oneOf(commandProcessor).doCommand("command", args);
            will(returnValue("OK"));
        }});

        assertEquals("OK; attempts: 4 times", retryCommand.execute());
    }
}
