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
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CaptureEntirePageScreenshotCommandTest extends FitGoodiesTestCase {
	private CommandProcessor commandProcessor;

	@Before
	public void setUp() throws Exception {
		commandProcessor = mock(CommandProcessor.class);
	}

	@Test
	public void testDoCommand() {
		SetupHelper helper = new SetupHelper();

		helper.setCommandProcessor(commandProcessor);
		helper.setSleepBeforeScreenshotMillis(1L);

		final String[] args = new String[]{"arg1", "arg2"};
		WrappedCommand command = CommandFactory.createCommand("captureEntirePageScreenshot", args, helper);
		when(commandProcessor.doCommand("captureEntirePageScreenshot", args)).thenReturn("OK");

		assertThat(command.execute(), is(equalTo("OK")));
	}

}
