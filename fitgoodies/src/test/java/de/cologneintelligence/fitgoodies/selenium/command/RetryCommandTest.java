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
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class RetryCommandTest extends FitGoodiesTestCase {
    private SetupHelper helper;
    private CommandProcessor commandProcessor;
    private WrappedCommand retryCommand;

    private final String[] args = new String[]{"arg1", "arg2"};

    @Before
    public void setUp() throws Exception {
        final RunnerHelper runnerHelper = DependencyManager.getOrCreate(
                RunnerHelper.class);

        helper = DependencyManager.getOrCreate(SetupHelper.class);
        commandProcessor = mock(CommandProcessor.class);
        helper.setCommandProcessor(commandProcessor);
        helper.setTakeScreenshots(false);
        helper.setSleepBeforeScreenshotMillis(1L);
        runnerHelper.setResultFile(new File("fixture.html"));
        retryCommand = CommandFactory.createCommand("commandAndRetry", args, helper);

    }

    @Test
    public void testDoCommand4Times() {
        helper.setRetryTimeout(200);
        helper.setRetryInterval(50);

        when(commandProcessor.doCommand("command", args)).thenReturn("NOK");

        assertThat(retryCommand.execute(), is(equalTo("NOK; attempts: 4 times")));

        verify(commandProcessor, times(4)).doCommand("command", args);
        verifyNoMoreInteractions(commandProcessor);
    }

    @Test
    public void testDoCommand6Times() {

        final String[] args = new String[]{"arg1", "arg2"};
        helper.setRetryTimeout(600);
        helper.setRetryInterval(100);

        when(commandProcessor.doCommand("command", args)).thenReturn("NOK");

        assertThat(retryCommand.execute(), is(equalTo("NOK; attempts: 6 times")));

        verify(commandProcessor, times(6)).doCommand("command", args);
        verifyNoMoreInteractions(commandProcessor);
    }

    @Test
    public void testDoCommandFirst5ReturnsNOKThenOK() {

        final String[] args = new String[]{"arg1", "arg2"};
        helper.setRetryTimeout(1600);
        helper.setRetryInterval(100);

        when(commandProcessor.doCommand("command", args)).thenReturn("NOK", "NOK", "NOK", "OK");
        assertThat(retryCommand.execute(), is(equalTo("OK; attempts: 4 times")));
    }
}
