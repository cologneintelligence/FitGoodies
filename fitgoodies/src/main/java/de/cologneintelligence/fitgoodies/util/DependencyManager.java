package de.cologneintelligence.fitgoodies.util;

import java.util.HashMap;
import java.util.Map;

public class DependencyManager {
    public static final DependencyManager INSTANCE = new DependencyManager();
    private DependencyManager() {}

    private final Map<Class<?>, Object> cache = new HashMap<Class<?>, Object>();

    public void clear() {
        cache.clear();
    }

    public <T> T getOrCreate(final Class<T> className) {
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
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void inject(final Class<T> className, final T injected) {
        cache.put(className, injected);
    }
}
