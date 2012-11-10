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


package de.cologneintelligence.fitgoodies.adapters;

import de.cologneintelligence.fitgoodies.ColumnFixture;
import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.adapters.CachingTypeAdapter;
import fit.TypeAdapter;

/**
 *
 * @author jwierum
 */
public final class CachingTypeAdapterTest extends FitGoodiesTestCase {
	public static class DummyFixture extends ColumnFixture {
		private int calls = 0;
		private final int result;

		public String field = "xy";

		public DummyFixture(final int r) {
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

	public void testCalls() throws Exception {
		DummyFixture dummy = new DummyFixture(13);
		TypeAdapter taMethod = TypeAdapter.on(dummy,
				DummyFixture.class.getMethod("getValue", new Class<?>[]{}));
		taMethod = new CachingTypeAdapter(taMethod);

		assertEquals(13, taMethod.get());
		assertEquals(13, taMethod.get());
		assertEquals(1, dummy.getCalls());

		dummy = new DummyFixture(42);
		taMethod = TypeAdapter.on(dummy,
				DummyFixture.class.getMethod("getValue", new Class<?>[]{}));
		taMethod = new CachingTypeAdapter(taMethod);

		assertEquals(42, taMethod.get());
		assertEquals(42, taMethod.get());
		assertEquals(1, dummy.getCalls());
	}

	public void testSetter() throws Exception {
		DummyFixture dummy = new DummyFixture(13);
		TypeAdapter taField = TypeAdapter.on(dummy, DummyFixture.class.getField("field"));
		taField = new CachingTypeAdapter(taField);

		assertEquals("xy", taField.get());

		taField.set("new!");
		assertEquals("new!", taField.get());

		taField.set("abc");
		assertEquals("abc", taField.get());
	}
}
