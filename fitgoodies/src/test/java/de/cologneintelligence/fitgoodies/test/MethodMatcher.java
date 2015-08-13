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

package de.cologneintelligence.fitgoodies.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;

public class MethodMatcher extends BaseMatcher<Method> {

	private String methodName;

	public MethodMatcher(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public boolean matches(Object item) {
		return item instanceof Method && ((Method) item).getName().equals(methodName);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("a method with name " + methodName);
	}

	public static Matcher<Method> aMethodNamed(String name) {
		return new MethodMatcher(name);
	}
}
