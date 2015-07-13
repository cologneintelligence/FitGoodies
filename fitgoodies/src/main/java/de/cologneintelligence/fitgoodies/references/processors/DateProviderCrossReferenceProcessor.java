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
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;

/**
 * This fixture provides the current date.
 * @author nerdmann
 */
public class DateProviderCrossReferenceProcessor extends AbstractCrossReferenceProcessor {
    private static String PATTERN = "(dateProvider)\\.(getCurrentDate)\\(([^\\)]*)\\)";
    private DateProvider dateProvider;

    /**
     * Default constructor.
     * @param dateProvider setup object
     */
    public DateProviderCrossReferenceProcessor(DateProvider dateProvider) {
        super(PATTERN);
        this.dateProvider = dateProvider;
    }

    @Override
    public String processMatch(CrossReference cr, Object object) throws CrossReferenceProcessorShortcutException {
        String parameter = cr.getParameter();
        if (parameter == null || parameter.isEmpty()) {
            return dateProvider.getCurrentDate();
        }
        return dateProvider.getCurrentDate(parameter);
    }

    /**
     * A user friendly description.
     * @return a description.
     */
    @Override
    public final String info() {
        return "provides current date in dd.MM.yyyy";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateProvider == null) ? 0 : dateProvider.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }




}
