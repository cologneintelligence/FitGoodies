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
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenCommandTest extends FitGoodiesTestCase {

	private CommandProcessor commandProcessor;
	private SetupHelper helper;

	@Before
	public void setUp() throws Exception {
		RunnerHelper runnerHelper = DependencyManager.getOrCreate(RunnerHelper.class);
		helper = DependencyManager.getOrCreate(SetupHelper.class);

		commandProcessor = mock(CommandProcessor.class);
		helper.setCommandProcessor(commandProcessor);
		runnerHelper.setResultFile(new File("fixture.html"));
	}

	@Test
	public void testDoCommand() {
		final String[] args = new String[]{"arg1", "arg2"};
		WrappedCommand openCommand = CommandFactory.createCommand("openSomething", args, helper);

		when(commandProcessor.doCommand("openSomething", args))
				.thenThrow(new SeleniumException("Error"));

		when(commandProcessor.doCommand("waitForPageToLoad", new String[]{"50000",}))
				.thenReturn("OK");

		assertThat(openCommand.execute(), is(equalTo("OK")));
	}
}
