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
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ObjectTypeHandlerTestTest extends FitGoodiesTestCase {

	private ObjectTypeHandler handler;

	@Before
	public void setUp() {
		handler = new ObjectTypeHandler(null);
	}

	@Test
	public void testGetType() throws ParseException {
		assertThat(handler.unsafeParse("a test"), (Matcher) is(equalTo("a test")));
		assertThat(handler.unsafeParse("a test 2"), (Matcher) is(equalTo("a test 2")));
	}

	@Test
	public void testType() throws Exception {
		assertThat(handler.getType(), is(equalTo(Object.class)));
	}
}
