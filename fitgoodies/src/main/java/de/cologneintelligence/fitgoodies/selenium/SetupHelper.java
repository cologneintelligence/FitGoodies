package de.cologneintelligence.fitgoodies.selenium;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.HttpCommandProcessor;

/**
 * @author kmussawisade
 */
public class SetupHelper {
    private String serverHost = "localhost";
    private int serverPort = 4444;
    private String browserStartCommand = "*firefox";
    private String browserURL = "http://localhost";
    private CommandProcessor commandProcessor;
    private String speed;
    private Long timeout = 30000L;
    private Long interval = 500L;
    private boolean takeScreenshots;

    private Long sleepBeforeScreenshotMillis=2000L;

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(final String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(final String serverPort) {
        this.serverPort = Integer.parseInt(serverPort);
    }

    public String getBrowserStartCommand() {
        return browserStartCommand;
    }

    public void setBrowserStartCommand(final String browserStartCommand) {
        this.browserStartCommand = browserStartCommand;
    }

    public String getBrowserURL() {
        return browserURL;
    }

    public void setBrowserURL(final String browserURL) {
        this.browserURL = browserURL;
    }

    public CommandProcessor getCommandProcessor() {
        if (commandProcessor == null) {

            commandProcessor = new HttpCommandProcessor(
                    serverHost, serverPort, browserStartCommand, browserURL);
            if (getSpeed() != null) {
                commandProcessor.doCommand("setSpeed", new String[]{getSpeed()});
            }
        }

        return commandProcessor;
    }

    public void setCommandProcessor(final CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;

    }

    public void setSpeed(final String speed) {
        this.speed = speed;
    }


    public void setTimeout(final String timeout) {
        this.timeout = Long.parseLong(timeout);
    }

    public void setInterval(final String interval) {
        this.interval = Long.parseLong(interval);
    }

    public void setTakeScreenshots(final boolean takeScreenshots) {
        this.takeScreenshots = takeScreenshots;
    }

    public Long getSleepBeforeScreenshotMillis() {
        return sleepBeforeScreenshotMillis;
    }

    public void setSleepBeforeScreenshotMillis(final Long sleepBeforeScreenshotMillis) {
        this.sleepBeforeScreenshotMillis = sleepBeforeScreenshotMillis;
    }

    public void start(final String startConfig) {
        getCommandProcessor().start(startConfig);
    }

    public void stop() {
        getCommandProcessor().stop();
        commandProcessor = null;
    }

    public String getSpeed() {
        return this.speed;
    }

    public Long getTimeout() {
        return timeout;
    }

    public Long getInterval() {
        return interval;
    }

    public boolean getTakeScreenshots() {
        return takeScreenshots;
    }

    public Long sleepBeforeScreenshot() {
        return sleepBeforeScreenshotMillis;
    }


}
