package de.cologneintelligence.fitgoodies.htmlparser;

public enum State {
	RIGHT(Constants.CSS_RIGHT),
	WRONG(Constants.CSS_WRONG),
	EXCEPTION(Constants.CSS_EXCEPTION),
	IGNORED(Constants.CSS_IGNORED),
	NONE(null);

	public final String cssClass;

	State(String cssClass) {
		this.cssClass = cssClass;
	}
}
