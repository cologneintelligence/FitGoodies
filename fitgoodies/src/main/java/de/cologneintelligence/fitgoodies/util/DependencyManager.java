package de.cologneintelligence.fitgoodies.util;

import java.util.HashMap;
import java.util.Map;

public final class DependencyManager {
    private static final DependencyManager INSTANCE = new DependencyManager();
    private DependencyManager() {}

    private final Map<Class<?>, Object> cache = new HashMap<Class<?>, Object>();

    public static void clear() {
        INSTANCE.realClear();
    }

    private void realClear() {
        cache.clear();
    }

    public static <T> T getOrCreate(final Class<T> className) {
        return INSTANCE.realGetOrCreate(className);
    }

    public <T> T realGetOrCreate(final Class<T> className) {
        if (!cache.containsKey(className)) {
            createCacheEntry(className);
        }

        @SuppressWarnings("unchecked")
        final T result = (T) cache.get(className);
        return result;
    }

    private <T> void createCacheEntry(final Class<T> className) {
        try {
            final T instance = className.newInstance();
            cache.put(className, instance);
        } catch (final InstantiationException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void inject(final Class<T> className, final T injected) {
        INSTANCE.realInject(className, injected);
    }

    public <T> void realInject(final Class<T> className, final T injected) {
        cache.put(className, injected);
    }
}
