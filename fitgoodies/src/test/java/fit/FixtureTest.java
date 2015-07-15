package fit;

import junit.framework.*;

public class FixtureTest extends TestCase {

	public FixtureTest(String name) {
		super(name);
	}
	
	public void testEscape() {
		assertEquals(" &nbsp; &nbsp; ", Fixture.escape("     "));
	}
}
