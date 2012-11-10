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

public class CaptureEntirePageScreenshotCommandTest extends FitGoodiesTestCase {
    private CommandProcessor commandProcessor;
    private WrappedCommand command;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        commandProcessor = mock(CommandProcessor.class);
    }

    public void testDoCommand() {
        SetupHelper helper = new SetupHelper();

        helper.setCommandProcessor(commandProcessor);
        helper.setSleepBeforeScreenshotMillis(1L);

        final String[] args = new String[]{"arg1", "arg2"};
        command = CommandFactory.createCommand("captureEntirePageScreenshot", args, helper);
        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("captureEntirePageScreenshot", args);
            will(returnValue("OK"));
        }});

        assertEquals("OK", command.execute());
    }

}
