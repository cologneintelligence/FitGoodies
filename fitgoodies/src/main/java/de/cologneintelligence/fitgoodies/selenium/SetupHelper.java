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
    private long timeout = 30000L;
    private long interval = 500L;
    private boolean takeScreenshots;
    private long sleepBeforeScreenshotMillis=2000L;

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(final String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(final int serverPort) {
        this.serverPort = serverPort;
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

    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    public void setInterval(final long interval) {
        this.interval = interval;
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

    public long getTimeout() {
        return timeout;
    }

    public long getInterval() {
        return interval;
    }

    public boolean getTakeScreenshots() {
        return takeScreenshots;
    }

    public long sleepBeforeScreenshot() {
        return sleepBeforeScreenshotMillis;
    }
}
