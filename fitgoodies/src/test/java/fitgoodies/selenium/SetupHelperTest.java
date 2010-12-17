package fitgoodies.selenium;

import fitgoodies.FitGoodiesTestCase;

public class SetupHelperTest extends FitGoodiesTestCase {

	public void testSetupHelper() {
		SetupHelper helper1 = SetupHelper.instance();
		assertNotNull(helper1);
		SetupHelper helper2 = SetupHelper.instance();
		assertNotNull(helper2);
		assertSame(helper1, helper2);
	}
	
	public void testPathToServlet() {
		//CommandProcessor commandProcessor = new HttpCommandProcessor(pathToServlet, browserStartCommand, browserURL)
		SetupHelper helper = SetupHelper.instance();
		
		
	}
}
