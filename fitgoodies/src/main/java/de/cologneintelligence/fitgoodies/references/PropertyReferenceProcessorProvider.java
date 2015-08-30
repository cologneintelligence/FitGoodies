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


package de.cologneintelligence.fitgoodies.references;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Reference processor that allows users to load values from property files.
 * The namespace is interpreted as the filename without its extension.
 *
 * @author kmussawisade
 */
public class PropertyReferenceProcessorProvider implements CellProcessorProvider {
	private static final Pattern PATTERN = Pattern.compile(
			"\\$\\{([A-Za-z0-9\\-/]+)\\.getValue\\(([^)]+)\\)\\}",
			Pattern.CASE_INSENSITIVE);

	private final Map<String, ResourceBundle> resourceBundleMap = new HashMap<>();

	@Override
	public boolean canProcess(String strippedText) {
		return strippedText != null && PATTERN.matcher(strippedText).find();
	}

	@Override
	public CellProcessor create(final String strippedText) {
		return new CellProcessor() {
			@Override
			public String preprocess() {
				Matcher matcher = PATTERN.matcher(strippedText);
				matcher.find();
				String resource = matcher.group(1);
				String key = matcher.group(2);

				String replacement;
				try {
					replacement = getResourceBundle(resource).getString(key);
				} catch (MissingResourceException e) {
					replacement = "error: missing resource bundle: " + resource;
				}

				return strippedText.replaceAll(Pattern.quote(matcher.group(0)), replacement);
			}
		};
	}

	private ResourceBundle getResourceBundle(final String bundle) {
		ResourceBundle resourceBundle = resourceBundleMap.get(bundle);
		if (resourceBundle == null) {
			resourceBundle = PropertyResourceBundle.getBundle(bundle);
			setResourceBundle(bundle, resourceBundle);
		}
		return resourceBundle;
	}

	/**
	 * Loads a resource bundle into a namespace.
	 *
	 * @param name           namespace to use
	 * @param resourceBundle the resourceBundle to set
	 */
	protected void setResourceBundle(final String name,
	                                 final ResourceBundle resourceBundle) {
		this.resourceBundleMap.put(name, resourceBundle);
	}

    /*
	/ **
	 * A user friendly description.
	 * @return a description.
	 * /
    @Override
	public final String info() {
        return "reads a property file and provides getValue(properyName)";
    }
    */
}
