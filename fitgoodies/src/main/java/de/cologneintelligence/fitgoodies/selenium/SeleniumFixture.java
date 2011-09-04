package de.cologneintelligence.fitgoodies.selenium;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.SeleniumException;

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
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
 * </table>
 *
 * @author kmussawisade
 *
 */
public class SeleniumFixture extends ActionFixture {

	private final CommandProcessor commandProcessor = SetupHelper.instance().getCommandProcessor();
	private String returnValue;

	@Override
	public void doCells(final Parse cells) {

		final String command = cells.text();

		try {
		    final String[] args = new String[] {
		            getColumnOrEmptyString(cells, 1),
		            getColumnOrEmptyString(cells, 2)};
			returnValue = doCommand(command, args);
			final boolean result = returnValue.startsWith("OK");
			checkResult(cells, result);
		} catch (final SeleniumException e) {
            wrong(lastCell(cells), e.getMessage());
        } catch (final Exception e) {
            exception(lastCell(cells), e);
        }
	}

    private final String getColumnOrEmptyString(final Parse cells, final int column)
            throws CrossReferenceProcessorShortcutException {

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

	private String doCommand(final String command, final String[] args) {
		String returnValue;
		if (command.startsWith("open")) {
			try {
				returnValue = commandProcessor.doCommand(command, args);
			} catch (final SeleniumException e) {
				returnValue = commandProcessor.doCommand("waitForPageToLoad", new String[]{"50000",});
			}
		} else if (command.equals("pause")){
	        returnValue = pause(args);
		} else {
            returnValue = commandProcessor.doCommand(command, args);
        }

		return returnValue;
	}

    private String pause(final String[] args) throws NumberFormatException {
        try {
            Thread.sleep(Long.parseLong(args[0]));
            return "OK";
        } catch (final InterruptedException e) {
            return e.getMessage();
        }
    }

    private Parse lastCell(final Parse cells) {
        Parse lastCell = cells;
        int i = 0;
        while (lastCell.more != null && i < 2) {
            ++i;
            lastCell = lastCell.more;
        }
        return lastCell;
    }
}
