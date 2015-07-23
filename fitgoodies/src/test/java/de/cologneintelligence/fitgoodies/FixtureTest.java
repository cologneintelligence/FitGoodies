package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.parsers.LongParserMock;
import de.cologneintelligence.fitgoodies.parsers.ParserHelper;
import de.cologneintelligence.fitgoodies.test.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FitUtils;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class FixtureTest extends FitGoodiesTestCase {

	public static class TestClass {
		public String value;

		public String error() {
			throw new RuntimeException("expected!");
		}

		public String noError() {
			return "a result!";
		}

		private String privateMethod() {
			return "a result!";
		}
	}

	public static class BrokenParserFixture extends Fixture {

		@Override
		public Object parse(String s, Class type) throws Exception {
			throw new RuntimeException();
		}
	}

	private static class UpDownSpy extends Fixture {
		public boolean upCalled;
		public boolean downCalled;

		@Override
		public void setUp() {
			upCalled = true;
		}

		@Override
		public void tearDown() {
			downCalled = true;
		}

		@Override
		protected void doRows(Parse rows) {
			throw new RuntimeException("");
		}
	}


	private static class ErrorUpDownSpy extends UpDownSpy {
		public boolean doRowsCalled;

		@Override
		protected void doRows(Parse rows) {
			doRowsCalled = true;
			super.doRows(rows);
		}

		@Override
		public void setUp() {
			super.setUp();
			throw new RuntimeException("expected");
		}
	}

	@Test
	public void testEscape() {
		assertThat(FitUtils.escape("     "), is(equalTo(" &nbsp; &nbsp; ")));

		String junk = "!@#$%^*()_-+={}|[]\\:\";',./?`";
		assertThat(FitUtils.escape(junk), is(equalTo(junk)));
		assertThat(FitUtils.escape(""), is(equalTo("")));
		assertThat(FitUtils.escape("<"), is(equalTo("&lt;")));
		assertThat(FitUtils.escape("<<"), is(equalTo("&lt;&lt;")));
		assertThat(FitUtils.escape("x<"), is(equalTo("x&lt;")));
		assertThat(FitUtils.escape("&"), is(equalTo("&amp;")));
		assertThat(FitUtils.escape("<&<"), is(equalTo("&lt;&amp;&lt;")));
		assertThat(FitUtils.escape("&<&"), is(equalTo("&amp;&lt;&amp;")));
		assertThat(FitUtils.escape("a < b && c < d"), is(equalTo("a &lt; b &amp;&amp; c &lt; d")));
		assertThat(FitUtils.escape("a\nb"), is(equalTo("a<br />b")));
	}

	@Test
	public void testCheckFieldRight() throws Exception {
		final String value = "value";
		Parse aCell = parseTd(value);

		Counts counts = checkFieldWithContent(value, aCell);
		assertCounts(counts, 1, 0, 0, 0);
	}

	private void assertCounts(Counts counts, int right, int wrong, int exceptions, int ignores) {
		assertThat(counts.right, is(right));
		assertThat(counts.wrong, is(wrong));
		assertThat(counts.exceptions, is(exceptions));
		assertThat(counts.ignores, is(ignores));
	}

	@Test
	public void testCheckFieldWrong() throws Exception {
		final String value = "value";
		Parse aCell = parseTd("another value");

		Counts counts = checkFieldWithContent(value, aCell);

		assertCounts(counts, 0, 1, 0, 0);
	}

	@Test
	public void testCheckFieldException() throws Exception {
		Parse aCell = parseTd("another value");

		final Fixture fixture = new Fixture();
		TestClass target = new TestClass();
		fixture.check(aCell, TypeAdapter.on(target, new BrokenParserFixture(), TestClass.class.getField("value")));

		assertCounts(fixture.counts(), 0, 0, 1, 0);
	}

	@Test
	public void testCheckFieldEmpty() throws Exception {
		Parse aCell = parseTd("");

		Counts counts = checkFieldWithContent("print me!", aCell);

		assertCounts(counts, 0, 0, 0, 0);
		assertThat(aCell.text(), is(equalTo("print me!")));
	}

	@Test
	public void testCheckFieldEmptyWithError() throws Exception {
		Parse aCell = parseTd("");

		final Fixture fixture = checkWithoutTypeAdapter(aCell);

		assertCounts(fixture.counts(), 0, 0, 0, 0);
		assertThat(aCell.text(), containsString("error"));
	}

	@Test
	public void testCheckFieldWithoutTypeAdapter() throws Exception {
		Parse aCell = parseTd("try me");

		final Fixture fixture = checkWithoutTypeAdapter(aCell);

		assertCounts(fixture.counts(), 0, 0, 0, 1);
		assertThat(aCell.text(), is(equalTo("try me")));
	}

	@Test
	public void testCheckForException() throws Exception {
		Parse aCell = parseTd("error");

		Counts counts = checkInvocation(aCell, "error");

		assertCounts(counts, 1, 0, 0, 0);
		assertThat(aCell.text(), is(equalTo("error")));
	}

	@Test
	public void testCheckForExceptionFail() throws Exception {
		Parse aCell = parseTd("error");

		Counts counts = checkInvocation(aCell, "noError");

		assertCounts(counts, 0, 1, 0, 0);
		assertThat(aCell.text(), is(equalTo("error expecteda result! actual")));
	}

	public Counts checkInvocation(Parse aCell, String methodName) throws NoSuchMethodException {
		final Fixture fixture = new Fixture();
		TestClass target = new TestClass();
		fixture.check2(aCell, TypeAdapter.on(target, new BrokenParserFixture(),
				TestClass.class.getMethod(methodName, new Class<?>[0])));
		return fixture.counts();
	}

	public Counts checkFieldWithContent(String value, Parse aCell) throws NoSuchFieldException {
		final Fixture fixture = new Fixture();
		TestClass target = new TestClass();
		target.value = value;
		fixture.check2(aCell, TypeAdapter.on(target, fixture, TestClass.class.getField("value")));
		return fixture.counts();
	}

	public Fixture checkWithoutTypeAdapter(Parse aCell) {
		final Fixture fixture = new Fixture();
		fixture.check2(aCell, null);
		return fixture;
	}

	@Test
	public void upAndDownIsCalledEvenOnErrors() {
		Parse table = parseTable(
				tr("number", "n()"),
				tr("1", "1"));

		UpDownSpy fixture = new UpDownSpy();
		fixture.doTable(table);

		assertTableException(fixture);
		assertThat(fixture.upCalled, is(true));
		assertThat(fixture.downCalled, is(true));
	}

	@Test
	public void downIsNotCalledOnUpErrors() throws Exception {
		final Parse table = parseTable(tr("x"));

		ErrorUpDownSpy fixture = new ErrorUpDownSpy();
		fixture.doTable(table);

		assertTableException(fixture);
		assertThat(fixture.upCalled, is(true));
		assertThat(fixture.doRowsCalled, is(false));
		assertThat(fixture.downCalled, is(false));
	}

	public void assertTableException(Fixture fixture) {
		assertThat(fixture.counts().right, is(equalTo((Object) 0)));
		assertThat(fixture.counts().wrong, is(equalTo((Object) 0)));
		assertThat(fixture.counts().exceptions, is(equalTo((Object) 1)));
	}

	@Test
	public void testParse() throws Exception {
		Fixture fixture = new Fixture();

		try {
			final Integer intExpected = 42;
			assertThat(fixture.parse("42", intExpected.getClass()), is(nullValue()));
			Assert.fail();
		} catch(IllegalArgumentException ignored) {}

		BigInteger biExpected = new BigInteger("123");
		assertThat(fixture.parse("123", biExpected.getClass()), (Matcher) is(equalTo(biExpected)));
		biExpected = new BigInteger("7");
		assertThat(fixture.parse("7", biExpected.getClass()), (Matcher) is(equalTo(biExpected)));

		BigDecimal bdExpected = new BigDecimal("312.45");
		assertThat(fixture.parse("312.45", bdExpected.getClass()), (Matcher) is(equalTo(bdExpected)));
		bdExpected = new BigDecimal("331.0");
		assertThat(fixture.parse("331.0", bdExpected.getClass()), (Matcher) is(equalTo(bdExpected)));

		final ParserHelper helper = DependencyManager.getOrCreate(ParserHelper.class);
		helper.registerParser(new LongParserMock());
		assertThat(fixture.parse("5", Long.class), (Matcher) is(equalTo(2L)));

		fixture.setCellParameter("true");
		assertThat(fixture.parse("5", Long.class), (Matcher) is(equalTo(7L)));
	}
}
