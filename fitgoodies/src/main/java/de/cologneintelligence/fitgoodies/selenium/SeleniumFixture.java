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

import com.thoughtworks.selenium.SeleniumException;
import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.Parse;
import de.cologneintelligence.fitgoodies.runners.RunnerHelper;
import de.cologneintelligence.fitgoodies.selenium.command.CommandFactory;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

/**
 * Run the selenium-IDE and record your test-case. Save the result as html and
 * copy the table part into a new Html-File. Adjust the first row and add the
 * reference to this class.
 * <table border="1" summary="">
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

    @Override
    protected void doCells(final Parse cells) {

        final String command = cells.text();
        try {
            final String[] args = new String[] { getColumnOrEmptyString(cells, 1),
                    getColumnOrEmptyString(cells, 2) };
            String result = CommandFactory.createCommand(command, args,
                    DependencyManager.getOrCreate(SetupHelper.class)).execute();
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
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        if (helper.getTakeScreenshots()) {
            takeScreenShot(cell);
        }
    }

    private void addScreenshotLinkToReportPage(final Parse cell, final String fileName) {
        cell.addToBody(" <a href=\"file:///" + fileName + "\">screenshot</a>");
    }

    private void takeScreenShot(final Parse cell) {
        String fileName = createSnapshotFilename(screenshotIndex++);
        SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);
        CommandFactory.createCommand("captureEntirePageScreenshot", new String[] { fileName, "" }, helper).execute();
        addScreenshotLinkToReportPage(cell, fileName);
    }

    private String createSnapshotFilename(final int index) {
        RunnerHelper helper = DependencyManager.getOrCreate(RunnerHelper.class);
        return helper.getResultFile() + ".screenshot" + index + ".png";
    }

    private String getColumnOrEmptyString(final Parse cells, final int column) {
        Parse parse = cells;
        for (int i = 0; i < column; ++i) {
            if (parse.more == null) {
                return "";
            }
            parse = parse.more;
        }

        return validator.preProcess(parse);
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
