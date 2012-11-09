package de.cologneintelligence.fitgoodies.util;

import java.util.Date;
import java.util.GregorianCalendar;

import de.cologneintelligence.fitgoodies.util.DateUtil;

import junit.framework.TestCase;

public class DateUtilTest extends TestCase {

    public void testGetDateAsString(){
        Date date = new GregorianCalendar(2009, 1, 14).getTime();
        assertEquals("14.02.2009", DateUtil.getDateAsString(date, DateUtil.DATE_PATTERN_DD_MM_YYYY));
    }

}
