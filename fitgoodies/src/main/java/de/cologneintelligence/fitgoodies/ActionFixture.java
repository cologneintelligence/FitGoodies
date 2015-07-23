package de.cologneintelligence.fitgoodies;

// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

import de.cologneintelligence.fitgoodies.adapters.TypeAdapterHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import de.cologneintelligence.fitgoodies.util.FixtureTools;
import de.cologneintelligence.fitgoodies.util.WaitForResult;

import java.lang.reflect.Method;

public class ActionFixture extends Fixture {

	private static final int DEFAULT_SLEEP_TIME = 100;

	protected Parse cells;
	protected Object actor;
	protected static Class empty[] = {};

	public ActionFixture() {
		this.actor = this;
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
		final Method method = method(1);
		final Class<?> type = method.getParameterTypes()[0];
		final TypeAdapter ta = getTypeAdapter(type);
		if (ta != null) {
			final Object[] args = {ta.parse(cells.more.more.text())};
			method.invoke(actor, args);
		}
	}


	public void press() throws Exception {
		method(0).invoke(actor, empty);
	}

	public void check() throws Exception {
		TypeAdapter adapter = TypeAdapter.on(actor, this, method(0));
		check(cells.more.more, adapter);
	}

	// Utility //////////////////////////////////

	protected Method method(int args) throws NoSuchMethodException {
		return method(FitUtils.camel(cells.more.text()), args);
	}

	protected Method method(String test, int args) throws NoSuchMethodException {
		Method methods[] = actor.getClass().getMethods();
		Method result = null;

		for (Method m : methods) {
			if (m.getName().equals(test) && m.getParameterTypes().length == args) {
				if (result == null) {
					result = m;
				} else {
					throw new NoSuchMethodException("too many implementations");
				}
			}
		}
		if (result == null) {
			throw new NoSuchMethodException();
		}
		return result;
	}


	/**
	 * Waits until a method returns true. The command takes a method name
	 * without parameters and a return value of {@code Boolean} as the first
	 * parameter and a timeout in ms as the second parameter.<br>
	 * The method is called every {@code sleepTime}ms, until it returns true or the timeout is
	 * exceeded.
	 * @throws Exception propagated to fit
	 */
	public void waitFor() throws Exception {
		final Method method = method(0);
		final TypeAdapter typeAdapter = getTypeAdapter(Long.class);
		final long maxTime = (Long) typeAdapter.parse(cells.more.more.text());
		final long sleepTime = getSleepTime(typeAdapter);
		final WaitForResult waitForResult = new WaitForResult(method, actor, maxTime);
		waitForResult.setSleepTime(sleepTime);
		waitForResult.repeatInvokeWithTimeout();
		writeResultIntoCell(waitForResult);
	}

	private long getSleepTime(final TypeAdapter typeAdapter) throws Exception {
		long sleepTime = DEFAULT_SLEEP_TIME;
		if (cells.more.more.more != null) {
			sleepTime = (Long) typeAdapter.parse(cells.more.more.more.text());
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

	private TypeAdapter getTypeAdapter(final Class<?> type) {
		TypeAdapter ta = TypeAdapter.on(actor, this, type);
		final TypeAdapterHelper taHelper = DependencyManager.getOrCreate(TypeAdapterHelper.class);
		final CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
		ta = FixtureTools.rebindTypeAdapter(ta, getCellParameter(), taHelper);
		ta = FixtureTools.processCell(cells.more.more, ta, this, helper);
		return ta;
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
		final Parse oldmore = cells.more;
		cells.more = new Parse("<td></td>", new String[] { "td" });
		cells.more.body = cells.body;
		cells.more.more = oldmore;
		cells.body = "enter";

		final Object oldActor = actor;
		actor = this;
		enter();
		actor = oldActor;

		cells.body = cells.more.body;
		cells.more = cells.more.more;
	}

	@Override
	protected void doRow(final Parse row) {
		setCellParameter(FixtureTools.extractCellParameter(row.parts));
		super.doRow(row);
	}
}
