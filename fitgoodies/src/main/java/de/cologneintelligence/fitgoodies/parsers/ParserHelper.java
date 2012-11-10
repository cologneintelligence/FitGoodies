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


package de.cologneintelligence.fitgoodies.parsers;

import java.util.HashMap;


/**
 * Singleton class which manages registered parsers.
 *
 * @see SetupFixture SetupFixture
 * @author jwierum
 */
public final class ParserHelper {
    private final HashMap<Class<?>, Parser<?>> parsers =
            new HashMap<Class<?>, Parser<?>>();

    public ParserHelper() {
        registerParser(new BigIntegerParser());
        registerParser(new BigDecimalParser());
        registerParser(new ObjectParser());
        registerParser(new ScientificDoubleParser());
    }
    /**
     * Registers a parser.
     * @param <T> destination type which the parser processes
     * @param type destination type which the parser processes
     * @param parser parser which converts <code>String</code> to
     * 		<code>&lt;T&gt;</code>
     */
    public <T> void registerParser(final Class<T> type, final Parser<T> parser) {
        parsers.put(type, parser);
    }

    /**
     * Parses <code>s</code> into an instance of a class of the type
     * <code>type</code>. If no suitable parser could be found,
     * <code>null</code> is returned.
     * @param s the string to parse
     * @param type the destination type
     * @param parameter <code>String</code> which holds the column parameter
     * @return parsed <code>s</code> as class of the type <code>type</code> or null
     * @throws Exception exception which is thrown by a parser. Propagated to fit.
     */
    public Object parse(final String s, final Class<?> type, final String parameter)
            throws Exception {
        Parser<?> p = parsers.get(type);

        if (p != null) {
            return p.parse(s, parameter);
        } else {
            return null;
        }
    }

    /**
     * Registers a parser. The type is automatically determined by asking
     * 		<code>parser</code> for its type.
     * @param <T> destination type which the parser processes
     * @param parser parser parser which converts <code>String</code> to
     * 		<code>&lt;T&gt;</code>
     */
    public <T> void registerParser(final Parser<T> parser) {
        registerParser(parser.getType(), parser);
    }
}
