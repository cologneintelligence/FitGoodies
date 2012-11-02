package de.cologneintelligence.fitgoodies.selenium;

import com.thoughtworks.selenium.SeleniumException;

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.selenium.command.CommandFactory;
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
    private int screenshotIndex = 0;

    public SeleniumFixture() {
        super();
    }

    @Override
    public void doCells(final Parse cells) {

        final String command = cells.text();
        try {
            final String[] args = new String[] { getColumnOrEmptyString(cells, 1), getColumnOrEmptyString(cells, 2) };
            String result = CommandFactory.createCommand(command, args).execute();
            checkResult(cells, result);
        } catch (final SeleniumException e) {
            wrong(lastCell(cells), e.getMessage());
        } catch (final Exception e) {
            exception(lastCell(cells), e);
        }
    }

    @Override
    public void wrong(final Parse cell, final String message) {
        super.wrong(cell, message);
        if (SetupHelper.instance().getTakeScreenshots()) {
            takeScreenShot(cell);
        }
    }

    private void addScreenshotLinkToReportPage(final Parse cell, final String fileName) {
        cell.addToBody(" <a href=\"file:///" + fileName + "\">screenshot</a>");
    }

    private void takeScreenShot(final Parse cell) {
        String fileName = createSnapshotFilename(screenshotIndex++);
        CommandFactory.createCommand("captureEntirePageScreenshot", new String[] { fileName, "" }).execute();
        addScreenshotLinkToReportPage(cell, fileName);
    }

    private String createSnapshotFilename(final int index) {
        return RunnerHelper.instance().getResultFilePath() + ".screenshot" + index + ".png";
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

    private void checkResult(final Parse cells, final String result) {
        if (result.startsWith("OK")) {
            right(lastCell(cells));
            info(lastCell(cells), result);
        } else {
            wrong(lastCell(cells), result);
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
