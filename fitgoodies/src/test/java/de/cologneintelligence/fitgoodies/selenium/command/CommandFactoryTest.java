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
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.selenium.command.CaptureEntirePageScreenshotCommand;
import de.cologneintelligence.fitgoodies.selenium.command.CommandFactory;
import de.cologneintelligence.fitgoodies.selenium.command.OpenCommand;
import de.cologneintelligence.fitgoodies.selenium.command.RetryCommand;
import de.cologneintelligence.fitgoodies.selenium.command.SeleniumCommand;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

public class CommandFactoryTest extends FitGoodiesTestCase {
    private SeleniumFactory factory;
    private CommandProcessor seleniumCommand;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        factory = mock(SeleniumFactory.class);
        seleniumCommand = mock(CommandProcessor.class);
        DependencyManager.inject(SeleniumFactory.class, factory);

        checking(new Expectations() {{
            allowing(factory).createCommandProcessor("localhost", 4444, "*firefox", "http://localhost");
            will(returnValue(seleniumCommand));

            allowing(seleniumCommand).doCommand("setTimeout", new String[]{"30000"});
        }});
    }

    private final String[] args = new String[]{};

    public void testCommandAndRetry() {
        assertEquals(RetryCommand.class,
                CommandFactory.createCommand("commandAndRetry", args, new SetupHelper()).getClass());
        assertEquals(RetryCommand.class,
                CommandFactory.createCommand("blaAndRetry", args, new SetupHelper()).getClass());
    }

    public void testCommandOpen() {
        assertEquals(OpenCommand.class,
                CommandFactory.createCommand("open", args, new SetupHelper()).getClass());
    }

    public void testCommandCaptureEntirePageScreenshot() {
        assertEquals(CaptureEntirePageScreenshotCommand.class,
                CommandFactory.createCommand("captureEntirePageScreenshot", args,
                        new SetupHelper()).getClass());
    }

    public void testCommand() {
        assertEquals(SeleniumCommand.class, CommandFactory.createCommand("command", args, new SetupHelper()).getClass());
        assertEquals(SeleniumCommand.class, CommandFactory.createCommand("bla", args, new SetupHelper()).getClass());
    }
}
