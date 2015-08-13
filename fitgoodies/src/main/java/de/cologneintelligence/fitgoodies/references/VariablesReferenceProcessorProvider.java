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

import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This processor allows to store and receive string values. To keep things clean,
 * the processor optionally supports namespaces.
 */

public class VariablesReferenceProcessorProvider implements CellProcessorProvider {
    private static final Pattern PATTERN =
            Pattern.compile("\\$\\{(?:([^.()]+)\\.)?(get|put|containsValue)\\(\\s*([^,]+)\\s*(?:,\\s*/(.+)/\\s*)?\\)\\}",
                    Pattern.CASE_INSENSITIVE);

    private final Map<String, String> vars = new HashMap<>();

    @Override
    public boolean canProcess(String strippedText) {
        return strippedText != null && PATTERN.matcher(strippedText).find();
    }

    @Override
    public CellProcessor create(final String strippedText) {
        final Matcher pattern = PATTERN.matcher(strippedText);
        pattern.find();

        String namespace = pattern.group(1);
        if (namespace != null) {
            namespace += ".";
        } else {
            namespace = "";
        }

        final String method = pattern.group(2);
        final String keyName = namespace + pattern.group(3);
        final String regex = pattern.group(4);

        return new CellProcessor() {
            @Override
            public String preprocess() {
                String result;

                if (method.equalsIgnoreCase("get")) {
                    if (!vars.containsKey(keyName)) {
                        result = "Unknown variable: " + keyName;
                    } else {
                        result = vars.get(keyName);
                    }
                } else if (method.equalsIgnoreCase("containsValue")) {
                    result = vars.containsKey(keyName) ? "true" : "false";
                } else { // put
                    result = "";
                }

                return strippedText.replaceAll(Pattern.quote(pattern.group(0)), result);
            }

            @Override
            public void postprocess(Object result, TypeHandler handler) {
                if (method.equalsIgnoreCase("put")) {
                    String value = handler.toString(result);
                    if (regex != null) {
                        Matcher matcher = Pattern.compile(regex).matcher(value);
                        value = "";
                        if (matcher.find() && matcher.group(1) != null) {
                            value = matcher.group(1);
                        }
                    }
                    vars.put(keyName, value);
                }
            }
        };
    }
}
