package de.cologneintelligence.fitgoodies.selenium;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;

public class SetupHelperTest extends FitGoodiesTestCase {

	public void testSetupHelper() {
		final SetupHelper helper1 = SetupHelper.instance();
		assertNotNull(helper1);
		final SetupHelper helper2 = SetupHelper.instance();
		assertNotNull(helper2);
		assertSame(helper1, helper2);
	}

	public void testDefaultValues() {
	    final SetupHelper helper = SetupHelper.instance();
	    assertEquals(4444, helper.getServerPort());
	    assertEquals("*firefox", helper.getBrowserStartCommand());
	    assertEquals("http://localhost", helper.getBrowserURL());
	    assertEquals("localhost", helper.getServerHost());
	    assertNull(helper.getSpeed());
	}

	public void testGettersAndSetters() {
	    final SetupHelper helper = SetupHelper.instance();
	    helper.setBrowserStartCommand("*chrome");
	    assertEquals("*chrome", helper.getBrowserStartCommand());
	    helper.setBrowserStartCommand("*opera");
        assertEquals("*opera", helper.getBrowserStartCommand());

        helper.setBrowserURL("http://example.org");
        assertEquals("http://example.org", helper.getBrowserURL());
        helper.setBrowserURL("http://example.com");
        assertEquals("http://example.com", helper.getBrowserURL());

        helper.setServerHost("127.0.0.1");
        assertEquals("127.0.0.1", helper.getServerHost());
        helper.setServerHost("192.168.0.1");
        assertEquals("192.168.0.1", helper.getServerHost());

        helper.setServerPort("1234");
        assertEquals(1234, helper.getServerPort());
        helper.setServerPort("4321");
        assertEquals(4321, helper.getServerPort());

        helper.setSpeed("slow");
        assertEquals("slow", helper.getSpeed());
        helper.setSpeed("fast");
        assertEquals("fast", helper.getSpeed());
	}
}
