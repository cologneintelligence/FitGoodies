/*
 * Copyright (c) 2002 Cunningham & Cunningham, Inc.
 * Copyright (c) 2009-2015 by Jochen Wierum & Cologne Intelligence
 *
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

import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.htmlparser.FitRow;
import de.cologneintelligence.fitgoodies.htmlparser.FitTable;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandlerFactory;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiverFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Fixture {
	protected Map<String, String> args;

	protected TypeHandlerFactory typeHandlerFactory = DependencyManager.getOrCreate(TypeHandlerFactory.class);
	protected ValueReceiverFactory valueReceiverFactory = DependencyManager.getOrCreate(ValueReceiverFactory.class);
	protected Validator validator = DependencyManager.getOrCreate(Validator.class);

	/**
	 * Sets the fixture parameters.
	 * <p/>
	 * Normally, these values are generated by reading the first line of the
	 * table. This method is primary useful for debugging. You won't need it
	 * otherwise.
	 *
     * @param args parameters to store in {@code args}
     */
	public final void setParams(final Map<String, String> args) {
		this.args = args;
	}


	/**
	 * Looks up a given parameter in the fixture's argument list.
	 *
	 * @param paramName the parameter name to look up
	 * @return the parameter value, if it could be found, {@code null} otherwise
	 * @see #getArg(String, String) {@link #getArg(String, String)}
	 * @see #getArg(String, String)
	 * {@link #getArg(String, String)}
	 */
	public final String getArg(final String paramName) {
		return getArg(paramName, null);
	}

	/**
	 * Finds an argument in an given argument list.
	 * <p/>
	 * The search for an argument is case-insensitive and whitespaces at the
	 * beginning and the end are ignored. The argument's name and its value are
	 * separated by an equal sign. All these inputs will result in
	 * &quot;world&quot;, if you look up &quot;hello&quot;:
	 * <p>
	 * &quot;hello=world&quot;, &quot; hello = world &quot;,
	 * &quot;HeLLo = world&quot;.</p>
	 * <p/>
	 * Note: the case of the value is unchanged.
	 * <p/>
	 *
	 * @param argName      the argument name to look up
	 * @param defaultValue the result value if the argument does not exist
	 * @return the argument's value without namespaces, or defaultValue
	 * @see #getArgNames() getArgs
	 * @see #copyParamsToFixture() copyParamsToFixture
	 */
	public String getArg(final String argName, final String defaultValue) {
		if (args == null || !args.containsKey(argName)) {
			return defaultValue;
		} else {
            return validator.preProcess(args.get(argName));
        }
	}


	// Traversal //////////////////////////

	/**
	 * Initializes the fixture arguments, call {@code setUp},
	 * {@code fit.Fixture.doTable(Parse)} and {@code tearDown()}.
	 *
	 * @param table the table to be processed
	 */
	public void doTable(FitTable table) {
		copyParamsToFixture();

		try {
			setUp();

			try {
                doRows(table.rows());
			} catch (Exception e) {
                table.exception(e);
			}

			tearDown();
		} catch (final Exception e) {
            table.exception(e);
		}
        table.finishExecution();
	}

	/**
	 * extracts and removes parameters from a row.
	 *
	 * @param row row to process
	 * @return extracted parameters
	 */
	protected String[] extractColumnParameters(FitRow row) {
        final List<String> result = new ArrayList<>();

        for (FitCell cell : row.cells()) {
			result.add(FitUtils.extractCellParameter(cell));
		}

		return result.toArray(new String[result.size()]);
	}

	protected void doRows(List<FitRow> rows) throws Exception {
        for (FitRow row : rows) {
			doRow(row);
		}
	}

	protected void doRow(FitRow row) throws Exception {
		doCells(row.cells());
	}

	protected void doCells(List<FitCell> cells) {
        for (int i = 0; i < cells.size(); i++) {
            FitCell cell = cells.get(i);

            try {
				doCell(cell, i);
			} catch (Exception e) {
                cell.exception(e);
			}
		}
	}

	protected void doCell(FitCell cell, int columnNumber) throws Exception {
		cell.ignore();
	}

	/**
	 * Does nothing. Override it to initialize the fixture.
	 * The method is called before doTables.
	 *
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

	// Parameters ///////////////////////////////

	/**
	 * Reads the argument list and copies all values in public members
	 * with the same name.
	 * <p/>
	 * If these members do not exist, the argument is skipped. You can still
	 * read the values using {@link #getArg(String, String)}.
	 *
	 * @see #getArg(String, String) getArg
	 */
	public void copyParamsToFixture() {
		for (final String fieldName : getArgNames()) {
			ValueReceiver valueReceiver;
			try {
				valueReceiver = valueReceiverFactory.createReceiver(this, fieldName);
			} catch (NoSuchMethodException | NoSuchFieldException e) {
				continue;
			}

			TypeHandler handler = createTypeHandler(valueReceiver, null);

			String fieldValueString = getArg(fieldName, null);
			try {
				Object fieldValue = handler.parse(fieldValueString);
				valueReceiver.set(this, fieldValue);
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * Returns all argument names.
	 *
	 * @return a list of all given argument names.
	 * @see #getArg(String, String) getArg
	 * @see #copyParamsToFixture()  copyParamsToFixture
	 */
	protected String[] getArgNames() {
		if (args == null) {
			return new String[]{};
		}

        return args.keySet().toArray(new String[args.keySet().size()]);
	}


	// Utility //////////////////////////////////

	/**
	 * Replacement of {@code check} which resolves cross-references
	 * before calling the original check method of fit.
	 *
	 * @param cell                 the cell to check
	 * @param valueReceiver        - TypeAdapter to use
	 */
	public void check(final FitCell cell, ValueReceiver valueReceiver, String currentCellParameter) {
		validator.process(cell, valueReceiver, currentCellParameter, typeHandlerFactory);
	}

	protected TypeHandler createTypeHandler(ValueReceiver valueReceiver, String cellParameter) {
		return typeHandlerFactory.getHandler(valueReceiver.getType(), cellParameter);
	}

	protected ValueReceiver createReceiver(Object target, String name) throws NoSuchMethodException, NoSuchFieldException {
		return valueReceiverFactory.createReceiver(target, name);
	}

	protected ValueReceiver createReceiver(Object target, Method method) throws NoSuchMethodException, NoSuchFieldException {
		return valueReceiverFactory.createReceiver(target, method);
	}
}
