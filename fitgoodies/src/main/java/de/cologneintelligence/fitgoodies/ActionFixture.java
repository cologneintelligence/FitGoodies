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
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import de.cologneintelligence.fitgoodies.util.WaitForResult;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;

public class ActionFixture extends Fixture {

	public static final long DEFAULT_SLEEP_TIME = 100;

	protected final WaitForResult waitForResult;

	protected FitRow row;
	protected Object actor;

	private String currentCellParameter;

	public ActionFixture(WaitForResult waitForResult) {
		this.waitForResult = waitForResult;
		this.actor = this;
	}

	public ActionFixture() {
		this(new WaitForResult());
	}

	// Traversal ////////////////////////////////

    @Override
    protected void doRow(FitRow row) throws Exception {
        currentCellParameter = FitUtils.extractCellParameter(row.cells().get(0));
        this.row = row;
        super.doRow(row);
    }

    protected void doCells(List<FitCell> cells) {
		try {
			Method action = getClass().getMethod(cells.get(0).getFitValue());
			action.invoke(this);
		} catch (Exception e) {
			cells.get(0).exception(e);
		}
	}

	// Actions //////////////////////////////////

	public void start() throws Exception {
		actor = Class.forName(row.cells().get(1).getFitValue()).newInstance();
	}

	public void enter() throws Exception {
		Method method = method(1);
		String cellText = row.cells().get(2).getFitValue();
		method.invoke(actor, parse(method.getParameterTypes()[0], cellText));
	}

	private <T> T parse(Class<?> type, String cellText) throws java.text.ParseException {
		String preprocessedText = validator.preProcess(cellText);
		TypeHandler th = getTypeHandler(type, currentCellParameter);

		@SuppressWarnings("unchecked")
		T result = (T) th.parse(preprocessedText);

		return result;
	}


	public void press() throws Exception {
		method(0).invoke(actor);
	}

	public void check() throws Exception {
		ValueReceiver receiver = createReceiver(actor, method(0));
		check(row.cells().get(2), receiver, null);
	}

	// Utility //////////////////////////////////

	protected Method method(int args) throws NoSuchMethodException {
		return method(FitUtils.camel(row.cells().get(1).getFitValue()), args);
	}

	protected Method method(String name, int args) throws NoSuchMethodException {
		Method methods[] = actor.getClass().getMethods();
		Method result = null;

		for (Method m : methods) {
			if (m.getName().equals(name) && m.getParameterTypes().length == args) {
				if (result == null) {
					result = m;
				} else {
					throw new NoSuchMethodException("too many implementations");
				}
			}
		}
		if (result == null) {
			throw new NoSuchMethodException(name);
		}
		return result;
	}


	/**
	 * Waits until a method returns true. The command takes a method name
	 * without parameters and a return value of {@code Boolean} as the first
	 * parameter and a timeout in ms as the second parameter.<br>
	 * The method is called every {@code sleepTime}ms, until it returns true or the timeout is
	 * exceeded.
	 */
	public void waitFor() throws ParseException, NoSuchMethodException {
		Method method = method(0);

		long maxTime = parse(Long.class, row.cells().get(2).getFitValue());
		long sleepTime = getSleepTime();

		waitForResult.wait(actor, method, maxTime, sleepTime);

		writeResultIntoCell(waitForResult);
	}

	private long getSleepTime() throws ParseException {
		long sleepTime = DEFAULT_SLEEP_TIME;
		if (row.size() > 3) {
			sleepTime = parse(Long.class, row.cells().get(3).getFitValue());
		}
		return sleepTime;
	}

	private void writeResultIntoCell(final WaitForResult waitForResult) {
        FitCell cell = row.cells().get(2);

        cell.setDisplayValue(Long.toString(waitForResult.getLastElapsedTime()));
		if (waitForResult.lastCallWasSuccessful()) {
            cell.right();
		} else {
            cell.wrong();
            cell.info("(Timeout)");
		}
	}

	private TypeHandler getTypeHandler(final Class<?> type, String currentCellParameter) {
		return typeHandlerFactory.getHandler(type, currentCellParameter);
	}

	/**
	 * Transforms the selected row into an &quot;enter&quot; command and
	 * reinterprets it.
	 * <p/>
	 * Example:
	 * Row content: {@code setEncoding | utf-8} <br>
	 * Code in the fixture:
	 * {@code
	 * public void setEncoding() throws Exception {
	 * transformAndEnter();
	 * }
	 * <p/>
	 * public void setEncoding(String encoding) {
	 * // do stuff with encoding here
	 * }
	 * }
	 *
	 * @throws Exception should be propagated to fit.
	 */
	protected final void transformAndEnter() throws Exception {
        FitCell cell = row.insert(0);
        cell.setFitValue("enter");

		Object oldActor = actor;
		actor = this;
		enter();
		actor = oldActor;

        row.remove(0);
	}

}
