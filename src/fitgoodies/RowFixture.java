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


package fitgoodies;

import fit.Parse;
import fit.TypeAdapter;
import fitgoodies.util.FixtureTools;

/**
 * In contrast to <code>fit.RowFixture</code>, this <code>RowFixture</code>
 * enables all fitgoodies features (for example custom type adapters,
 * custom parsers and cross references).
 *
 * @author jwierum
 * @version $Id: RowFixture.java 203 2009-08-24 12:03:16Z jwierum $
 */
public abstract class RowFixture extends fit.RowFixture {
	private String[] parameters;

	/**
	 * Replacement of <code>check</code> which resolves cross-references
	 * before calling the original check method of fit.
	 *
	 *  @param cell the cell to check
     *  @param a - TypeAdapter to use
     *
     *  @see fit.Fixture#check {@link fit.Fixture#check(Parse, TypeAdapter)}
     */
	@Override
	public void check(final Parse cell, final TypeAdapter a) {
		TypeAdapter ta = FixtureTools.processCell(cell, a, this);
		if (ta != null) {
			super.check(cell, a);
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
		Object result = FixtureTools.parse(text, type, null);
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
            	if (i < parameters.length) {
            		parameter = parameters[i];
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
	 * Replacement of <code>bindField(String)</code>, which calls
	 * <code>fit.RowFixture.bindField(String)</code> and rebinds the
	 * returned <code>TypeAdapter</code> to a custom registered,
	 * more specific one, if possible.
	 *
	 * @param name field name to bind
	 * @return TypeAdapter which is bound to the field
     * @throws Exception stops the fixture if the field
     *  		does not exist or is not accessible
     * @see fit.RowFixture#bindField(String)
     *  	{@link fit.RowFixture#bindField(String)}
	 */
	private TypeAdapter bindField(final String name, final String parameter)
			throws Exception {
		TypeAdapter a = super.bindField(name);
		a = FixtureTools.rebindTypeAdapter(a, parameter);
		return a;
	}

	/**
	 * Replacement of <code>bindMethod(String)</code>, which calls
	 * <code>fit.RowFixture.bindMethod(String)</code> and
	 * rebinds the returned <code>TypeAdapter</code> to a custom registered,
	 * more specific one, if possible.
	 *
	 *  @param name method name to bind
     *  @return TypeAdapter which is bound to the method
     *  @throws Exception stops the fixture if the method
     *  		does not exist or is not accessible
     *  @see fit.RowFixture#bindMethod(String)
     *  	{@link fit.RowFixture#bindMethod(String)}
	 */
	private TypeAdapter bindMethod(final String name, final String parameter) throws Exception {
		TypeAdapter a = super.bindMethod(name);
		a = FixtureTools.rebindTypeAdapter(a, parameter);
		return a;
	}

	/**
	 * Sets the fixture parameters.
	 *
	 * Normally, these values are generated by reading the first
	 * line of the table. This method is primary useful for debugging.
	 * You won't need it otherwise.
	 *
	 * @param args parameters to store in <code>args</code>
	 */
	public final void setParams(final String[] args) {
		this.args = args;
	}

	/**
	 * Initializes the fixture arguments, call <code>setUp</code>,
	 * <code>fit.RowFixture.doTable(Parse)</code> and <code>tearDown()</code>.
	 *
     * @param table the table to be processed
	 * @see fit.RowFixture#doTable(Parse) {@link fit.RowFixture#doTable(Parse)}
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
     * Looks up a given parameter in the fixture's argument list.
     *
     * @param paramName the parameter name to look up
     * @return  the parameter value, if it could be found, <code>null</code> otherwise
     * @see #getParam(String, String) {@link #getParam(String, String)}
     * @see FixtureTools#getArg(String[], String, String)
     * 		{@link FixtureTools#getArg(String[], String, String)}
     */
    public final String getParam(final String paramName) {
		return getParam(paramName, null);
	}

	/**
	 * Looks up a given parameter in the fixture's argument list.
	 *
	 * If the value does not exist, the given default value is returned.
     * @param paramName paramName the parameter name to look up
     * @param defaultValue defaultValue the value to be returned if the parameter is missing
     * @return the parameter value, if it could be found, <code>defaultValue</code> otherwise
	 */
	public final String getParam(final String paramName, final String defaultValue) {
		return FixtureTools.getArg(args, paramName, defaultValue);
	}

	@Override
	public void doRows(final Parse rows) {
		parameters = FixtureTools.extractColumnParameters(rows);
		FixtureTools.resolveQuestionMarks(rows);
		super.doRows(rows);
	}


}
