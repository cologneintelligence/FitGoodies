package fitgoodies.references.processors;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;

import fitgoodies.references.CrossReference;

public class DateProviderCrossReferenceProcessorTest extends TestCase {

	public void testGetCurrentDate() throws Exception {
		Mockery mockery = new Mockery();
		final String date = "21.01.2009";
		final DateProvider dateProvider = mockery.mock(DateProvider.class);
		mockery.checking(new Expectations(){{
			oneOf(dateProvider).getCurrentDate(); will(returnValue(date));
		}});
		
		DateProviderCrossReferenceProcessor processor = new DateProviderCrossReferenceProcessor(dateProvider);
		
		CrossReference cr = new CrossReference("getCurrentDate", "dateProvider", null, processor);		
		assertEquals(date, processor.processMatch(cr, null));
		
	}
}
