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

package de.cologneintelligence.fitgoodies.selenium;

import java.text.ParseException;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.internal.NamedSequence;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.SeleniumException;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.processors.DateProvider;
import de.cologneintelligence.fitgoodies.references.processors.DateProviderCrossReferenceProcessor;
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;

public class SeleniumFixtureTest extends FitGoodiesTestCase {
    private CommandProcessor commandProcessor;
    private SeleniumFixture fixture;
    private Parse table;
    private final String[] args = new String[]{"arg1", "arg2"};
    private SetupHelper helper;


    @Override
    public void setUp() throws Exception {
        super.setUp();

        final RunnerHelper runnerHelper = DependencyManager.getOrCreate(RunnerHelper.class);
        helper = DependencyManager.getOrCreate(SetupHelper.class);

        commandProcessor = mock(CommandProcessor.class);
        helper.setCommandProcessor(commandProcessor);
        helper.setRetryTimeout(500);
        helper.setRetryInterval(100);
        helper.setTakeScreenshots(false);
        helper.setSleepBeforeScreenshotMillis(1L);

        runnerHelper.setResultFilePath("fixture.html");
        fixture = new SeleniumFixture();

        table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>command</td><td>arg1</td><td>arg2</td></tr>"
                        + "</table>");
    }

    public void testInvokeSeleniumCommandReturnsOK() throws Exception {
        doCommandReturnsOK();
        fixture.doTable(table);
        assertRightCell("arg2");
    }


    public void testInvokeSeleniumCommandReturnsNOKWithScreenshot() throws Exception {
        helper.setTakeScreenshots(true);
        doCommandReturnsNOK();
        checkTakingScreenshot(0);
        fixture.doTable(table);
        assertWrongCell("arg2");
    }

    public void testInvokeSeleniumCommandReturnsNOKWithTwoScreenshots() throws Exception {
        helper.setTakeScreenshots(true);
        createTwoCommandsTable();
        doCommandReturnsNOK();
        checkTakingScreenshot(0);
        doCommandReturnsNOK();
        checkTakingScreenshot(1);
        fixture.doTable(table);
        assertCell(0,2,0);
    }


    public void testInvokeSeleniumCommandReturnsNOK() throws Exception {
        doCommandReturnsNOK();
        fixture.doTable(table);
        assertWrongCell("arg2");
    }

    public void testInvokeSeleniumCommandThrowsSeleniumExceptionTakeScreenshot() throws Exception {
        helper.setTakeScreenshots(true);
        doCommandThrowsException();
        checkTakingScreenshot(0);
        fixture.doTable(table);
        assertWrongCell("Error: something is wrong!");
        assertTrue(thirdCell().body, thirdCell().body.contains("<a href=\"file:///fixture.html.screenshot0.png\">screenshot</a>"));
    }

    public void testInvokeSeleniumCommandThrowsSeleniumException() throws Exception {
        doCommandThrowsException();
        fixture.doTable(table);
        assertWrongCell("Error: something is wrong!");
    }

    public void testInvokeSeleniumCommandReturnsNOKAndRetry() throws Exception {
        doCommandCalled4TimesReturnsEachTimeNOK();
        fixture.doTable(table);
        assertWrongCell("NOK; attempts: ");
    }



    public void testInvokeSeleniumCommandThrowsSeleniumExceptionAndRetry() throws Exception {
        createCommandAndRetryTable();
        doCommandCalled4TimesEachTimeThrowsException();
        fixture.doTable(table);
        assertWrongCell("NOK; attempts: ");
    }



    public void testInvokeSeleniumCommandThrowsSeleniumExceptionAndRetryWithSuccessAfterThree() throws Exception {
        createCommandAndRetryTable();
        doCommandCalled4TimesLastTimeReturnsOK();
        fixture.doTable(table);
        assertRightCell("arg2 OK; attempts: 3 times");
    }


    public void testInvokeSeleniumCommandThrowsException() throws Exception {
        assertEquals(0, fixture.counts.exceptions);
        doCommandThrowsRuntimeException();
        fixture.doTable(table);
        assertExceptionCell("java.lang.RuntimeException: Error");
    }

    public void testInvokeSeleniumWithCrossReference() throws Exception {
        final Parse table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>command</td><td>arg1</td><td>${dateProvider.getCurrentDate()}</td></tr>"
                        + "</table>");
        final DateProvider dateProvider = mock(DateProvider.class);
        final String date = "21.01.2009";
        final DateProviderCrossReferenceProcessor processor = new DateProviderCrossReferenceProcessor(dateProvider);
        final CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
        helper.getProcessors().remove(processor);
        helper.getProcessors().add(processor);
        checking(new Expectations() {{
            oneOf(dateProvider).getCurrentDate();
            will(returnValue(date));
        }});

        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("command", new String[]{"arg1", date});
            will(returnValue("OK"));
        }});
        fixture.doTable(table);
        assertRightCell();
    }

    public void testInvokeSeleniumWithoutParameters() throws Exception {
        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("command", new String[]{"", ""});
            will(returnValue("OK"));
        }});

        table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>command</td></tr>"
                        + "</table>");

        fixture.doTable(table);
        assertRightCell();
    }

    public void testInvokeSeleniumOpenCommand() throws Exception {
        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("open", new String[]{"", ""});
            will(throwException(new SeleniumException("Error")));
        }});
        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("waitForPageToLoad", new String[] { "50000", });
            will(returnValue("OK"));
        }});

        table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>open</td></tr>"
                        + "</table>");

        fixture.doTable(table);
        assertRightCell();
    }

    private void createTwoCommandsTable() throws ParseException {
        table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>command</td><td>arg1</td><td>arg2</td></tr>"
                        + "<tr><td>command</td><td>arg1</td><td>arg2</td></tr>"
                        + "</table>");
    }

    private void doCommandThrowsRuntimeException() {
        final RuntimeException runtimeException = new RuntimeException("Error");
        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("command", args);
            will(throwException(runtimeException));
        }});
    }

    private void doCommandCalled4TimesLastTimeReturnsOK() {
        checking(new Expectations() {{
            final Sequence sequence = new NamedSequence("sequence");
            oneOf(commandProcessor).doCommand("command", args);
            will(throwException(new SeleniumException("Error")));inSequence(sequence);
            oneOf(commandProcessor).doCommand("command", args);
            will(throwException(new SeleniumException("Error")));inSequence(sequence);
            oneOf(commandProcessor).doCommand("command", args);
            will(returnValue("OK"));inSequence(sequence);
        }});
    }

    private void createCommandAndRetryTable() throws ParseException {
        table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>commandAndRetry</td><td>arg1</td><td>arg2</td></tr>"
                        + "</table>");
    }

    private void doCommandCalled4TimesEachTimeThrowsException() throws Exception {
        checking(new Expectations() {{
            atLeast(4).of(commandProcessor).doCommand("command", args);
            will(throwException(new SeleniumException("Error")));
        }});
    }

    private void doCommandCalled4TimesReturnsEachTimeNOK() throws Exception {
        createCommandAndRetryTable();

        checking(new Expectations() {{
            atLeast(4).of(commandProcessor).doCommand("command", args);
            will(returnValue("NOK"));
        }});
    }

    private void doCommandReturnsOK() {
        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("command", args);
            will(returnValue("OK"));
        }});
    }

    private void doCommandThrowsException() {
        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("command", args);
            will(throwException(new SeleniumException("Error: something is wrong!")));
        }});
    }

    private void doCommandReturnsNOK() {
        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("command", args);
            will(returnValue("NOK"));
        }});
    }

    private void checkTakingScreenshot(final int index) {
        checking(new Expectations() {{
            oneOf(commandProcessor).doCommand("captureEntirePageScreenshot",
                    new String[]{"fixture.html.screenshot" + index +".png", ""});
        }});
    }

    private void assertRightCell(final String text) {
        assertRightCell();
        thirdCellContains(text);
    }

    private void assertWrongCell(final String text) {
        assertCell(0,1,0);
        thirdCellContains(text);
    }

    private void assertExceptionCell(final String text) {
        assertCell(0,0,1);
        thirdCellContains(text);
    }

    private void assertRightCell() {
        assertCell(1,0,0);
    }

    private void assertCell(final int right, final int wrong, final int exceptions) {
        assertEquals(right, fixture.counts.right);
        assertEquals(wrong, fixture.counts.wrong);
        assertEquals(exceptions, fixture.counts.exceptions);
    }

    private void thirdCellContains(final String text) {
        final Parse cell = thirdCell();
        assertTrue("expected to contain [" + text + "] but was [" + cell.text() + "]" , cell.text().contains(text));
    }

    private Parse thirdCell() {
        final Parse rows = table.parts;
        final Parse cells = rows.more.parts;
        return cells.more.more;
    }

}