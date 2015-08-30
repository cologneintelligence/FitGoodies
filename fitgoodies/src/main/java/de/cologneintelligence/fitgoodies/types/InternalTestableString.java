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

package de.cologneintelligence.fitgoodies.types;

import java.lang.reflect.Array;
import java.util.regex.Pattern;

public class InternalTestableString extends TestableString {
	public enum TestType {
		EQUALS {
			@Override
			public boolean equals(String given, String compared) {
				return compared.equals(given);
			}
		},

		CONTAINS {
			@Override
			public boolean equals(String given, String compared) {
				return compared.contains(given);
			}
		},

		EQUALSIC {
			@Override
			public boolean equals(String given, String compared) {
				return compared.equalsIgnoreCase(given);
			}
		},

		CONTAINSIC {
			@Override
			public boolean equals(String given, String compared) {
				return compared.toLowerCase().contains(given.toLowerCase());
			}
		},

		STARTSWITH {
			@Override
			public boolean equals(String given, String compared) {
				return compared.startsWith(given);
			}
		},

		STARTSWITHIC {
			public boolean equals(String given, String compared) {
				return compared.toLowerCase().startsWith(given.toLowerCase());
			}
		},

		ENDSWITH {
			@Override
			public boolean equals(String given, String compared) {
				return compared.endsWith(given);
			}
		},

		ENDSWITHIC {
			@Override
			public boolean equals(String given, String compared) {
				return compared.toLowerCase().endsWith(given.toLowerCase());
			}
		},

		REGEX {
			@Override
			public boolean equals(String given, String compared) {
				return Pattern.compile(given).matcher(compared).find();
			}
		},

		REGEXIC {
			@Override
			public boolean equals(String given, String compared) {
				return Pattern.compile(given, Pattern.CASE_INSENSITIVE).matcher(compared).find();
			}
		};

		public abstract boolean equals(String given, String compared);

		public static TestType byName(String testType) {

			if (testType == null) {
				return EQUALS;
			} else {
				testType = testType.replaceAll("[^a-zA-Z0-9]+", "");
			}

			for (TestType type : values()) {
				if (type.name().equalsIgnoreCase(testType)) {
					return type;
				}
			}

			throw new IllegalArgumentException("Unknown test type: " + testType);
		}
	}

	private final TestType testType;


	public InternalTestableString(String content, String testType) {
		this(content, TestType.byName(testType));
	}

	public InternalTestableString(String content, TestType testType) {
		super(content);
		this.testType = testType;
	}

	public int hashCode() {
		return getContent().hashCode();
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object o) {
		if (o == null) {
			return getObject() == null;
		} else if (!(o instanceof TestableString)) {
			return false;
		}

		Object rhs = ((TestableString) o).getObject();
		if (rhs == null) {
			return getObject() == null;
		}

		return equalsInternal(rhs);
	}

	private boolean equalsInternal(Object rhs) {
		if (rhs.getClass().isArray()) {
			for (int i = 0; i < Array.getLength(rhs); ++i) {
				Object o = Array.get(rhs, i);

				if (o instanceof TestableString) {
					if (equals(o)) {
						return true;
					}
				} else if (equalsInternal(o)) {
					return true;
				}
			}
			return false;
		} else {
			return testType.equals(getContent(), rhs.toString());
		}
	}

	public String getContent() {
		return (String) getObject();
	}

	public TestType getTestType() {
		return testType;
	}

	@Override
	public String toString() {
		return getContent() + " (matched as \"" + testType.name() + "\")";
	}
}
