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


package de.cologneintelligence.fitgoodies.references.processors;
import java.util.HashMap;

/**
 * @param <T> value type of the HashMap
 *
 * @author jwierum
 * @version $Id$
 */

class NamespaceHashMap<T> {
	private final HashMap<String, HashMap<String, T>> map
		= new HashMap<String, HashMap<String, T>>();

	public void put(final String namespace, final String key, final T value) {
		if (!map.containsKey(namespace)) {
			map.put(namespace, new HashMap<String, T>());
		}

		map.get(namespace).put(key, value);

	}

	public int size() {
		int size = 0;

		for (String key : map.keySet()) {
			size += map.get(key).size();
		}

		return size;
	}

	public T delete(final String namespace, final String key) {
		if (!map.containsKey(namespace)) {
			return null;
		}

		return map.get(namespace).remove(key);
	}

	public T get(final String namespace, final String key) {
		if (!map.containsKey(namespace)) {
			return null;
		}

		return map.get(namespace).get(key);
	}


}
