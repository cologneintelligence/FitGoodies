package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.adapters.TypeAdapterHelper;
import de.cologneintelligence.fitgoodies.parsers.LongParserMock;
import de.cologneintelligence.fitgoodies.parsers.ParserHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
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

	public static class TestFixture extends Fixture {
		public int x;
		public String y;

		public int a;
		public String b;
	}

	private static class UpDownSpy extends Fixture {
		public boolean upCalled;
		public boolean downCalled;
		public boolean doRowsCalled;

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
			doRowsCalled = true;
			throw new RuntimeException("");
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
		fixture.check(aCell, TypeAdapter.on(target, new Fixture() {
			@Override
			public Object parse(String s, Class type) throws Exception {
				throw new RuntimeException();
			}
		}, TestClass.class.getField("value")));

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
		fixture.check2(aCell, TypeAdapter.on(target, fixture,
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

		UpDownSpy fixture = new UpDownSpy() {
			@Override
			public void setUp() {
				super.setUp();
				throw new RuntimeException("expected");
			}
		};
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
		} catch (IllegalArgumentException ignored) {
		}

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


	@Test
	public void testGetParameter() throws Exception {
		CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);

		Fixture fixture = new Fixture();
		fixture.setParams(new String[]{
				"x = y", " param = value "
		});

		assertThat(fixture.getArg("x", null), is(equalTo("y")));
		assertThat(fixture.getArg("param", null), is(equalTo("value")));
		assertThat(fixture.getArg("not-good", "good"), is(equalTo("good")));

		fixture = new Fixture();
		fixture.setParams(new String[]{
				"x =z", " a b=test "
		});

		assertThat(fixture.getArg("param", "bad"), is(equalTo("bad")));
		assertThat(fixture.getArg("X", null), is(equalTo("z")));
		assertThat(fixture.getArg("A B", null), is(equalTo("test")));

		fixture = new Fixture();
		fixture.setParams(null);
		assertThat(fixture.getArg("x", "null"), is(equalTo("null")));
		assertThat(fixture.getArg("y", "error"), is(equalTo("error")));

		fixture = new Fixture();
		fixture.setParams(new String[]{
				"y = a${tests.get(x)}b", " a b=test "
		});

		helper.parseBody("${tests.put(x)}", "x");
		assertThat(fixture.getArg("y", null), is(equalTo("axb")));
	}

	@Test
	public void testGetParameters() {
		String[] actual = getArgNamesFromFixture(new String[]{
				"x = y", " param = value "
		});
		assertThat(actual.length, is(equalTo((Object) 2)));
		assertThat(actual[0], is(equalTo("x")));
		assertThat(actual[1], is(equalTo("param")));

		actual = getArgNamesFromFixture(new String[]{
				"x =z", " a b=test "
		});
		assertThat(actual.length, is(equalTo((Object) 2)));
		assertThat(actual[0], is(equalTo("x")));
		assertThat(actual[1], is(equalTo("a b")));

		actual = getArgNamesFromFixture(null);
		assertThat(actual.length, is(equalTo(0)));

		actual = getArgNamesFromFixture(new String[0]);
		assertThat(actual.length, is(equalTo(0)));
	}

	public String[] getArgNamesFromFixture(String[] values) {
		String[] actual;Fixture fixture2 = new Fixture();
		fixture2.setParams(values);
		actual = fixture2.getArgNames();
		return actual;
	}


	@Test
	public void testCopyParamsToFixture() {
		final TypeAdapterHelper taHelper = new TypeAdapterHelper();

		TestFixture fixture = new TestFixture();
		fixture.setParams(new String[]{" x = 8 ", "y=string", "z=error"});

		fixture.a = 9;
		fixture.copyParamsToFixture(fixture, taHelper);
		assertThat(fixture.a, is(equalTo(9)));
		assertThat(fixture.x, is(equalTo(8)));
		assertThat(fixture.y, is(equalTo("string")));

		fixture = new TestFixture();
		fixture.setParams(new String[]{" a = 42 ", "b=c"});
		fixture.copyParamsToFixture(fixture, taHelper);

		assertThat(fixture.a, is(equalTo(42)));
		assertThat(fixture.b, is(equalTo("c")));

		fixture = new TestFixture();
		fixture.setParams(null);
		fixture.copyParamsToFixture(fixture, taHelper);
	}

}
