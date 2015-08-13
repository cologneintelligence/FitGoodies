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

import com.thoughtworks.selenium.CommandProcessor;

import de.cologneintelligence.fitgoodies.selenium.command.DefaultSeleniumFactory;
import de.cologneintelligence.fitgoodies.selenium.command.SeleniumFactory;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

/**
 * @author kmussawisade
 */
public class SetupHelper {
    private String serverHost = "localhost";
    private int serverPort = 4444;
    private String browserStartCommand = "*firefox";
    private String browserURL = "http://localhost";
    private CommandProcessor commandProcessor;
    private Integer speed;
    private long timeout = 30000;
    private long retryTimeout = 30000;
    private long retryInterval = 500;
    private boolean takeScreenshots;
    private long sleepBeforeScreenshotMillis=2000L;

    public String getBrowserStartCommand() {
        return browserStartCommand;
    }

    public String getBrowserURL() {
        return browserURL;
    }

    public CommandProcessor getCommandProcessor() {
        if (commandProcessor == null) {
            commandProcessor = DependencyManager.getOrCreate(SeleniumFactory.class, DefaultSeleniumFactory.class)
                    .createCommandProcessor(serverHost, serverPort, browserStartCommand, browserURL);

            if (speed != null) {
                commandProcessor.doCommand("setSpeed", new String[]{Integer.toString(speed)});
            }
        }

        return commandProcessor;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(final long intervalInMs) {
        this.retryInterval = intervalInMs;
    }

    public long getRetryTimeout() {
        return retryTimeout;
    }

    public void setRetryTimeout(final long timeoutInMs) {
        this.retryTimeout = timeoutInMs;
    }

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

    public Long getSleepBeforeScreenshotMillis() {
        return sleepBeforeScreenshotMillis;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(final Integer speed) {
        this.speed = speed;
    }

    public boolean getTakeScreenshots() {
        return takeScreenshots;
    }

    public void setTakeScreenshots(final boolean takeScreenshots) {
        this.takeScreenshots = takeScreenshots;
    }

    public void setSleepBeforeScreenshotMillis(final Long sleepBeforeScreenshotMillis) {
        this.sleepBeforeScreenshotMillis = sleepBeforeScreenshotMillis;
    }

    public long sleepBeforeScreenshot() {
        return sleepBeforeScreenshotMillis;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    public void setBrowserStartCommand(final String browserStartCommand) {
        this.browserStartCommand = browserStartCommand;
    }

    public void setBrowserURL(final String browserURL) {
        this.browserURL = browserURL;
    }

    public void setCommandProcessor(final CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;

    }

    public void start(final String startConfig) {
        getCommandProcessor().start(startConfig);
        getCommandProcessor().doCommand("setTimeout", new String[]{Long.toString(timeout)});
    }

    public void stop() {
        getCommandProcessor().stop();
        commandProcessor = null;
    }
}
