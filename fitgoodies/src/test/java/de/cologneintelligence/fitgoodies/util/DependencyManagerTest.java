package de.cologneintelligence.fitgoodies.util;

import junit.framework.TestCase;

public class DependencyManagerTest extends TestCase {
    public static class DependencyManagerTestDummy {}

    @Override
    public void setUp() throws Exception {
        super.setUp();

        DependencyManager.INSTANCE.clear();
    }

    public void testManagerIsSingleton() {
        assertSame(DependencyManager.INSTANCE, DependencyManager.INSTANCE);
    }

    public void testUnknownClassIsLoaded() {
        DependencyManagerTestDummy obj =
                DependencyManager.INSTANCE.getOrCreate(DependencyManagerTestDummy.class);

        assertNotNull(obj);
        assertTrue(obj instanceof DependencyManagerTestDummy);
    }

    public void testObjectsAreCached() {
        DependencyManagerTestDummy obj =
                DependencyManager.INSTANCE.getOrCreate(DependencyManagerTestDummy.class);
        DependencyManagerTestDummy obj2 =
                DependencyManager.INSTANCE.getOrCreate(DependencyManagerTestDummy.class);

        assertSame(obj, obj2);
    }

    public void testClearResetsCache() {
        DependencyManagerTestDummy obj =
                DependencyManager.INSTANCE.getOrCreate(DependencyManagerTestDummy.class);
        DependencyManager.INSTANCE.clear();
        DependencyManagerTestDummy obj2 =
                DependencyManager.INSTANCE.getOrCreate(DependencyManagerTestDummy.class);

        assertNotSame(obj, obj2);
    }

    public void testInjectOverridesCache() {
        DependencyManagerTestDummy injected = new DependencyManagerTestDummy();

        DependencyManager.INSTANCE.inject(DependencyManagerTestDummy.class, injected);
        DependencyManagerTestDummy obj2 =
                DependencyManager.INSTANCE.getOrCreate(DependencyManagerTestDummy.class);

        assertSame(obj2, injected);
    }
}
