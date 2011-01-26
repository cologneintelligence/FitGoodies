package fitgoodies.util;

import fitgoodies.references.processors.PropertyProvider;

public class SystemPropertyProvider implements PropertyProvider {

	@Override
	public String getProperty(String key) {
		System.err.println(key);
		System.err.println(System.getProperty(key));
		return System.getProperty(key);
	}

}
