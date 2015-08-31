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

package de.cologneintelligence.fitgoodies.typehandler;

import de.cologneintelligence.fitgoodies.testsupport.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.types.InternalTestableString;
import de.cologneintelligence.fitgoodies.types.TestableString;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InternalTestableStringTypeHandlerTest extends FitGoodiesTestCase {

	@Test
	public void testGetType() throws Exception {
		assertThat(new TestableStringTypeHandler("").getType(), is(equalTo(TestableString.class)));
	}

	@Test
	public void testUnsafeParse() throws Exception {
		InternalTestableString parse1 = new TestableStringTypeHandler("starts with").unsafeParse("test");
		InternalTestableString parse2 = new TestableStringTypeHandler("contains").unsafeParse("value");
		assertThat(parse1.getContent(), is(equalTo("test")));
		assertThat(parse2.getContent(), is(equalTo("value")));
		assertThat(parse1.getTestType(), is(equalTo(InternalTestableString.TestType.STARTSWITH)));
		assertThat(parse2.getTestType(), is(equalTo(InternalTestableString.TestType.CONTAINS)));
	}
}
