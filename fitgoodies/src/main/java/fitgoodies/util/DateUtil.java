package fitgoodies.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    
    public static String DATE_PATTERN_DD_MM_YYYY = "dd.MM.yyyy"; 
    
    public static String getDateAsString(Date date, String formatPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatPattern);
        return sdf.format(date);
    }

}
