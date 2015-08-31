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

package de.cologneintelligence.fitgoodies.util;

import java.util.HashMap;
import java.util.Map;

public final class DependencyManager {
	private static final DependencyManager INSTANCE = new DependencyManager();

	private DependencyManager() {
	}

	private final Map<Class<?>, Object> cache = new HashMap<>();

	public static void clear() {
		INSTANCE.realClear();
	}

	private void realClear() {
		cache.clear();
	}

	public static <T> T getOrCreate(final Class<T> className) {
		return INSTANCE.realGetOrCreate(className, className);
	}

	public <T> T realGetOrCreate(final Class<T> className, final Class<? extends T> concreteClass) {
		if (!cache.containsKey(className)) {
			createCacheEntry(className, concreteClass);
		}

		@SuppressWarnings("unchecked")
		final T result = (T) cache.get(className);
		return result;
	}

	private <T> void createCacheEntry(final Class<T> className, final Class<? extends T> concreteClass) {
		try {
			final T instance = concreteClass.newInstance();
			cache.put(className, instance);
		} catch (final InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void inject(final Class<T> className, final T injected) {
		INSTANCE.realInject(className, injected);
	}

	public <T> void realInject(final Class<T> className, final T injected) {
		cache.put(className, injected);
	}

	public static <T> T getOrCreate(final Class<T> baseClass, final Class<? extends T> concreteClass) {
		return INSTANCE.realGetOrCreate(baseClass, concreteClass);
	}
}
