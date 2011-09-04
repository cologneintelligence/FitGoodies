package de.cologneintelligence.fitgoodies.references.processors;

import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;

public class EnvironmentPropertyCrossReferenceProcessor extends AbstractCrossReferenceProcessor {
	private static final String PATTERN =
        "(System)\\.(getProperty)\\(([^)]+)\\)";
	private PropertyProvider propertyProvider;

	public EnvironmentPropertyCrossReferenceProcessor(PropertyProvider propertyProvider) {
		super(PATTERN);
		this.propertyProvider = propertyProvider;
	}

	@Override
	public String processMatch(CrossReference cr, Object object) throws CrossReferenceProcessorShortcutException {
		return propertyProvider.getProperty(cr.getParameter());
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

}
