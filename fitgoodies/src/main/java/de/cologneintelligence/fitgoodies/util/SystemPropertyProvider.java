package de.cologneintelligence.fitgoodies.util;

import de.cologneintelligence.fitgoodies.references.processors.PropertyProvider;

public class SystemPropertyProvider implements PropertyProvider {

	@Override
	public String getProperty(final String key) {
		return System.getProperty(key);
	}

}
