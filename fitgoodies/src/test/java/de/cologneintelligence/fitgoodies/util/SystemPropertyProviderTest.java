package de.cologneintelligence.fitgoodies.util;

import de.cologneintelligence.fitgoodies.references.processors.PropertyProvider;
import de.cologneintelligence.fitgoodies.util.SystemPropertyProvider;
import junit.framework.TestCase;

public class SystemPropertyProviderTest extends TestCase {

	public void testGetProperty() throws Exception {
		PropertyProvider propertyProvider = new SystemPropertyProvider();
		assertEquals(System.getProperty("java.home"), propertyProvider.getProperty("java.home"));		
	}
}
