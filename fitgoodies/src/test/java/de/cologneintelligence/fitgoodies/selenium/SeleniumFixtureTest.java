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

package de.cologneintelligence.fitgoodies.selenium;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.SeleniumException;
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.test.FitGoodiesFixtureTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class SeleniumFixtureTest extends FitGoodiesFixtureTestCase<SeleniumFixture> {
	private CommandProcessor commandProcessor;
	private final String[] args = new String[]{"arg1", "arg2"};
	private SetupHelper helper;

    @Override
    protected Class<SeleniumFixture> getFixtureClass() {
        return SeleniumFixture.class;
    }

    @Before
	public void setUpMocks() throws Exception {
		final RunnerHelper runnerHelper = DependencyManager.getOrCreate(RunnerHelper.class);
		helper = DependencyManager.getOrCreate(SetupHelper.class);

		commandProcessor = mock(CommandProcessor.class);
		helper.setCommandProcessor(commandProcessor);
		helper.setRetryTimeout(500);
		helper.setRetryInterval(100);
		helper.setTakeScreenshots(false);
		helper.setSleepBeforeScreenshotMillis(1L);

		runnerHelper.setResultFile(new File("fixture.html"));

		useTable(tr("command", "arg1", "$arg2"));
        preparePreprocess("arg1", "arg1");
        preparePreprocess("$arg2", "arg2");
    }

	@Test
	public void testInvokeSeleniumCommandReturnsOK() throws Exception {
		doCommandReturnsOKs();
		run();
		assertRightCell("arg2");
	}

	@Test
	public void testInvokeSeleniumCommandReturnsNOKWithScreenshot() throws Exception {
		helper.setTakeScreenshots(true);
		doCommandReturnsNOKs();
		run();
		assertWrongCell("arg2");
		checkTakingScreenshot(0);
	}

	@Test
	public void testInvokeSeleniumCommandReturnsNOKWithTwoScreenshots() throws Exception {
		helper.setTakeScreenshots(true);
		createTwoCommandsTable();
		doCommandReturnsNOKs();
		run();
        assertCounts(0, 2, 0, 0);
        checkTakingScreenshot(0);
		checkTakingScreenshot(1);
	}


	@Test
	public void testInvokeSeleniumCommandReturnsNOK() throws Exception {
		doCommandReturnsNOKs();
		run();
		assertWrongCell("arg2");
	}

	@Test
	public void testInvokeSeleniumCommandThrowsSeleniumExceptionTakeScreenshot() throws Exception {
		helper.setTakeScreenshots(true);
		doCommandThrowsExceptions();
		run();
		checkTakingScreenshot(0);
		assertWrongCell("Error");
		assertThat(htmlAt(0, 2), containsString("<a href=\"file:///fixture.html.screenshot0.png\">screenshot</a>"));
	}

	@Test
	public void testInvokeSeleniumCommandThrowsSeleniumException() throws Exception {
		doCommandThrowsExceptions();
		run();
		assertWrongCell("Error");
	}

	@Test
	public void testInvokeSeleniumCommandReturnsNOKAndRetry() throws Exception {
		doCommandReturnsNOKs();
		createCommandAndRetryTable();
		run();
		assertWrongCell("NOK; attempts: ");
	}

	@Test
	public void testInvokeSeleniumCommandThrowsSeleniumExceptionAndRetry() throws Exception {
		createCommandAndRetryTable();
		doCommandThrowsExceptions();
		run();
		assertWrongCell("NOK; attempts: ");
	}

	@Test
	public void testInvokeSeleniumCommandThrowsSeleniumExceptionAndRetryWithSuccessAfterThree() throws Exception {
		createCommandAndRetryTable();
		doCommandCalled4TimesLastTimeReturnsOK();
		run();
		assertRightCell("OK; attempts: 4 times");
	}


	@Test
	public void testInvokeSeleniumCommandThrowsException() throws Exception {
        assertCounts(0, 0, 0, 0);
		doCommandThrowsRuntimeException();
		run();
		assertExceptionCell("java.lang.RuntimeException: Error");
	}

	@Test
	public void testInvokeSeleniumWithoutParameters() throws Exception {
		when(commandProcessor.doCommand("command", new String[]{"", ""})).thenReturn("OK");

		useTable(tr("command"));
		run();
        assertCounts(1, 0, 0, 0);
    }

	@Test
	public void testInvokeSeleniumOpenCommand() throws Exception {
		when(commandProcessor.doCommand("open", new String[]{"", ""})).thenThrow(new SeleniumException("Error"));
		when(commandProcessor.doCommand("waitForPageToLoad", new String[]{"50000",})).thenReturn("OK");

		useTable(tr("open"));

		run();
        assertCounts(1, 0, 0, 0);
    }

	private void createTwoCommandsTable() {
		useTable(tr("command", "arg1", "$arg2"), tr("command", "arg1", "arg2"));
        preparePreprocess("arg2", "arg2");
    }

	private void doCommandThrowsRuntimeException() {
		final RuntimeException runtimeException = new RuntimeException("Error");
		when(commandProcessor.doCommand("command", args)).thenThrow(runtimeException);
	}

	private void doCommandCalled4TimesLastTimeReturnsOK() {
		when(commandProcessor.doCommand("command", args))
				.thenThrow(
                    new SeleniumException("Error"),
                    new SeleniumException("Error"),
                    new SeleniumException("Error"))
				.thenReturn("OK");
	}

	private void createCommandAndRetryTable() {
		useTable(tr("commandAndRetry", "arg1", "$arg2"));
	}

	private void doCommandThrowsExceptions() {
		when(commandProcessor.doCommand("command", args)).thenThrow(new SeleniumException("Error"));
	}

	private void doCommandReturnsNOKs() {
		when(commandProcessor.doCommand("command", args)).thenReturn("NOK");
	}

	private void doCommandReturnsOKs() {
		when(commandProcessor.doCommand("command", args)).thenReturn("OK");
	}

	private void checkTakingScreenshot(final int index) {
		verify(commandProcessor).doCommand("captureEntirePageScreenshot",
            new String[]{"fixture.html.screenshot" + index + ".png", ""});
	}

	private void assertRightCell(final String text) {
        assertCounts(1, 0, 0, 0);
        thirdCellContains(text);
	}

	private void assertWrongCell(final String text) {
        assertCounts(0, 1, 0, 0);
        thirdCellContains(text);
	}

	private void assertExceptionCell(final String text) {
        assertCounts(0, 0, 0, 1);
        thirdCellContains(text);
	}

    private void thirdCellContains(final String text) {
		assertThat(htmlAt(0, 2), containsString(text));
	}
}
