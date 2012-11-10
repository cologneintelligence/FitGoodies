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
import com.thoughtworks.selenium.SeleniumException;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.selenium.command.CommandFactory;
import de.cologneintelligence.fitgoodies.selenium.command.WrappedCommand;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

public class OpenCommandTest extends FitGoodiesTestCase {

    private CommandProcessor commandProcessor;
    private WrappedCommand openCommand;
    private SetupHelper helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RunnerHelper runnerHelper = DependencyManager.getOrCreate(RunnerHelper.class);
        helper = DependencyManager.getOrCreate(SetupHelper.class);

        commandProcessor = mock(CommandProcessor.class);
        helper.setCommandProcessor(commandProcessor);
        runnerHelper.setResultFilePath("fixture.html");
    }

    public void testDoCommand() {
        final String[] args = new String[]{"arg1", "arg2"};
        openCommand = CommandFactory.createCommand("openSomething", args, helper);

        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("openSomething", args);
            will(throwException(new SeleniumException("Error")));
        }});

        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("waitForPageToLoad", new String[] { "50000", });
            will(returnValue("OK"));
        }});

        assertEquals("OK", openCommand.execute());
    }
}
