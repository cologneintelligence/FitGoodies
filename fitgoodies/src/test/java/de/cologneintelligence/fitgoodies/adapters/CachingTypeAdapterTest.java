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

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import fit.Fixture;
import fit.TypeAdapter;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public final class CachingTypeAdapterTest extends FitGoodiesTestCase {
	public static class DummyObject {
		private int calls = 0;
		private final int result;

		public String field = "xy";

		public DummyObject(final int r) {
			result = r;
		}

		public final int getValue() {
			++calls;
			return result;
		}

		public final int getCalls() {
			return calls;
		}
	}

	@Test
	public void testCalls() throws Exception {
		DummyObject dummy = new DummyObject(13);
		TypeAdapter taMethod = TypeAdapter.on(dummy, new Fixture(),
				DummyObject.class.getMethod("getValue", new Class<?>[]{}));
		taMethod = new CachingTypeAdapter(taMethod);

		assertThat(taMethod.get(), is(equalTo((Object) 13)));
		assertThat(taMethod.get(), is(equalTo((Object) 13)));
		assertThat(dummy.getCalls(), is(equalTo((Object) 1)));

		dummy = new DummyObject(42);
		taMethod = TypeAdapter.on(dummy, new Fixture(),
				DummyObject.class.getMethod("getValue", new Class<?>[]{}));
		taMethod = new CachingTypeAdapter(taMethod);

		assertThat(taMethod.get(), is(equalTo((Object) 42)));
		assertThat(taMethod.get(), is(equalTo((Object) 42)));
		assertThat(dummy.getCalls(), is(equalTo((Object) 1)));
	}

	@Test
	public void testSetter() throws Exception {
		DummyObject dummy = new DummyObject(13);
		TypeAdapter taField = TypeAdapter.on(dummy, new Fixture(), DummyObject.class.getField("field"));
		taField = new CachingTypeAdapter(taField);

		assertThat(taField.get(), (Matcher) is(equalTo("xy")));

		taField.set("new!");
		assertThat(taField.get(), (Matcher) is(equalTo("new!")));

		taField.set("abc");
		assertThat(taField.get(), (Matcher) is(equalTo("abc")));
	}
}
