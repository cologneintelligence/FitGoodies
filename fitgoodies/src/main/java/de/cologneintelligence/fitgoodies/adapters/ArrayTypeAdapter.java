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


package de.cologneintelligence.fitgoodies.adapters;

import fit.Fixture;
import fit.TypeAdapter;

import java.lang.reflect.Array;
import java.util.StringTokenizer;

/**
 * If the destination type is an array, this adapter handles it.
 * It splits the input at commas and converts each part into an array element.
 *
 */
public class ArrayTypeAdapter extends TypeAdapter {
    private final String parameter;
    private final TypeAdapterHelper helper;

    /**
     * Creates a new TypeAdapter which can read arrays.
     * In contrast to the fit TypeAdapter, this one is able to process registered
     * Types of array.
     *
     * @param ta old TypeAdapter
     * @param convertParameter column/row parameter
     * @param typeAdapterHelper the helper which manages registered types
     */
    public ArrayTypeAdapter(final TypeAdapter ta, final String convertParameter,
            final TypeAdapterHelper typeAdapterHelper) {
        this.field = ta.field;
        this.fixture = ta.fixture;
        this.method = ta.method;
        this.target = ta.target;
        this.type = ta.type;
        parameter = convertParameter;
        helper = typeAdapterHelper;

        init(ta.target, ta.fixture, ta.type);
    }

    /**
     * {@code TypeAdapter} which handles the array items.
     */
    protected TypeAdapter componentAdapter;

    /**
     * Initializes the internal variables, especially the target, the target's
     * type and a type specific component adapter.
     * @param target target object
     * @param fixture target fixture to parse objects
     * @param type type of the fixture data
     */
    @Override @SuppressWarnings("rawtypes")
    protected void init(Object target, final Fixture fixture, final Class type) {
        super.init(target, fixture, type);

        componentAdapter = on(target, fixture, type.getComponentType());
        componentAdapter = helper.getAdapter(componentAdapter, parameter);
    }

    /**
     * Converts a {@code String} into a {@code Array}.
     * @param s the {@code String} to convert
     * @return an {@code Array} which contains {@code s}
     * @throws Exception if the string could not be parsed
     */
    @Override
    public Object parse(final String s) throws Exception {
        final StringTokenizer t = new StringTokenizer(s, ",");
        final Object array = Array.newInstance(componentAdapter.type, t.countTokens());
        for (int i = 0; t.hasMoreTokens(); i++) {
            Array.set(array, i, componentAdapter.parse(t.nextToken().trim()));
        }
        return array;
    }

    /**
     * Returns the String representation of the array. The result is a comma
     * separated list of all items. The items are converted to Strings using
     * an item-specific type adapter.
     *
     * @param o Array to convert
     * @return {@code o} as a String
     */
    @Override
    public String toString(final Object o) {
        if (o == null) {
            return "";
        }

        final int length = Array.getLength(o);
        final StringBuilder b = new StringBuilder(5 * length);
        for (int i = 0; i < length; i++) {
            b.append(componentAdapter.toString(Array.get(o, i)));
            if (i < length - 1) {
                b.append(", ");
            }
        }
        return b.toString();
    }

    /**
     * Checks whether two Arrays {@code a} and {@code b} are
     * equal.
     *
     * A type specific {@code TypeAdapter} is used to compare the items.
     *
     * @param a first array
     * @param b second array
     * @return {@code true} if all items are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(final Object a, final Object b) {
        final int length = Array.getLength(a);
        if (length != Array.getLength(b)) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!componentAdapter.equals(Array.get(a, i), Array.get(b, i))) {
                return false;
            }
        }
        return true;
    }
}
