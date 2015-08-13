package de.cologneintelligence.fitgoodies.selenium.command;

import com.thoughtworks.selenium.CommandProcessor;

public interface SeleniumFactory {
    CommandProcessor createCommandProcessor(String host, int port, String browserStartCommand, String url);
}
