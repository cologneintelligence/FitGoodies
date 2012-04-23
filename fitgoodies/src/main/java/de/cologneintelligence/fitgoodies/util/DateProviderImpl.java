package de.cologneintelligence.fitgoodies.util;

import java.util.Date;

import de.cologneintelligence.fitgoodies.references.processors.DateProvider;


public class DateProviderImpl implements DateProvider {

    @Override
    public String getCurrentDate() {
        return DateUtil.getDateAsString(new Date(), DateUtil.DATE_PATTERN_DD_MM_YYYY);
        
    }

    @Override
    public String getCurrentDate(String format) {
        return DateUtil.getDateAsString(new Date(), format);
    }

}
