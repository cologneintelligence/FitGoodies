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
package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.references.processors.CrossReferenceProcessorMock;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author jwierum
 */
public final class RowFixtureTest extends FitGoodiesTestCase {

	public static class DummyRowObject {

		public Integer x;
		public String y;
		public Integer z;

		public DummyRowObject(
				final Integer xVal,
				final String yVal,
				final Integer zVal) {
			x = xVal;
			y = yVal;
			z = zVal;
		}
	}

	public static class TestRowObject {

		public String x;
		public Integer y;
		public Integer z;

		public TestRowObject(
				final String xVal,
				final Integer yVal,
				final Integer zVal) {
			x = xVal;
			y = yVal;
			z = zVal;
		}
	}

	private static class DummyRowFixture extends RowFixture {

		public boolean upCalled;
		public boolean downCalled;

		@Override
		public Class<?> getTargetClass() {
			return DummyRowObject.class;
		}

		@Override
		public Object[] query() throws Exception {
			return new DummyRowObject[]{
					new DummyRowObject(1, "x", 3),
					new DummyRowObject(8, "matched", 6)
			};
		}

		@Override
		public void setUp() {
			upCalled = true;
		}

		@Override
		public void tearDown() {
			downCalled = true;
		}
	}

	private static class TestRowFixture extends RowFixture {

		public boolean upCalled;
		public boolean downCalled;

		@Override
		public Class<?> getTargetClass() {
			return TestRowObject.class;
		}

		@Override
		public Object[] query() throws Exception {
			return new TestRowObject[]{
					new TestRowObject("x", 2, 3),
					new TestRowObject("x", 5, 6)
			};
		}

		@Override
		public void setUp() {
			upCalled = true;
		}

		@Override
		public void tearDown() {
			downCalled = true;
		}
	}

	public static final class ErrorFixture extends DummyRowFixture {

		private boolean downCalled = false;

		public boolean isDownCalled() {
			return downCalled;
		}

		@Override
		public void tearDown() {
			downCalled = true;
		}
	}

	private DummyRowFixture rowFixture;

	@Before
	public void setUp() throws Exception {
		rowFixture = new DummyRowFixture();
	}

	@Test
	public void testNumberCases() {
		Parse table = parseTable(
				tr("x", "y", "z"),
				tr("1", "x", "3"));
		rowFixture.doTable(table);
		assertThat(rowFixture.counts().right, is(equalTo((Object) 3)));
		assertThat(rowFixture.counts().wrong, is(equalTo((Object) 1)));

		table = parseTable(
				tr("x", "y", "z"),
				tr("1", "match", "3"));
		rowFixture.doTable(table);
		assertThat(rowFixture.counts().wrong, is(equalTo((Object) 4)));
		assertThat(rowFixture.counts().right, is(equalTo((Object) 5)));
	}

	@Test
	public void testCrossReferencesForStringValues() {
		Parse table = parseTable(
				tr("x", "y", "z"),
				tr("8", "${2}", "6"));

		CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
		helper.getProcessors().add(new CrossReferenceProcessorMock("2"));

		rowFixture.doTable(table);
		assertThat(rowFixture.counts().right, is(equalTo((Object) 3)));

		table = parseTable(
				tr("x", "y"),
				tr("8", "${test}"));
		rowFixture.doTable(table);
		assertThat(rowFixture.counts().wrong, is(equalTo((Object) 4)));
	}

	@Test
	public void testCrossReferencesForIntegerValues() {
		TestRowFixture sameValueRowFixture = new TestRowFixture();
		Parse table = parseTable(
				tr("x", "y", "z"),
				tr("x", "${test}", "3"));

		CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
		helper.getProcessors().add(new CrossReferenceProcessorMock("test", "2"));

		sameValueRowFixture.doTable(table);
		assertThat(sameValueRowFixture.counts().right, is(3));
	}

	@Test
	public void testCrossReferencesExpectedRowsCountEqualsComputedRowsCount() {
		TestRowFixture sameValueRowFixture = new TestRowFixture();
		Parse table = parseTable(
				tr("x", "y", "z"),
				tr("x", "${test2}", "3"),
				tr("x", "5", "6"));

		CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
		helper.getProcessors().add(new CrossReferenceProcessorMock("test2", "2"));

		sameValueRowFixture.doTable(table);
		assertThat(sameValueRowFixture.counts().right, is(6));
	}

	@Test
	public void testUpDown() {
		Parse table = parseTable(
				tr("number", "n()"),
				tr("1", "1"));
		rowFixture.doTable(table);

		assertThat(rowFixture.upCalled, is(true));
		assertThat(rowFixture.downCalled, is(true));
	}

	@Test
	public void testGetParams() {
		rowFixture.setParams(new String[]{"x=y", "a=b"});

		assertThat(rowFixture.getParam("x"), is(equalTo("y")));
		assertThat(rowFixture.getParam("y"), is(nullValue()));

		assertThat(rowFixture.getParam("a", "z"), is(equalTo("b")));
		assertThat(rowFixture.getParam("u", "z"), is(equalTo("z")));
	}

	@Test
	public void testUpWithErrors() throws Exception {
		Parse table = parseTable(tr("x"));

		RowFixture fixture = new RowFixture() {
			@Override
			public Class<?> getTargetClass() {
				return null;
			}

			@Override
			public Object[] query() throws Exception {
				return null;
			}

			@Override
			public void setUp() throws Exception {
				throw new RuntimeException("x");
			}

			@Override
			public void tearDown() throws Exception {
				throw new RuntimeException("x");
			}
		};
		fixture.doTable(table);

		assertThat(fixture.counts().right, is(equalTo((Object) 0)));
		assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 1)));
	}

	@Test
	public void testDownWithErrors() throws Exception {
		Parse table = parseTable();

		ErrorFixture fixture = new ErrorFixture();
		fixture.doTable(table);

		assertThat(fixture.counts().right, is(equalTo((Object) 0)));
		assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 1)));
		assertThat(fixture.isDownCalled(), is(true));
	}
}
