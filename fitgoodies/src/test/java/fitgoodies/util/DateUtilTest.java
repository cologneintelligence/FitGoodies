package fitgoodies.util;

import java.util.Date;

import junit.framework.TestCase;

public class DateUtilTest extends TestCase {
    
    public void testGetDateAsString(){
        Date date = new Date(1234567891234l); // 14.02.2009
        assertEquals("14.02.2009", DateUtil.getDateAsString(date, DateUtil.DATE_PATTERN_DD_MM_YYYY));
    }

}
