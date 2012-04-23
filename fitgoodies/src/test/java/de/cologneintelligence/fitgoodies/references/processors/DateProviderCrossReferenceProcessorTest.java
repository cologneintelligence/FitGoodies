package de.cologneintelligence.fitgoodies.references.processors;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;

import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.util.DateProviderImpl;

public class DateProviderCrossReferenceProcessorTest extends TestCase {

    public void testGetCurrentDate() throws Exception {
        Mockery mockery = new Mockery();
        final String date = "21.01.2009";
        final DateProvider dateProvider = mockery.mock(DateProvider.class);
        mockery.checking(new Expectations() {{
           oneOf(dateProvider).getCurrentDate();
           will(returnValue(date));
        }});

        DateProviderCrossReferenceProcessor processor = new DateProviderCrossReferenceProcessor(dateProvider);

        CrossReference cr = new CrossReference("getCurrentDate", "dateProvider", null, processor);
        assertEquals(date, processor.processMatch(cr, null));

    }

    public void testGetCurrentDateWithFormat() throws Exception {
        String format = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        final String date = simpleDateFormat.format(new Date());
        
        final DateProvider dateProvider = new DateProviderImpl();
        DateProviderCrossReferenceProcessor processor = new DateProviderCrossReferenceProcessor(dateProvider);

        CrossReference cr = new CrossReference("getCurrentDate", "dateProvider", format, processor);
        assertEquals(date, processor.processMatch(cr, null));

    }

    public void testGetCurrentDateWithDefaultFormat() throws Exception {
        String format = "dd.MM.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        final String date = simpleDateFormat.format(new Date());
        
        final DateProvider dateProvider = new DateProviderImpl();
        DateProviderCrossReferenceProcessor processor = new DateProviderCrossReferenceProcessor(dateProvider);

        CrossReference cr = new CrossReference("getCurrentDate", "dateProvider", null, processor);
        assertEquals(date, processor.processMatch(cr, null));

    }
}
