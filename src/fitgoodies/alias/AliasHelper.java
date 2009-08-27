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

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class to manage aliases.
 *
 * @author jwierum
 * @version $Id: AliasHelper.java 185 2009-08-17 13:47:24Z jwierum $
 */
public final class AliasHelper {
	private static AliasHelper instance;
	private final Map<String, String> classMapper = new HashMap<String, String>();

	private AliasHelper() {
	}

	/**
	 * Resets the helper to default values.
	 */
	public static void reset() {
		instance = null;
	}

	/**
	 * Returns an instance of <code>AliasHelper</code>.
	 * @return instance of <code>AliasHelper</code>
	 */
	public static AliasHelper instance() {
		if (instance == null) {
			instance = new AliasHelper();
		}
		return instance;
	}

	/**
	 * Looks up a class in the alias map. If <code>className</code> exists as
	 * an alias, it is resolved and returned. If the alias doesn't exist, the
	 * original String is returned.
	 *
	 * @param className class name to look up
	 * @return resolved name of the alias
	 */
	public String getClazz(final String className) {
		String realName = classMapper.get(className);
		if (realName == null) {
			realName = className;
		}
		return realName;
	}

	/**
	 * Registers a new alias <code>alias</code> for <code>className</code>.
	 *
	 * @param alias alias to use
	 * @param className class the alias maps on
	 */
	public void register(final String alias, final String className) {
		classMapper.put(alias, className);
	}
}
