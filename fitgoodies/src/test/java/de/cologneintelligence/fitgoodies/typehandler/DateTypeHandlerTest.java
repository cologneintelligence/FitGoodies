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


package de.cologneintelligence.fitgoodies.typehandler;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class DateTypeHandlerTest extends FitGoodiesTestCase {

	private DateTypeHandler handler;

	@Before
	public void setUp() {
		handler = new DateTypeHandler(null);
	}

	@Test
	public void testParser() throws Exception {
		TypeHandler<Date> p = new DateTypeHandler(null);
		final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
		assertThat(p.parse("01/18/1987"), (Matcher) is(equalTo(dateFormat.parse("01/18/1987"))));

		p = new DateTypeHandler("de_DE, dd.MM.yyyy");
		assertThat(p.parse("18.01.1987"), (Matcher) is(equalTo(dateFormat.parse("01/18/1987"))));

		p = new DateTypeHandler("de_DE, dd.MM.yyyy");
		assertThat(p.parse("08.03.1989"), (Matcher) is(equalTo(dateFormat.parse("03/08/1989"))));

		p = new DateTypeHandler(null);
		assertThat(p.parse("03/08/1989"), (Matcher) is(equalTo(dateFormat.parse("03/08/1989"))));
	}

	@Test(expected = ParseException.class)
	public void testException() throws ParseException {
		new DateTypeHandler("invalid").parse("01/01/1970");
	}

	@Test
	public void testType() {
		assertThat(handler.getType(), not(CoreMatchers.is(nullValue())));
	}

	// TODO: test equals, toString

}
