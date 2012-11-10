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

package de.cologneintelligence.fitgoodies.util;

import junit.framework.TestCase;

public class DependencyManagerTest extends TestCase {
    public static class DependencyManagerTestDummy {}

    @Override
    public void setUp() throws Exception {
        super.setUp();

        DependencyManager.clear();
    }

    public void testUnknownClassIsLoaded() {
        final DependencyManagerTestDummy obj =
                DependencyManager.getOrCreate(DependencyManagerTestDummy.class);

        assertNotNull(obj);
        assertTrue(obj instanceof DependencyManagerTestDummy);
    }

    public void testObjectsAreCached() {
        final DependencyManagerTestDummy obj =
                DependencyManager.getOrCreate(DependencyManagerTestDummy.class);
        final DependencyManagerTestDummy obj2 =
                DependencyManager.getOrCreate(DependencyManagerTestDummy.class);

        assertSame(obj, obj2);
    }

    public void testClearResetsCache() {
        final DependencyManagerTestDummy obj =
                DependencyManager.getOrCreate(DependencyManagerTestDummy.class);
        DependencyManager.clear();
        final DependencyManagerTestDummy obj2 =
                DependencyManager.getOrCreate(DependencyManagerTestDummy.class);

        assertNotSame(obj, obj2);
    }

    public void testInjectOverridesCache() {
        final DependencyManagerTestDummy injected = new DependencyManagerTestDummy();

        DependencyManager.inject(DependencyManagerTestDummy.class, injected);
        final DependencyManagerTestDummy obj2 =
                DependencyManager.getOrCreate(DependencyManagerTestDummy.class);

        assertSame(obj2, injected);
    }
}
