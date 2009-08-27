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


package fitgoodies.references;

import fitgoodies.ActionFixture;
import fitgoodies.references.processors.AbstractCrossReferenceProcessor;

/**
 * This fixture allows to register new processors via HTML. A processor
 * must extend an <code>AbstractCrossReferenceProcessor</code> and it must
 * be located in java's class path.<br /><br />
 *
 * To load a processor, you must specify its fully qualified class name:
 * <table border="1">
 * 	<tr><td colspan="2">fitgoodies.references.SetupFixture</td></tr>
 * 	<tr><td>use</td><td>com.example.MyReferenceProcessor</td></tr>
 *  <tr><td>remove</td><td>com.example.MyOtherReferenceProcessor</td></tr>
 * </table>
 *
 * @author jwierum
 * @version $Id: SetupFixture.java 185 2009-08-17 13:47:24Z jwierum $
 */
public class SetupFixture extends ActionFixture {
	private final Processors processors;

	/**
	 * Default constructor. Uses {@link CrossReferenceHelper}'s singleton
	 * processor list.
	 */
	public SetupFixture() {
		this(CrossReferenceHelper.instance().getProcessors());
	}

	/**
	 * Custom constructor which uses a self defined processor list.
	 * This method is basically used for testing.
	 * @param procs processor list to use
	 */
	SetupFixture(final Processors procs) {
		super();
		processors = procs;
	}

	/**
	 * Calls {@link #use(String)}, using the next cell as its parameter.
	 * @throws Exception propagated to fit
	 * @see #use(String) user(String)
	 */
	public void use() throws Exception {
		transformAndEnter();
	}

	/**
	 * Calls {@link #remove(String)}, using the next cell as its parameter.
	 * @throws Exception propagated to fit
	 * @see #remove(String) remove(String)
	 */
	public void remove() throws Exception {
		transformAndEnter();
	}

	/**
	 * Removes the processor <code>module</code> from the list of registered
	 * processors.
	 * @param module fully qualified path to an implementation of
	 * 		{@link AbstractCrossReferenceProcessor}
	 * @throws Exception propagated to fit, thrown if a class could not be found,
	 * 		casted or initialized
	 * @see #remove() remove()
	 */
	public final void remove(final String module) throws Exception {
		Class<?> c = null;
		c = Class.forName(module);

		for (int i = 0; i < processors.count(); ++i) {
			if (processors.get(i).getClass() == c) {
				processors.remove(i);
			}
		}
	}

	/**
	 * Adds the processor <code>module</code> to the list of registered processors.
	 * @param module fully qualified path to an implementation of
	 * 		{@link AbstractCrossReferenceProcessor}
	 * @throws Exception propagated to fit, thrown if a class could not be found,
	 * 		casted or initialized
	 * @see #remove() remove()
	 */
	public final void use(final String module) throws Exception {
		AbstractCrossReferenceProcessor processor;
		processor = (AbstractCrossReferenceProcessor)
			Class.forName(module).newInstance();

		processors.add(processor);
		cells.more.more.addToBody("<br />" + processor.info());
	}
}
