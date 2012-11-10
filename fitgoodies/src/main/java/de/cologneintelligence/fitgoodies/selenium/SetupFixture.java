package de.cologneintelligence.fitgoodies.selenium;

import de.cologneintelligence.fitgoodies.ActionFixture;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

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
 *      <tr><td>takeScreenshots</td><td>true</td></tr>
 *      <tr><td>start</td><td>start config</td></tr>
 * </table>
 * @author kmussawisade
 *
 */
public class SetupFixture extends ActionFixture {
    private final SetupHelper helper = DependencyManager.getOrCreate(SetupHelper.class);

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

    public void speed() throws Exception {
        transformAndEnter();
    }

    public void timeout() throws Exception {
        transformAndEnter();
    }

    public void interval() throws Exception {
        transformAndEnter();
    }

    public void takeScreenshots() throws Exception {
        transformAndEnter();
    }

    public void sleepBeforeScreenshot() throws Exception {
        transformAndEnter();
    }

    @Override
    public void start() throws Exception {
        transformAndEnter();
    }

    public void stop() throws Exception {
        helper.stop();
    }

    public void serverHost(final String serverHost) {
        helper.setServerHost(serverHost);
    }

    public void serverPort(final String serverPort) {
        helper.setServerPort(Integer.parseInt(serverPort));
    }

    public void browserStartCommand(final String browserStartCommand) {
        helper.setBrowserStartCommand(browserStartCommand);
    }

    public void browserURL(final String browserURL) {
        helper.setBrowserURL(browserURL);
    }

    public void speed(final String speed) {
        helper.setSpeed(speed);
    }

    public void timeout(final String timeout) throws Exception {
        helper.setTimeout(Long.parseLong(timeout));
    }

    public void interval(final String interval) throws Exception {
        helper.setInterval(Long.parseLong(interval));
    }

    public void takeScreenshots(final String takeScreenshots) throws Exception {
        helper.setTakeScreenshots(Boolean.parseBoolean(takeScreenshots));
    }

    public void sleepBeforeScreenshot(final String sleepBeforeScreenshot) throws Exception {
        helper.setSleepBeforeScreenshotMillis(Long.parseLong(sleepBeforeScreenshot));
    }

    public void start(final String startConfig) {
        helper.start(startConfig);
    }
}
