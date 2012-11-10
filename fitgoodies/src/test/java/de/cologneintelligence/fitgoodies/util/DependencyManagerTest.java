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
