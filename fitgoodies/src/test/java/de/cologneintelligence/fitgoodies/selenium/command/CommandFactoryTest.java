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

import com.thoughtworks.selenium.CommandProcessor;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandFactoryTest extends FitGoodiesTestCase {
    private final String[] args = new String[]{};

    @Before
    public void setUp() throws Exception {
        SeleniumFactory factory = mock(SeleniumFactory.class);
        CommandProcessor seleniumCommand = mock(CommandProcessor.class);
        DependencyManager.inject(SeleniumFactory.class, factory);

        when(factory.createCommandProcessor("localhost", 4444, "*firefox", "http://localhost"))
                .thenReturn(seleniumCommand);
    }

    @Test
    public void testCommandAndRetry() {
        assertThat(CommandFactory.createCommand("commandAndRetry", args, new SetupHelper()).getClass(), (Matcher) is(sameInstance(RetryCommand.class)));
        assertThat(CommandFactory.createCommand("blaAndRetry", args, new SetupHelper()).getClass(), (Matcher) is(sameInstance(RetryCommand.class)));
    }

    @Test
    public void testCommandOpen() {
        assertThat(CommandFactory.createCommand("open", args, new SetupHelper()).getClass(), (Matcher) is(sameInstance(OpenCommand.class)));
    }

    @Test
    public void testCommandCaptureEntirePageScreenshot() {
        assertThat(CommandFactory.createCommand("captureEntirePageScreenshot", args,
                new SetupHelper()).getClass(), (Matcher) is(sameInstance(CaptureEntirePageScreenshotCommand.class)));
    }

    @Test
    public void testCommand() {
        assertThat(CommandFactory.createCommand("command", args, new SetupHelper()).getClass(), (Matcher) is(sameInstance(SeleniumCommand.class)));
        assertThat(CommandFactory.createCommand("bla", args, new SetupHelper()).getClass(), (Matcher) is(sameInstance(SeleniumCommand.class)));
    }
}
