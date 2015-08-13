package de.cologneintelligence.fitgoodies;

// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import de.cologneintelligence.fitgoodies.util.WaitForResult;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;

import java.lang.reflect.Method;
import java.text.ParseException;

public class ActionFixture extends Fixture {

	public static final long DEFAULT_SLEEP_TIME = 100;

	protected final WaitForResult waitForResult;

	protected Parse cells;
	protected Object actor;
	protected static Class empty[] = {};

	private String currentCellParameter;

	public ActionFixture(WaitForResult waitForResult) {
		this.waitForResult = waitForResult;
		this.actor = this;
	}

	public ActionFixture() {
		this(new WaitForResult());
	}

	// Traversal ////////////////////////////////

	protected void doCells(Parse cells) {
		this.cells = cells;
		try {
			Method action = getClass().getMethod(cells.text(), empty);
			action.invoke(this, empty);
		} catch (Exception e) {
			exception(cells, e);
		}
	}

	// Actions //////////////////////////////////

	public void start() throws Exception {
		actor = Class.forName(cells.more.text()).newInstance();
	}

	public void enter() throws Exception {
		Method method = method(1);
		String cellText = cells.more.more.text();
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
		method(0).invoke(actor, empty);
	}

	public void check() throws Exception {
		ValueReceiver receiver = createReceiver(actor, method(0));
		check(cells.more.more, receiver, null);
	}

	// Utility //////////////////////////////////

	protected Method method(int args) throws NoSuchMethodException {
		return method(FitUtils.camel(cells.more.text()), args);
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

		long maxTime = parse(Long.class, cells.more.more.text());
		long sleepTime = getSleepTime();

		waitForResult.wait(actor, method, maxTime, sleepTime);

		writeResultIntoCell(waitForResult);
	}

	private long getSleepTime() throws ParseException {
		long sleepTime = DEFAULT_SLEEP_TIME;
		if (cells.size() > 3) {
			sleepTime = parse(Long.class, cells.at(3).text());
		}
		return sleepTime;
	}

	private void writeResultIntoCell(final WaitForResult waitForResult) {
		cells.more.more.body = Long.toString(waitForResult.getLastElapsedTime());
		if (waitForResult.lastCallWasSuccessful()) {
			right(cells.more.more);
		} else {
			wrong(cells.more.more);
			info(cells.more.more, "(Timeout)");
		}
	}

	private TypeHandler getTypeHandler(final Class<?> type, String currentCellParameter) {
		return typeHandlerFactory.getHandler(type, currentCellParameter);
	}

	/**
	 * Transforms the selected row into an &quot;enter&quot; command and
	 * reinterprets it.
	 * <p>
	 * Example:
	 * Row content: {@code setEncoding | utf-8} <br>
	 * Code in the fixture:
	 * {@code
	 * public void setEncoding() throws Exception {
	 *     transformAndEnter();
	 * }
	 *
	 * public void setEncoding(String encoding) {
	 *     // do stuff with encoding here
	 * }
	 * }
	 *
	 * @throws Exception should be propagated to fit.
	 */
	protected final void transformAndEnter() throws Exception {
		Parse oldMore = cells.more;
		cells.more = new Parse("<td></td>", new String[] { "td" });
		cells.more.body = cells.body;
		cells.more.more = oldMore;
		cells.body = "enter";

		Object oldActor = actor;
		actor = this;
		enter();
		actor = oldActor;

		cells.body = cells.more.body;
		cells.more = cells.more.more;
	}

	@Override
	protected void doRow(final Parse row) {
		currentCellParameter = FitUtils.extractCellParameter(row.parts);
		super.doRow(row);
	}
}
