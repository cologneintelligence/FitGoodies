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


package fitgoodies.alias;

import fitgoodies.FitGoodiesTestCase;

/**
 * $Id$
 * @author jwierum
 */
public final class AliasHelperTest extends FitGoodiesTestCase {
	public void testSingleton() {
		AliasHelper helper = AliasHelper.instance();

		assertNotNull(helper);
		assertSame(helper, AliasHelper.instance());

		AliasHelper.reset();
		assertNotNull(AliasHelper.instance());
		assertNotSame(helper, AliasHelper.instance());
	}

	public void testGetDefaultClass()  {
		String actual = AliasHelper.instance().getClazz("java.lang.String");
		assertEquals("java.lang.String", actual);

		actual = AliasHelper.instance().getClazz("java.lang.Integer");
		assertEquals("java.lang.Integer", actual);
	}

	public void testAddAlias() throws ClassNotFoundException {
		assertEquals("test", AliasHelper.instance().getClazz("test"));

		AliasHelper.instance().register("test", "fitgoodies.Fixture");
		assertEquals("fitgoodies.Fixture", AliasHelper.instance().getClazz("test"));

		AliasHelper.instance().register("test", "fitgoodies.ActionFixture");
		assertEquals("fitgoodies.ActionFixture", AliasHelper.instance().getClazz("test"));

		assertEquals("test2", AliasHelper.instance().getClazz("test2"));

		AliasHelper.instance().register("test2", "fitgoodies.RowFixture");
		assertEquals("fitgoodies.RowFixture", AliasHelper.instance().getClazz("test2"));
	}
}
