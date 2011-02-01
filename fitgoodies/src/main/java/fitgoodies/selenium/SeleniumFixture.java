package fitgoodies.selenium;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.SeleniumException;

import fit.Parse;
import fitgoodies.ActionFixture;
import fitgoodies.references.CrossReferenceHelper;

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
	private String returnValue;
	
	@Override
	public void doCells(final Parse cells) {
		 
		final String command = cells.text();
		
		try {
		    final String[] args = new String[] { 
		            CrossReferenceHelper.instance().parseBody(cells.more.text(), null), 
		            CrossReferenceHelper.instance().parseBody(cells.more.more.text(),null) };
			returnValue = doCommand(command, args);												
			result = returnValue.startsWith("OK");
			checkResult(cells, result);
		} catch (SeleniumException e) {
            wrong(cells.more.more, e.getMessage());
        } catch (Exception e) {
            exception(cells.more.more, e);
        }			
	}

	private void checkResult(Parse cells, boolean result) {
		if (result) {
			right(cells.more.more);
		} else {
            wrong(cells.more.more);
		}
	}

	private String doCommand(final String command, final String[] args) {
		String returnValue;
		if (command.startsWith("open")) {
			try {
				returnValue = commandProcessor.doCommand(command, args);
			} catch (SeleniumException e) {							
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
            Thread.sleep(new Long (args[0]));
            return "OK";
        } catch (InterruptedException e) {
            return e.getMessage();
        }
    }

}
