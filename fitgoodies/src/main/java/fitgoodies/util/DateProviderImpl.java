package fitgoodies.util;

import java.util.Date;

import fitgoodies.references.processors.DateProvider;

public class DateProviderImpl implements DateProvider {

    @Override
    public String getCurrentDate() {
        return DateUtil.getDateAsString(new Date(), DateUtil.DATE_PATTERN_DD_MM_YYYY);
        
    }

}
