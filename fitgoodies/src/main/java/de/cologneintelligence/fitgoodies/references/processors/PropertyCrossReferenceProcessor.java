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
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.cologneintelligence.fitgoodies.references.CrossReference;


/**
 * Reference processor that allows users to load values from property files.
 * The namespace is interpreted as the filename without its extension.
 *
 * @author kmussawisade
 * @version $Id$
 */
public class PropertyCrossReferenceProcessor extends AbstractCrossReferenceProcessor {
	private static final String PATTERN =
        "([A-Za-z0-9\\-/]+)\\.(getValue)\\(([^)]+)\\)";

	/**
	 * Default constructor.
	 */
	public PropertyCrossReferenceProcessor() {
		super(PATTERN);
	}

    private final Map<String, ResourceBundle> resourceBundleMap =
    	new HashMap<String, ResourceBundle>();

	/**
	 * Replaces the match with the selected property.
	 * The namespace of the reference must be the filename of the property file.
	 * The command is the property name.
	 * @param cr the extracted match
	 * @param object ignored
	 * @return the value of <code>cr.getCommand()</code> in the file
	 * 		<code>cr.getNamespace()</code>.
	 */
    @Override
	public final String processMatch(final CrossReference cr, final Object object) {
    	ResourceBundle rb = getResourceBundel(cr.getNamespace());
        return rb.getString(cr.getParameter());
    }

    private ResourceBundle getResourceBundel(final String namespace) {
        ResourceBundle resourceBundle = resourceBundleMap.get(namespace);
        if (resourceBundle == null) {
            resourceBundle = PropertyResourceBundle.getBundle(namespace);
            setResourceBundle(namespace, resourceBundle);
        }
        return resourceBundle;
    }

    /**
     * Loads a resource bundle into a namespace.
     * @param name namespace to use
     * @param resourceBundle the resourceBundle to set
     */
    public final void setResourceBundle(final String name,
    		final ResourceBundle resourceBundle) {
        this.resourceBundleMap.put(name, resourceBundle);
    }

	/**
	 * A user friendly description.
	 * @return a description.
	 */
    @Override
	public final String info() {
        return "reads a property file and provides getValue(properyName)";
    }
}
