package fit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class FixtureTest {

	@Test
	public void testEscape() {
		assertThat(Fixture.escape("     "), is(equalTo(" &nbsp; &nbsp; ")));
	}
}
