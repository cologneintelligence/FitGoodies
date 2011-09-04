package de.cologneintelligence.fitgoodies.selenium;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.selenium.SetupHelper;

public class SetupHelperTest extends FitGoodiesTestCase {

	public void testSetupHelper() {
		SetupHelper helper1 = SetupHelper.instance();
		assertNotNull(helper1);
		SetupHelper helper2 = SetupHelper.instance();
		assertNotNull(helper2);
		assertSame(helper1, helper2);
	}
	
}
