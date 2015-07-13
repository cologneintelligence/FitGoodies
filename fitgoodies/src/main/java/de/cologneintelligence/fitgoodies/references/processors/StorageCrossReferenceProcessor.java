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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This processor allows to store and receive string values. To keep things clean,
 * the processor uses namespaces.
 * <p>
 *
 * Valid examples and return values:<p><ul>
 * <li>${testnamespace.put(var1)} with object &quot;x&quot;: x</li>
 * <li>${testnamespace.put(var2)} with object &quot;y&quot;: y</li>
 * <li>${ns2.put(var2)} with object &quot;x&quot;: x</li>
 * <li>${ns2.get(var2)} with object &quot;y&quot;: x</li>
 * <li>${ns2.containsValue(var1)} with object &quot;y&quot;: ns2.var1: no value found!</li>
 * <li>${testnamespace.containsValue(var1)} with object &quot;y&quot;: y</li>
 * <li>${ns.get(var3, /Password: ([^\s]+)/}
 * </ul>
 *
 */

public class StorageCrossReferenceProcessor extends AbstractCrossReferenceProcessor {
    private static final String PATTERN =
            //"([^.()]+)\\.(get|put|containsValue)\\(([^)]+)\\)";
            "([^.()]+)\\.(get|put|containsValue)\\(([^,)]+\\s*(?:,\\s*/(?:[^/]|\\\\/)+(?<!\\\\)/\\s*)?)\\)";


    private final NamespaceHashMap<String> variablesMap =
            new NamespaceHashMap<>();

    /**
     * Default constructor.
     */
    public StorageCrossReferenceProcessor() {
        super(PATTERN);
    }

    /**
     * Processes a match. The namespace represents the namespace, the parameter
     * represents the variable's name.
     * <p>
     *
     * The return value depends on what command is used. If it is &quot;get&quot;,
     * the loaded value or an error is returned. If it is &quot;put&quot;,
     * {@code object} is returned, and if it is &quot;containsValue&quot;,
     * either the object or an error is returned.
     *
     * @param cr the extracted match
     * @param object the object to save
     * @return {@code object}, an error message or the loaded value, depending
     * 		on {@code cr.getcommand()} (see method description above).
     */
    @Override
    public final String processMatch(final CrossReference cr, final Object object) {
        String result = null;
        switch (cr.getCommand()) {
            case "get":
                result = getValue(cr);
                break;
            case "put":
                result = putValue(cr, object);
                break;
            case "containsValue":
                result = variablesMap.get(cr.getNamespace(), cr.getParameter());
                if (result == null) {
                    result = cr.getNamespace() + "." + cr.getParameter()
                            + ": no value found!";
                } else {
                    result = object.toString();
                }   break;
        }

        return result;
    }

    private String putValue(
            final CrossReference cr, final Object object) {
        String result;
        if (!cr.getParameter().contains(",")) {
            result = getSimpleValue(cr, object);
        } else {
            result = getRegexValue(cr, object);
        }
        return result;
    }

    private String getRegexValue(final CrossReference cr, final Object object) {
        String args[] = cr.getParameter().split("\\s*,\\s*");
        String varName = args[0];
        String regex = args[1].substring(1, args[1].length() - 1).replace("\\/", "/");
        Matcher matcher = Pattern.compile(regex).matcher(object.toString());

        if (matcher.find()) {
            String result = matcher.group(1);
            variablesMap.put(cr.getNamespace(), varName, result);
            return object.toString();
        } else {
            return "/" + regex + "/: illegal regex";
        }
    }

    private String getSimpleValue(final CrossReference cr, final Object object) {
        String result;
        variablesMap.put(cr.getNamespace(), cr.getParameter(), object.toString());
        result = object.toString();
        return result;
    }

    private String getValue(final CrossReference cr) {
        String result;
        result = variablesMap.get(cr.getNamespace(), cr.getParameter());

        if (result == null) {
            result = cr.getNamespace() + "." + cr.getParameter()
                    + ": cross reference could not be resolved!";
        }
        return result;
    }

    /**
     * A user friendly description.
     * @return a description.
     */
    @Override
    public final String info() {
        return "provides get(), set() and containsValue()";
    }
}
