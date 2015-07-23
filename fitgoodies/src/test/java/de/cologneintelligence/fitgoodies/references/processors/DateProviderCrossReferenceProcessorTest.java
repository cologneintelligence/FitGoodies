/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
 * This file is part of FitGoodies.
 *
 * FitGoodies is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FitGoodies is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FitGoodies.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.cologneintelligence.fitgoodies.references.processors;

import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.util.DateProvider;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DateProviderCrossReferenceProcessorTest {

    @Test
    public void testGetCurrentDate() throws Exception {
        final String date = "21.01.2009";
        final DateProvider dateProvider = mock(DateProvider.class);

       when(dateProvider.getCurrentDate()).thenReturn(date);

        DateProviderCrossReferenceProcessor processor = new DateProviderCrossReferenceProcessor(dateProvider);

        CrossReference cr = new CrossReference("getCurrentDate", "dateProvider", null, processor);
        assertEquals(date, processor.processMatch(cr, null));

    }

    @Test
    public void testGetCurrentDateWithFormat() throws Exception {
        String format = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        final String date = simpleDateFormat.format(new Date());

        final DateProvider dateProvider = new DateProvider();
        DateProviderCrossReferenceProcessor processor = new DateProviderCrossReferenceProcessor(dateProvider);

        CrossReference cr = new CrossReference("getCurrentDate", "dateProvider", format, processor);
        assertEquals(date, processor.processMatch(cr, null));

    }

    @Test
    public void testGetCurrentDateWithDefaultFormat() throws Exception {
        String format = "dd.MM.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        final String date = simpleDateFormat.format(new Date());

        final DateProvider dateProvider = new DateProvider();
        DateProviderCrossReferenceProcessor processor = new DateProviderCrossReferenceProcessor(dateProvider);

        CrossReference cr = new CrossReference("getCurrentDate", "dateProvider", null, processor);
        assertEquals(date, processor.processMatch(cr, null));

    }
}
