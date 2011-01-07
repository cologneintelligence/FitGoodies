package fitgoodies.selenium;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.HttpCommandProcessor;

/**
 * @author kmussawisade
 *
 */
public class SetupHelper {

	private static SetupHelper seleniumSetupHelper;
	private String serverHost;
	private int serverPort;
	private String browserStartCommand;
	private String browserURL;
	private CommandProcessor commandProcessor;

	
	public static SetupHelper instance() {
		if(seleniumSetupHelper == null) {
			seleniumSetupHelper = new SetupHelper();
		}		
		return seleniumSetupHelper;
	}

	public String getServerHost() {
		return serverHost;
	}


	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}


	public int getServerPort() {
		return serverPort;
	}


	public void setServerPort(String serverPort) {
		this.serverPort = Integer.parseInt(serverPort);
	}


	public String getBrowserStartCommand() {
		return browserStartCommand;
	}


	public void setBrowserStartCommand(String browserStartCommand) {
		this.browserStartCommand = browserStartCommand;
	}


	public String getBrowserURL() {
		return browserURL;
	}


	public void setBrowserURL(String browserURL) {
		this.browserURL = browserURL;
	}

	public CommandProcessor getCommandProcessor() {
		if ( commandProcessor == null) {
			commandProcessor =  new HttpCommandProcessor(serverHost, serverPort, browserStartCommand, browserURL);
		}
		return commandProcessor; 
	}

	public void setCommandProcessor(CommandProcessor commandProcessor) {
		this.commandProcessor = commandProcessor;
		
	}

	public void start(String startConfig) {		
		getCommandProcessor().start(startConfig);
	}

	public void stop() {		
		getCommandProcessor().stop();
		commandProcessor = null;
	}

}
