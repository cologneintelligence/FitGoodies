/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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


package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.util.FixtureTools;
import fit.Parse;
import fit.TypeAdapter;

/**
 * In contrast to <code>fit.ColumnFixture</code>, this <code>ColumnFixture</code>
 * enables all fitgoodies features (for example custom type adapters,
 * custom parsers and cross references).
 *
 * @author jwierum
 * @version $Id$
 */
public class ColumnFixture extends fit.ColumnFixture {
	private String[] columnParameters;
	private String columnParameter;

	/**
	 * Replacement of <code>doCell</code> which resolves cross-references
	 * before calling the original <code>doCell</code> method of fit.
	 *
	 *  @param cell the cell to check
     *  @param column the selected column
     *
     *  @see fit.Fixture#doCell(Parse, int) fit.Fixture.doCell(Parse, int)
     */
    @Override
	public void doCell(final Parse cell, final int column) {
        TypeAdapter a = columnBindings[column];

        columnParameter = null;
        if (column < columnParameters.length) {
        	columnParameter = columnParameters[column];
        }

        if (a == null) {
            ignore(cell);
        } else {
	        a = FixtureTools.processCell(cell, a, this);
	        if (a != null) {
		        try {
		            String text = cell.text();
		            if (text.equals("")) {
		                check(cell, a);
		            } else if (a.field != null) {
		                a.set(a.parse(text));
		            } else if (a.method != null) {
		                check(cell, a);
		            }
		        } catch (Exception e) {
		            exception(cell, e);
		        }
	        }
        }
    }

	/**
	 * Replacement of <code>parse</code> which uses the extended parse features of
	 * fitgoodies and uses fit's parse as a fallback.
	 *
	 *  @param text text to parse
     *  @param type type to transform text to
     *
     *  @return Object of type <code>type</code> which represents <code>text</code>
     *  @throws Exception if the value can't be parsed
     *
     *  @see fit.Fixture#parse(String, Class) {@link fit.Fixture#parse(String, Class)}
	 */
	@Override @SuppressWarnings("unchecked")
	public Object parse(final String text, final Class type) throws Exception {
		Object result = FixtureTools.parse(text, type, columnParameter);

		if (result == null) {
			return super.parse(text, type);
		} else {
			return result;
		}
	}

	@Override
    protected void bind(final Parse heads) {
		Parse head = heads;
        columnBindings = new TypeAdapter[head.size()];
        for (int i = 0; head != null; i++, head = head.more) {
            String name = head.text();
            String suffix = "()";
            try {
            	String parameter = null;
            	if (i < columnParameters.length) {
            		parameter = columnParameters[i];
            	}

                if (name.equals("")) {
                    columnBindings[i] = null;
                } else if (name.endsWith(suffix)) {
                    columnBindings[i] = bindMethod(name.substring(0,
                    		name.length() - suffix.length()), parameter);
                } else {
                    columnBindings[i] = bindField(name, parameter);
                }
            } catch (Exception e) {
                exception(head, e);
            }
        }
    }

	/**
	 * Replacement of <code>bindMethod(String)</code>, which calls
	 * <code>fit.ColumnFixture.bindMethod(String)</code> and
	 * rebinds the returned <code>TypeAdapter</code> to a custom registered,
	 * more specific one, if possible.
	 *
	 *  @param name method name to bind
     *  @return TypeAdapter which is bound to the method
     *  @throws Exception stops the fixture if the method
     *  		does not exist or is not accessible
     *  @see fit.ColumnFixture#bindMethod(String)
     *  	{@link fit.ColumnFixture#bindMethod(String)}
	 */
	private TypeAdapter bindMethod(final String name, final String parameter)
			throws Exception {
		TypeAdapter ta = super.bindMethod(name);
		ta = FixtureTools.rebindTypeAdapter(ta, parameter);
		return ta;
	}

	/**
	 * Replacement of <code>bindField(String)</code>, which calls
	 * <code>fit.ColumnFixture.bindField(String)</code> and rebinds the
	 * returned <code>TypeAdapter</code> to a custom registered,
	 * more specific one, if possible.
	 *
	 * @param name field name to bind
	 * @return TypeAdapter which is bound to the field
     * @throws Exception stops the fixture if the field
     *  		does not exist or is not accessible
     * @see fit.ColumnFixture#bindField(String)
     *  	{@link fit.ColumnFixture#bindField(String)}
	 */
	private TypeAdapter bindField(final String name, final String parameter)
			throws Exception {
		TypeAdapter ta = super.bindField(name);
		ta = FixtureTools.rebindTypeAdapter(ta, parameter);
		return ta;
	}

	/**
	 * Replacement of <code>doRows(Parse)</code> which resolves question marks
	 * in the first row and calls fit.ColumnFixture.doRows(Parse).
	 *
	 * Question marks represent method calls,
	 * so getValue() and getValue? are equivalent.
	 *
	 * @param rows rows to be processed
	 */

	@Override
	public void doRows(final Parse rows) {
		columnParameters = FixtureTools.extractColumnParameters(rows);
		FixtureTools.resolveQuestionMarks(rows);
		super.doRows(rows);
	}

	/**
	 * Sets the fixture columnParameters.
	 *
	 * Normally, these values are generated by reading the first
	 * line of the table. This method is primary useful for debugging.
	 * You won't need it otherwise.
	 *
	 * @param parameters columnParameters to store in <code>args</code>
	 */
	public final void setParams(final String[] parameters) {
		this.args = parameters;
	}

	/**
	 * Initializes the fixture arguments, call <code>setUp</code>,
	 * <code>fit.ActionFixture.doTable(Parse)</code> and <code>tearDown()</code>.
	 *
     * @param table the table to be processed
	 * @see fit.Fixture#doTable(Parse) {@link fit.Fixture#doTable(Parse)}
	 */
    @Override
    public void doTable(final Parse table) {
    	FixtureTools.copyParamsToFixture(args, this);

    	try {
    		setUp();

            try {
                super.doTable(table);
            } catch (Exception e) {
                exception(table.parts.parts, e);
            }

            tearDown();
    	} catch (Exception e) {
            exception(table.parts.parts, e);
        }
    }

    /**
     * Does nothing. Override it to initialize the fixture.
     * The method is called before doTables.
     * @throws Exception any kind of exception aborts the execution of this fixture
     */
    public void setUp() throws Exception {
    }

    /**
     * Does nothing. Override it to tear down the fixture.
     * The method is called after doTables.
     *
     * @throws Exception any kind of exception aborts the execution of this fixture
     */
    public void tearDown() throws Exception {
    }

    /**
     * Looks up a given columnParameter in the fixture's argument list.
     *
     * @param paramName the columnParameter name to look up
     * @return  the columnParameter value, if it could be found, <code>null</code> otherwise
     * @see #getParam(String, String) {@link #getParam(String, String)}
     * @see FixtureTools#getArg(String[], String, String)
     * 		{@link FixtureTools#getArg(String[], String, String)}
     */
	public final String getParam(final String paramName) {
		return getParam(paramName, null);
	}

	/**
	 * Looks up a given columnParameter in the fixture's argument list.
	 *
	 * If the value does not exist, the given default value is returned.
     * @param paramName paramName the columnParameter name to look up
     * @param defaultValue defaultValue the value to be returned if the columnParameter is missing
     * @return the columnParameter value, if it could be found, <code>defaultValue</code> otherwise
	 */
	public final String getParam(final String paramName, final String defaultValue) {
		return FixtureTools.getArg(args, paramName, defaultValue);
	}
}
