package fitgoodies.references.processors;

import org.jmock.Expectations;
import org.jmock.Mockery;

import junit.framework.TestCase;
import fitgoodies.references.CrossReference;

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
