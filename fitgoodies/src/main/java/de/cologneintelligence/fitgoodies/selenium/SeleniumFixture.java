package de.cologneintelligence.fitgoodies.selenium;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.Wait;

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import fit.Parse;

/**
 * Run the selenium-IDE and record your test-case. Save the result as html and
 * copy the table part into a new Html-File. Adjust the first row and add the
 * reference to this class.
 * <table border="1">
 * <tr>
 * <td>fitgoodies.selenium.SeleniumFixture</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>open</td>
 * <td>/application/login.html</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>type</td>
 * <td>id_username</td>
 * <td>username</td>
 * </tr>
 * <tr>
 * <td>type</td>
 * <td>id_password</td>
 * <td>secret</td>
 * </tr>
 * <tr>
 * <td>clickAndWait</td>
 * <td>//input[@name='login']</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>clickAndWait</td>
 * <td>link=Products</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>clickAndRetry</td>
 * <td>link=Available Products</td>
 * <td></td>
 * </tr>
 * </table>
 * 
 * @author kmussawisade
 * 
 */
public class SeleniumFixture extends ActionFixture {
	private final Long timeout = SetupHelper.instance().getTimeout();
	private final Long interval = SetupHelper.instance().getInterval();
	private final Long sleepBeforeScreenshot = SetupHelper.instance().getSleepBeforeScreenshot(); 
	private final CommandProcessor commandProcessor = SetupHelper.instance().getCommandProcessor();

	private int screenshotIndex = 0;

	public SeleniumFixture() {
		super();
	}

	@Override
	public void doCells(final Parse cells) {

		final String command = cells.text();
		try {
			final String[] args = new String[] { getColumnOrEmptyString(cells, 1), getColumnOrEmptyString(cells, 2) };
			if (command.endsWith("AndRetry")) {
				Retry retry = doCommandAndRetry(command, args);
				checkResult(cells, retry.wasOk());
				info(lastCell(cells), retry.attemptMessage());
			} else {
				checkResult(cells, doCommand(command, args));
			}
		} catch (final SeleniumException e) {
			wrong(cells, e);
		} catch (final Exception e) {
			exception(lastCell(cells), e);
		}
	}

	private void wrong(final Parse cells, final Exception e) {
		wrong(lastCell(cells), e.getMessage());
	}

	@Override
	public void wrong(Parse cell) {
		super.wrong(cell);
		if (SetupHelper.instance().getTakeScreenshots()) {
			String fileName = createSnapshotFilename(screenshotIndex++);
			waitBeforeTakingScreenshot();
			takeScreenShot(fileName);
			addScreenshotLinkToReportPage(cell, fileName);
		}
	}

	private void addScreenshotLinkToReportPage(Parse cell, String fileName) {
		cell.addToBody(label("snapshot:") + "<a href=\"file:///" + fileName + "\"</a>");
	}

	private void takeScreenShot(String fileName) {
		commandProcessor.doCommand("captureEntirePageScreenshot", new String[] { fileName, "" });
	}

	private void waitBeforeTakingScreenshot() {
	    try {
			Thread.sleep(sleepBeforeScreenshot);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
    }

	private String createSnapshotFilename(int index ) {
		return RunnerHelper.instance().getResultFilePath() + ".screenshot" + index + ".png";
	}

	private Retry doCommandAndRetry(final String command, final String[] args) {
		String seleniumCommand = command.substring(0, command.indexOf("AndRetry"));
		Retry retry = new Retry(seleniumCommand, args, timeout, interval) {
			@Override
			public boolean command(String command, String[] args) {
				return doCommand(command, args);
			}
		};
		try {
			retry.start("Timeout by " + command + ";");
		} catch (final Wait.WaitTimedOutException e) {
			throw new RetryException(e.getMessage() + retry.attemptMessage());
		}
		return retry;
	}

	private final String getColumnOrEmptyString(final Parse cells, final int column) throws CrossReferenceProcessorShortcutException {

		Parse parse = cells;
		for (int i = 0; i < column; ++i) {
			if (parse.more == null) {
				return "";
			}
			parse = parse.more;
		}

		return CrossReferenceHelper.instance().parseBody(parse.text(), null);
	}

	private void checkResult(final Parse cells, final boolean result) {
		if (result) {
			right(lastCell(cells));
		} else {
			wrong(lastCell(cells));
		}
	}

	private boolean doCommand(final String command, final String[] args) {
		String returnValue;
		if (command.startsWith("open")) {
			try {
				returnValue = commandProcessor.doCommand(command, args);
			} catch (final SeleniumException e) {
				returnValue = commandProcessor.doCommand("waitForPageToLoad", new String[] { "50000", });
			}
		} else if (command.equals("pause")) {
			returnValue = pause(args);
		} else {
			returnValue = commandProcessor.doCommand(command, args);
		}

		return returnValue.startsWith("OK");
	}

	private String pause(final String[] args) throws NumberFormatException {
		try {
			Thread.sleep(Long.parseLong(args[0]));
			return "OK";
		} catch (final InterruptedException e) {
			return e.getMessage();
		}
	}

	protected Parse lastCell(final Parse cells) {
		Parse lastCell = cells;
		int i = 0;
		while (lastCell.more != null && i < 2) {
			++i;
			lastCell = lastCell.more;
		}
		return lastCell;
	}

}
