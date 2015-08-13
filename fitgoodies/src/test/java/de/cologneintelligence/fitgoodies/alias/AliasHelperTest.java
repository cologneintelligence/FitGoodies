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


package de.cologneintelligence.fitgoodies.alias;

import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public final class AliasHelperTest extends FitGoodiesTestCase {
	private AliasHelper helper;

	@Before
	public void setUp() throws Exception {
		helper = new AliasHelper();
	}

	@Test
	public void testGetDefaultClass() {
		String actual = helper.getClazz("java.lang.String");
		assertThat(actual, is(equalTo("java.lang.String")));

		actual = helper.getClazz("java.lang.Integer");
		assertThat(actual, is(equalTo("java.lang.Integer")));
	}

	@Test
	public void testGetFitgoodiesMapping() {
		assertMapping("fitgoodies.ActionFixture");
		assertMapping("fitgoodies.ColumnFixture");
		assertMapping("fitgoodies.RowFixture");

		assertMapping("fitgoodies.file.FileFixture");
		assertMapping("fitgoodies.runners.RunFixture");
		assertMapping("fitgoodies.selenium.SeleniumFixture");
		assertMapping("fitgoodies.selenium.SetupFixture");
		assertMapping("fitgoodies.mail.SetupFixture");
		assertMapping("fitgoodies.date.SetupFixture");
		assertMapping("fitgoodies.database.SetupFixture");
		assertMapping("fitgoodies.typehandler.SetupFixture");

		assertMapping("fitgoodies.database.ResultSetFixture");
		assertMapping("fitgoodies.database.TableFixture");

		assertMapping("fitgoodies.file.CSVFileRecordFixture");
		assertMapping("fitgoodies.file.DelimiterFileRecordFixture");
		assertMapping("fitgoodies.file.FixedLengthFileRecordFixture");
		assertMapping("fitgoodies.file.XMLFileFixture");
		assertMapping("fitgoodies.log4j.LogFixture");
		assertMapping("fitgoodies.log4j.SetupFixture");
		assertMapping("fitgoodies.mail.MailFixture");
		assertMapping("fitgoodies.alias.SetupFixture");

		assertMapping("fitgoodies.external.StartFixture");
	}

	private void assertMapping(final String className) {
		final String actual = helper.getClazz(className);
		final String expected = "de.cologneintelligence." + className;
		assertThat(actual, is(equalTo(expected)));
		try {
			assertThat(Class.forName(actual), not(CoreMatchers.is(nullValue())));
		} catch (final ClassNotFoundException e) {
			Assert.fail("The referenced fixture " + actual + " does not exist");
		}
	}

	@Test
	public void testAddAlias() throws ClassNotFoundException {
		assertThat(helper.getClazz("test"), is(equalTo("test")));

		helper.register("test", "fitgoodies.MyFixture");
		assertThat(helper.getClazz("test"), is(equalTo("fitgoodies.MyFixture")));

		helper.register("test", "fitgoodies.MyActionFixture");
		assertThat(helper.getClazz("test"), is(equalTo("fitgoodies.MyActionFixture")));

		assertThat(helper.getClazz("test2"), is(equalTo("test2")));

		helper.register("test2", "fitgoodies.MyRowFixture");
		assertThat(helper.getClazz("test2"), is(equalTo("fitgoodies.MyRowFixture")));
	}

	@Test
	public void testRecursiveAliases() {
		helper.register("test", "test2");
		helper.register("test2", "test3");
		helper.register("test3", "ok");

		assertThat(helper.getClazz("test"), is(equalTo("ok")));
	}
}
