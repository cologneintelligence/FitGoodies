package fitgoodies.selenium;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.Wait;
import com.thoughtworks.selenium.Wait.WaitTimedOutException;

import fit.Parse;
import fitgoodies.ActionFixture;

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

	private CommandProcessor commandProcessor = SetupHelper.instance().getCommandProcessor();
	boolean result = false;
	private long seleniumTimeout = Wait.DEFAULT_TIMEOUT;
	private String message;	
	private String returnValue;
	
	@Override
	public void doCells(final Parse cells) {
		 
		final String command = cells.text();
		final String[] args = new String[] { cells.more.text(), cells.more.more.text() };
		Wait wait = new Wait() {						
			@Override
			public boolean until() {
				try {
					returnValue = doCommand(command, args);												
					result = returnValue.startsWith("OK");					
					return result;
				} catch (SeleniumException e) {
					result = false;
					message = e.getMessage();									
				} 			
				return result;
			}
		};
	
		try {
			wait.wait(message + "\nTimeout " + seleniumTimeout + " exceeded for command " + command, seleniumTimeout);
		} catch (WaitTimedOutException we) {
			result = false;
			message = we.getMessage();						
		} catch (Exception e) {
			result = false;
			exception(cells.more.more, e);
		}

		checkResult(cells, result, message);
	}

	private void checkResult(Parse cells, boolean result, String message) {
		if (result) {
			right(cells.more.more);
		} else {
			if ( message != null ) {
				wrong(cells.more.more, message);
			}
		}
	}

	public void setSeleniumTimeout(long lseleniumTimeout) {
		this.seleniumTimeout = lseleniumTimeout;
	}

	private String doCommand(final String command, final String[] args) {
		String returnValue;
		if (command.startsWith("open")) {
			try {
				returnValue = commandProcessor.doCommand(command, args);
			} catch (SeleniumException e) {							
				returnValue = commandProcessor.doCommand("waitForPageToLoad", new String[]{"50000",});								
			}						
		} else {
			returnValue = commandProcessor.doCommand(command, args);
		}
		return returnValue;
	}

}
