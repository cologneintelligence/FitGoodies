package fitgoodies.selenium;

import fit.Parse;
import fitgoodies.ActionFixture;

/**
 * Sets the connection parameters for the selenium server.<br /><br />
 *
 * Such a setup table could look like this:
 * <table border="1">
 * 		<tr><td colspan="2">fitgoodies.selenium.SetupFixture</td></tr>
 * 		<tr><td>serverHost</td><td>selenium-server-host</td></tr>
 * 		<tr><td>serverPort</td><td>4444</td></tr>
 * 		<tr><td>browserStartCommand</td><td>*firefox</td></tr>
 * 		<tr><td>browserURL</td><td>http://web-application-to-be-tested-by-selenium/</td></tr>
 *      <tr><td>start</td><td>start config</td></tr>
 * </table>
 * @author kmussawisade
 *
 */
public class SetupFixture extends ActionFixture {


	public void serverHost() throws Exception {
		transformAndEnter();
	}
	
	public void serverPort() throws Exception {
		transformAndEnter();	
	}
	
	public void browserStartCommand() throws Exception {
		transformAndEnter();
	}
	
	public void browserURL() throws Exception {
		transformAndEnter();
	}

	public void start() throws Exception {
		transformAndEnter();
	}

	public void serverHost(String serverHost) {
		SetupHelper.instance().setServerHost(serverHost);
	}
	
	public void serverPort(String serverPort) {
		SetupHelper.instance().setServerPort(serverPort);
	}
	
	public void browserStartCommand(String browserStartCommand) {
		SetupHelper.instance().setBrowserStartCommand(browserStartCommand);
	}
	
	public void browserURL(String browserURL) {
		SetupHelper.instance().setBrowserURL(browserURL);
	}
		
	public void start(String startConfig) {
		SetupHelper.instance().start(startConfig);
	}
}
