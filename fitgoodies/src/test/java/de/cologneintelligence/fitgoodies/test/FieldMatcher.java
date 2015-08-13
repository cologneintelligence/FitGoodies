/*
 * Copyright (c) 2009-2014  Cologne Intelligence GmbH
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

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

import java.lang.reflect.Field;

public class FieldMatcher extends DiagnosingMatcher<Object> {
    private final String fieldName;
    private final Matcher matcher;

    public FieldMatcher(String fieldName, Matcher matcher) {
        this.fieldName = fieldName;
        this.matcher = matcher;
    }

    public static FieldMatcher hasField(String name, Matcher matcher) {
        return new FieldMatcher(name, matcher);
    }

    @Override
    public boolean matches(final Object item, Description mismatchDescription) {
        try {
            final Field fieldItem = item.getClass().getField(fieldName);
            final Object fieldObjectItem = fieldItem.get(item);

            if (matcher.matches(fieldObjectItem)) {
                return true;
            }

            matcher.describeMismatch(fieldObjectItem, mismatchDescription);

        } catch (IllegalAccessException e) {
            mismatchDescription.appendText("is inaccessible");
        } catch (NoSuchFieldException e) {
            mismatchDescription.appendText("doesn't exist");
        }

        return false;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Field " + fieldName + " that ");
        matcher.describeTo(description);
    }
}
