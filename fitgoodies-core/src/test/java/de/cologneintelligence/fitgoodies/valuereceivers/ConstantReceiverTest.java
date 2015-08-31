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

package de.cologneintelligence.fitgoodies.valuereceivers;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConstantReceiverTest {

	@Test
	public void testGet() throws Exception {
		assertThat(new ConstantReceiver(12).get(), (Matcher) is(equalTo(12)));
		assertThat(new ConstantReceiver("test").get(), (Matcher) is(equalTo("test")));
		assertThat(new ConstantReceiver("test", String.class).get(), (Matcher) is(equalTo("test")));
		assertThat(new ConstantReceiver(null, String.class).get(), is(equalTo(null)));
	}

	@Test
	public void testGetType() throws Exception {
		assertThat(new ConstantReceiver(12).getType(), (Matcher) is(equalTo(Integer.class)));
		assertThat(new ConstantReceiver(12, Integer.class).getType(), (Matcher) is(equalTo(Integer.class)));
		assertThat(new ConstantReceiver("test", String.class).getType(), (Matcher) is(equalTo(String.class)));
		assertThat(new ConstantReceiver(null, String.class).getType(), (Matcher) is(equalTo(String.class)));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testSet() throws Exception {
		new ConstantReceiver(12).set(null, null);
	}

	@Test
	public void testCanSet() throws Exception {
		assertThat(new ConstantReceiver(12).canSet(), is(false));
	}
}
