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

package de.cologneintelligence.fitgoodies.references;

import de.cologneintelligence.fitgoodies.references.CellProcessor;
import de.cologneintelligence.fitgoodies.references.DateReferenceProcessorProvider;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DateProvider;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class DateReferenceProcessorProviderTest extends FitGoodiesTestCase {

	@Mock
	private DateProvider dateProvider;
	private DateReferenceProcessorProvider provider;

	@Before
	public void setUp() {
		DependencyManager.inject(DateProvider.class, dateProvider);
		provider = new DateReferenceProcessorProvider();
	}

	@Test
	public void testParse() {
		assertThat(provider.canProcess("${dateProvider.getCurrentDate()}"), is(true));
		assertThat(provider.canProcess("${dateProvider.getCurrentDate(test)}"), is(true));
		assertThat(provider.canProcess("y${dateProvider.getCurrentDate()}x"), is(true));
		assertThat(provider.canProcess("${getCURRENTDate()}"), is(true));

		assertThat(provider.canProcess("${other.getCURRENTDate()}"), is(false));
	}

	@Test
	public void testGetCurrentDate() throws Exception {
		final String date1 = "21.01.2009";
		final String date2 = "21.01.2010";
		when(dateProvider.getCurrentDate()).thenReturn(date1, date2);

		CellProcessor processor = provider.create("${dateprovider.getCurrentDate()}");
		assertThat(processor.preprocess(), is(equalTo("21.01.2009")));

		processor = provider.create(">${getCurrentDate()}<");
		assertThat(processor.preprocess(), is(equalTo(">21.01.2010<")));

	}

	@Test
	public void testGetCurrentDateWithFormat() throws Exception {
		final String date1 = "21.01.2009";
		final String date2 = "21.01.2010";
		when(dateProvider.getCurrentDate("test")).thenReturn(date1);
		when(dateProvider.getCurrentDate("test2")).thenReturn(date2);

		CellProcessor processor = provider.create("${getCurrentDate(test)}");
		assertThat(processor.preprocess(), is(equalTo("21.01.2009")));

		processor = provider.create(">${getCurrentDate(test2)}<");
		assertThat(processor.preprocess(), is(equalTo(">21.01.2010<")));
	}

}
