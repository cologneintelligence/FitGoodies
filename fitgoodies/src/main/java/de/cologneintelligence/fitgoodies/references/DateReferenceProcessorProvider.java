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

package de.cologneintelligence.fitgoodies.references;

import de.cologneintelligence.fitgoodies.util.DateProvider;
import de.cologneintelligence.fitgoodies.util.DependencyManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This fixture provides the current date.
 * @author nerdmann
 */
public class DateReferenceProcessorProvider implements CellProcessorProvider {
    private static Pattern PATTERN = Pattern.compile(
            "\\$\\{(?:dateProvider\\.)?getCurrentDate\\(([^\\)]*)\\)\\}", Pattern.CASE_INSENSITIVE);

    private DateProvider dateProvider = DependencyManager.getOrCreate(DateProvider.class);

    @Override
    public boolean canProcess(String strippedText) {
        return strippedText != null &&  PATTERN.matcher(strippedText).find();
    }

    @Override
    public CellProcessor create(final String strippedText) {
        return new CellProcessor() {
            @Override
            public String preprocess() {
                Matcher matcher = PATTERN.matcher(strippedText);
                matcher.find();

                String format = matcher.group(1);

                String result;
                if (format.isEmpty()) {
                    result = dateProvider.getCurrentDate();
                } else {
                    result = dateProvider.getCurrentDate(format);
                }

                return strippedText.replaceAll(Pattern.quote(matcher.group(0)), result);
            }
        };
    }
}
