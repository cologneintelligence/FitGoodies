package de.cologneintelligence.fitgoodies.selenium.command;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.HttpCommandProcessor;

public class DefaultSeleniumFactory implements SeleniumFactory {
    @Override
    public CommandProcessor createCommandProcessor(final String host, final int port, final String browserStartCommand, final String url) {
        return new HttpCommandProcessor(host, port, browserStartCommand, url);
    }
}
