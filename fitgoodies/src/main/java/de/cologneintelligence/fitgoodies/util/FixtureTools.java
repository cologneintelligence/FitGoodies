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


package de.cologneintelligence.fitgoodies.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.cologneintelligence.fitgoodies.adapters.CachingTypeAdapter;
import de.cologneintelligence.fitgoodies.adapters.TypeAdapterHelper;
import de.cologneintelligence.fitgoodies.parsers.ParserHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import fit.Fixture;
import fit.Parse;
import fit.TypeAdapter;

/**
 * Helper class with static methods which provides functions for all fixtures.
 *
 * @author jwierum
 */
public final class FixtureTools {
    private static Pattern parameterPattern = Pattern.compile("^(.*)\\s*\\[\\s*(.*?)\\s*\\]\\s*$");

    private FixtureTools() {
    }

    /**
     * Test whether a <code>String</code> has a boolean value.
     * &quot;true&quot;, &quot;yes&quot; and &quot;1&quot; are recognized as
     * <code>true</code>, &quot;false&quot;, &quot;no&quot; and &quot;0&quot;
     * are recognized as <code>false</code>.<br />
     *
     * Values other than these six lead to an IllegalArgumentException.
     *
     * @param text text to parse
     * @return the represented value
     * @throws IllegalArgumentException if the <code>String</code>
     * 		can not be parsed.
     */
    public static boolean convertToBoolean(final String text) {
        if (text.equalsIgnoreCase("true")
                || text.equalsIgnoreCase("yes")
                || text.equalsIgnoreCase("1")) {
            return true;
        } else if (text.equalsIgnoreCase("false")
                || text.equalsIgnoreCase("no")
                || text.equalsIgnoreCase("0")) {
            return false;
        } else {
            throw new IllegalArgumentException("should be true or false");
        }
    }

