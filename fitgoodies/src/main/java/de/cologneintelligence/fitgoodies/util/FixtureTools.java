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

import de.cologneintelligence.fitgoodies.adapters.CachingTypeAdapter;
import de.cologneintelligence.fitgoodies.adapters.TypeAdapterHelper;
import de.cologneintelligence.fitgoodies.parsers.ParserHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import fit.Fixture;
import fit.Parse;
import fit.TypeAdapter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class with static methods which provides functions for all fixtures.
 *
 */
public final class FixtureTools {
    private static Pattern parameterPattern = Pattern.compile("^(.*)\\s*\\[\\s*(.*?)\\s*\\]\\s*$");

    private FixtureTools() {
    }

    /**
     * Test whether a {@code String} has a boolean value.
     * &quot;true&quot;, &quot;yes&quot; and &quot;1&quot; are recognized as
     * {@code true}, &quot;false&quot;, &quot;no&quot; and &quot;0&quot;
     * are recognized as {@code false}.<p>
     *
     * Values other than these six lead to an IllegalArgumentException.
     *
     * @param text text to parse
     * @return the represented value
     * @throws IllegalArgumentException if the {@code String}
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
     * (a call to {@code check}) is necessary.
     * @param cell the cell to parse
     * @param ta the type bound adapter which is used to compare the cell content
     * @param parent the parent fixture
     * @param crossReferenceHelper CrossReferenceHelper to resolve cross references
     * @return a cached {@code TypeAdapter}, whether the cell should be
     * 		{@code check}'ed, {@code null} if no more processing is
     * 		required (the cell is then marked as right or wrong from
     * 		{@code processCell})
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
     * Tries to parse a {@code text} into an object of the type
     * {@code type} by looking up a registered parser. If no parser
     * could be found, {@code null} is returned.
     * @param text the text to be parsed
     * @param type the destination type of the return value
     * @param parameter provided row/column parameter
     * @param parserHelper ParserHelper to parse text
     * @return an Object of the type {@code type} which represents
     * 	{@code text} or null, if no parser was found.
     * @throws Exception should be propagated to fit.
     */
    public static Object parse(final String text, final Class<?> type,
            final String parameter, final ParserHelper parserHelper)
                    throws Exception {

        return parserHelper.parse(text, type, parameter);
    }

    /**
     * Replaces the given {@code TypeAdapter} with a more specific one.
     * The type of the new adapter is determined by the type of
     * {@code ta.target}.
     * <p>
     * If no suitable TypeAdapter could be found, the old one is returned.
     * <p>
     * @param ta the {@code TypeAdapter} to replace.
     * @param parameter column/row parameter
     * @param taHelper TypeAdapterHelper to resolve TypeAdapters
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
     * &quot;world&quot;, if you look up &quot;hello&quot;:
     * <p>
     * &quot;hello=world&quot;, &quot; hello = world &quot;,
     * &quot;HeLLo = world&quot;.</p>
     * <p>
     * Note: the case of the value is unchanged.
     * <p>
     *
     * @param args the argument list (normally, this is fixture.args)
     * @param argName the argument name to look up
     * @param defaultValue the result value if the argument does not exist
     * @param crossReferenceHelper crossReferenceHelper to resolve cross references
     * @return the argument's value without namespaces, or defaultValue
     *
     * @see #getArgs(String[]) getArgs
     * @see #copyParamsToFixture(String[], Fixture, CrossReferenceHelper, TypeAdapterHelper) copyParamsToFixture
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
     * @see #getArg(String[], String, String, CrossReferenceHelper) getArg
     * @see #copyParamsToFixture(String[], Fixture, CrossReferenceHelper, TypeAdapterHelper) copyParamsToFixture
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

        return result.toArray(new String[result.size()]);
    }

    /**
     * Reads the argument list and copies all values in public members
     * with the same name.
     *
     * If these members do not exist, the argument is skipped. You can still
     * read the values using {@link FixtureTools#getArg(String[], String, String, CrossReferenceHelper)}.
     *
     * @param args argument list to process
     * @param fixture fixture to copy the values to
     * @param crossReferenceHelper the CrossReferenceHelper to resolve cross references
     * @param taHelper TypeAdapterHelper to resolve types
     *
     * @see #getArg(String[], String, String, CrossReferenceHelper) getArg
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
            } catch (final Exception ignored) {
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

        return result.toArray(new String[result.size()]);
    }

    /**
     * extracts and removes parameters from a cell.
     * @param cell cell to process
     * @return the extracted parameter or {@code null}
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

    public static String htmlSafeFile(File file) {
        return htmlSafeFile(file.getPath());
    }

    public static String htmlSafeFile(String file) {
        return file.replace(File.separatorChar, '/');
    }
}
