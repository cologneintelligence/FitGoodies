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

package de.cologneintelligence.fitgoodies.valuereceivers;

import de.cologneintelligence.fitgoodies.util.FitUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueReceiverFactory {
	public static Pattern METHOD_PATTERN = Pattern.compile("(.*)(?:\\(\\)|\\?)");

	public ValueReceiver createReceiver(Object target, Method method) {
		return new MethodValueReceiver(method, target);
	}

	public ValueReceiver createReceiver(Object target, Field field) {
		return new FieldValueReceiver(field, target);
	}

	public ValueReceiver createReceiver(Object target, String name) throws NoSuchMethodException, NoSuchFieldException {
		ValueReceiver receiver;
		if (name.equals("")) {
			receiver = null;
		} else {
			Matcher matcher = METHOD_PATTERN.matcher(name);

			if (matcher.find()) {
				final String methodName = FitUtils.camel(matcher.group(1));
				final Method method = target.getClass().getMethod(methodName);
				receiver = createReceiver(target, method);
			} else {
				final Field field = target.getClass().getField(FitUtils.camel(name));
				receiver = createReceiver(target, field);
			}
		}
		return receiver;
	}
}
