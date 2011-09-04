package de.cologneintelligence.fitgoodies.references.processors;

import org.jmock.Expectations;
import org.jmock.Mockery;

import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.processors.EnvironmentPropertyCrossReferenceProcessor;
import de.cologneintelligence.fitgoodies.references.processors.PropertyProvider;

import junit.framework.TestCase;

public class EnvironmentPropertyCrossReferenceProcessorTest extends TestCase {

	public void testGetJavaHome() throws Exception {
		Mockery mockery = new Mockery();
		final PropertyProvider propertyProvider = mockery.mock(PropertyProvider.class);
		mockery.checking(new Expectations(){{
			oneOf(propertyProvider).getProperty("key"); will(returnValue("testProperty"));
		}});
		
		EnvironmentPropertyCrossReferenceProcessor processor = new EnvironmentPropertyCrossReferenceProcessor(propertyProvider);
		
		CrossReference cr = new CrossReference("getProperty", "System", "key", processor);
		assertEquals("testProperty", processor.processMatch(cr  , null));
		
	}
}