    /**
     * Resolves cross references and decides, whether more processing
     * (a call to <code>check</code>) is necessary.
     * @param cell the cell to parse
     * @param ta the type bound adapter which is used to compare the cell content
     * @param parent the parent fixture
     * @param crossReferenceHelper
     * @return a cached <code>TypeAdapter</code>, whether the cell should be
     * 		<code>check</code>'ed, <code>null</code> if no more processing is
     * 		required (the cell is then marked as right or wrong from
     * 		<code>processCell</code>)
     */
    public static TypeAdapter processCell(final Parse cell,
            final TypeAdapter ta, final Fixture parent,
            final CrossReferenceHelper crossReferenceHelper) {

        String actualStringValue = "";
        boolean callParentCheck = true;

        final TypeAdapter adapter = new CachingTypeAdapter(ta);

        Object obj;
        try {
            obj = adapter.get();
            if (obj != null) {
                actualStringValue = adapter.toString(obj);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        try {
            cell.body = crossReferenceHelper.parseBody(cell.body, actualStringValue);
        } catch (final CrossReferenceProcessorShortcutException e) {
            setShortCutMessage(cell, parent, actualStringValue, obj, e);
            callParentCheck = false;
        }
        if (callParentCheck) {
            return adapter;
        } else {
            return null;
        }
    }

    private static void setShortCutMessage(final Parse cell,
            final Fixture parent, final String actualStringValue, final Object obj,
            final CrossReferenceProcessorShortcutException e) {
        if (e.isOk()) {
            if (obj == null) {
                cell.body = "(null)";
            } else {
                cell.body = actualStringValue;
            }

            cell.body += " <font color=\"#808080\">"
                    + Fixture.escape(e.getMessage()) + "</font>";
            parent.right(cell);
        } else {
            cell.body = e.getMessage();

            if (obj == null) {
                parent.wrong(cell, "(null)");
            } else {
                parent.wrong(cell, actualStringValue);
            }
        }
    }

    /**
     * Tries to parse a <code>text</code> into an object of the type
     * <code>type</code> by looking up a registered parser. If no parser
     * could be found, <code>null</code> is returned.
     * @param text the text to be parsed
     * @param type the destination type of the return value
     * @param parameter provided row/column parameter
     * @return an Object of the type <code>type</code> which represents
     * 	<code>text</code> or null, if no parser was found.
     * @throws Exception should be propagated to fit.
     */
    public static Object parse(final String text, final Class<?> type,
            final String parameter, final ParserHelper parserHelper)
                    throws Exception {

        return parserHelper.parse(text, type, parameter);
    }

    /**
     * Replaces the given <code>TypeAdapter</code> with a more specific one.
     * The type of the new adapter is determined by the type of
     * <code>ta.target</code>.
     * <br /><br />
     * If no suitable TypeAdapter could be found, the old one is returned.
     * @param ta the <code>TypeAdapter</code> to replace.
     * @param parameter column/row parameter
     * @param taHelper
     * @return a TypeAdapter
     */
    public static TypeAdapter rebindTypeAdapter(final TypeAdapter ta,
            final String parameter, final TypeAdapterHelper taHelper) {
        return taHelper.getAdapter(ta, parameter);
    }

    /**
     * Finds an argument in an given argument list.
     *
     * The search for an argument is case-insensitive and whitespaces at the
     * beginning and the end are ignored. The argument's name and its value are
     * separated by an equal sign. All these inputs will result in
     * &quot;world&quot;, if you look up &quot;hello&quot;:<br />
     * &quot;hello=world&quot;, &quot; hello = world &quot;,
     * &quot;HeLLo = world&quot;.<br /><br />
     * Note: the case of the value is unchanged.
     *
     * @param args the argument list (normally, this is fixture.args)
     * @param argName the argument name to look up
     * @param defaultValue the result value if the argument does not exist
     * @return the argument's value without namespaces, or defaultValue
     *
     * @see #getArgs(String[]) getArgs
     * @see #copyParamsToFixture(String[], Fixture) copyParamsToFixture
     */
    public static String getArg(final String[] args, final String argName,
            final String defaultValue, final CrossReferenceHelper crossReferenceHelper) {
        if (args == null) {
            return defaultValue;
        }

        for (final String argument : args) {
            final String[] pair = argument.split("=", 2);
            if (pair.length == 2) {
                if (pair[0].trim().equalsIgnoreCase(argName)) {
                    try {
                        return crossReferenceHelper.parseBody(pair[1].trim(), "");
                    } catch (final CrossReferenceProcessorShortcutException e) {
                        return "";
                    }
                }
            }
        }

        return defaultValue;
    }

    /**
     * Replaces question marks at the end of a cell with "()" in the first
     * row of a table.
     *
     * @param rows the table to process
     */
    public static void resolveQuestionMarks(final Parse rows) {
        Parse cell = rows.parts;
        while (cell != null) {
            if (cell.body.endsWith("?")) {
                cell.body = cell.body.substring(0, cell.body.length() - 1) + "()";
            }
            cell = cell.more;
        }
    }

    /**
     * Returns all argument names.
     *
     * @param args argument list to process (this is usually fixture.args)
     * @return a list of all given argument names.
     *
     * @see #getArg(String[], String, String) getArg
     * @see #copyParamsToFixture(String[], Fixture) copyParamsToFixture
     */
    public static String[] getArgs(final String[] args) {
        final List<String> result = new ArrayList<String>();

        if (args == null) {
            return new String[]{};
        }

        for (final String argument : args) {
            final String[] pair = argument.split("=", 2);
            if (pair.length == 2) {
                result.add(pair[0].trim());
            }
        }

        return result.toArray(new String[]{});
    }

    /**
     * Reads the argument list and copies all values in public members
     * with the same name.
     *
     * If these members do not exist, the argument is skipped. You can still
     * read the values using {@link FixtureTools#getArg(String[], String, String)}.
     *
     * @param args argument list to process
     * @param fixture fixture to copy the values to
     * @param crossReferenceHelper
     * @param taHelper
     *
     * @see #getArg(String[], String, String) getArg
     * @see #getArgs(String[]) getArgs
     */
    public static void copyParamsToFixture(final String[] args, final Fixture fixture,
            final CrossReferenceHelper crossReferenceHelper,
            final TypeAdapterHelper taHelper) {
        for (final String fieldName : getArgs(args)) {
            try {
                final Field field = fixture.getClass().getField(fieldName);
                final TypeAdapter ta = rebindTypeAdapter(TypeAdapter.on(fixture, field), null,
                        taHelper);

                final String fieldValueString = getArg(args, fieldName, null, crossReferenceHelper);
                final Object fieldValue = ta.parse(fieldValueString);
                ta.set(fieldValue);
            } catch (final Exception e) {
            }
        }
    }

    /**
     * extracts and removes parameters from a row.
     * @param row row to process
     * @return extracted parameters
     */
    public static String[] extractColumnParameters(final Parse row) {
        Parse cell = row.parts;
        final List<String> result = new ArrayList<String>();

        while (cell != null) {
            result.add(extractCellParameter(cell));
            cell = cell.more;
        }

        return result.toArray(new String[]{});
    }

    /**
     * extracts and removes parameters from a cell.
     * @param cell cell to process
     * @return the extracted parameter or <code>null</code>
     */
    public static String extractCellParameter(final Parse cell) {
        final Matcher matcher = parameterPattern.matcher(cell.text());
        if (matcher.matches()) {
            cell.body = matcher.group(1);
            return matcher.group(2);
        } else {
            return null;
        }
    }
}
