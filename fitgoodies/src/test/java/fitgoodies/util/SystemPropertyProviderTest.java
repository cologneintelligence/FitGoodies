package fitgoodies.util;

import fitgoodies.references.processors.PropertyProvider;
import junit.framework.TestCase;

public class SystemPropertyProviderTest extends TestCase {

	public void testGetProperty() throws Exception {
		PropertyProvider propertyProvider = new SystemPropertyProvider();
		assertEquals(System.getProperty("java.home"), propertyProvider.getProperty("java.home"));		
	}
}
