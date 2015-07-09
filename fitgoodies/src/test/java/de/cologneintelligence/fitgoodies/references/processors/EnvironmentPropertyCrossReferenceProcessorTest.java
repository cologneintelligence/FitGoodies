/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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

import de.cologneintelligence.fitgoodies.references.CrossReference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnvironmentPropertyCrossReferenceProcessorTest {
	@Test
	public void testGetJavaHome() throws Exception {
		final PropertyProvider propertyProvider = mock(PropertyProvider.class);
		when(propertyProvider.getProperty("key")).thenReturn("testProperty");

		EnvironmentPropertyCrossReferenceProcessor processor = new EnvironmentPropertyCrossReferenceProcessor(propertyProvider);

		CrossReference cr = new CrossReference("getProperty", "System", "key", processor);
		assertEquals("testProperty", processor.processMatch(cr  , null));
	}
}
