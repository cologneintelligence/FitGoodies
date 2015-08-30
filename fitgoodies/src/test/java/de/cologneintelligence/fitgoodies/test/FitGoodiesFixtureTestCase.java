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

import de.cologneintelligence.fitgoodies.Fixture;
import de.cologneintelligence.fitgoodies.Validator;
import de.cologneintelligence.fitgoodies.htmlparser.FitCell;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandler;
import de.cologneintelligence.fitgoodies.typehandler.TypeHandlerFactory;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.valuereceivers.ConstantReceiver;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiver;
import de.cologneintelligence.fitgoodies.valuereceivers.ValueReceiverFactory;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;

import java.text.ParseException;
import java.util.*;

import static de.cologneintelligence.fitgoodies.test.ConstantReceiverMatcher.constantReceiverThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public abstract class FitGoodiesFixtureTestCase<T extends Fixture> extends FitGoodiesTestCase {

    protected static abstract class Task {
        abstract public void run() throws Exception;
    }

    @Mock
    protected Validator validator;

    @Mock
    protected ValueReceiverFactory valueReceiverFactory;

    @Mock
    protected TypeHandlerFactory typeHandlerFactory;

    protected List<Task> expectations;

    private Map<String, ValueReceiver> receiverCache;
    private Map<String, TypeHandler> typeHandlerCache;

    protected T fixture;

    @Before
    public void prepareFixture() throws Exception {
        DependencyManager.inject(Validator.class, validator);
        DependencyManager.inject(ValueReceiverFactory.class, valueReceiverFactory);
        DependencyManager.inject(TypeHandlerFactory.class, typeHandlerFactory);

        expectations = new LinkedList<>();
        receiverCache = new HashMap<>();
        typeHandlerCache = new HashMap<>();

        T result;
        try {
            result = newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Assert.fail("Cannot instantiate fixture " + getFixtureClass().getName() + ": " + e.getCause().getMessage());
            result = null;
        }
        fixture = result;
    }

    protected T newInstance() throws InstantiationException, IllegalAccessException {
        return getFixtureClass().newInstance();
    }

    @After
    public void runExpectations() throws Exception {
        for (Task expectation : expectations) {
            expectation.run();
        }

        if (expectations.size() > 0) {
            verify(validator, atLeast(0)).preProcess(any(FitCell.class));
            verify(validator, atLeast(0)).preProcess(any(String.class));
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(valueReceiverFactory);
        }
    }

    abstract protected Class<T> getFixtureClass();

    public void run() {
        fixture.doTable(lastFitTable);
    }

    protected void preparePreprocess(final String input, String output) {
        when(validator.preProcess(input)).thenReturn(output);
    }

    protected void preparePreprocess(final FitCell cell, String output) {
        when(validator.preProcess(cell)).thenReturn(output);
    }

    protected void expectMethodValidation(final int row, final int col,
                                          final String method) throws Exception {

        final ValueReceiver valueReceiver = expectValueReceiverCreation(method);

        expectations.add(new Task() {
            @Override
            public void run() {
                verify(validator).process(
                    cellThat(row, col),
                    argThatSame(valueReceiver),
                    argThat(nullValue(String.class)),
                    argThatSame(typeHandlerFactory));
            }
        });
    }

    protected void expectFieldSet(final int row, final int col, final Object target, String field, final Object value) throws Exception {
        final ValueReceiver valueReceiver = expectFieldValueReceiverCreation(field, value.getClass());
        TypeHandler typeHandler = prepareGetTypeHandler(valueReceiver.getType(), null);

        @SuppressWarnings("RedundantStringConstructorCall")
        final String s = new String();
        when(validator.preProcess(cellThat(row, col))).thenReturn(s);
        when(typeHandler.parse(argThatSame(s))).thenReturn(value);

        expectations.add(new Task() {
            @Override
            public void run() throws IllegalAccessException {
                verify(valueReceiver).set(argThatSame(target), argThatSame(value));
            }
        });
    }

    protected TypeHandler prepareGetTypeHandler(Class type, String parameter) {
        TypeHandler typeHandler;

        String key = type.getName() + "-" + Objects.toString(parameter);
        if (!typeHandlerCache.containsKey(key)) {
            typeHandler = mock(TypeHandler.class);
            when(typeHandlerFactory.getHandler(type, parameter)).thenReturn(typeHandler);
            when(typeHandler.getType()).thenReturn(type);
            typeHandlerCache.put(key, typeHandler);
        } else {
            typeHandler = typeHandlerCache.get(key);
        }
        return typeHandler;
    }

    protected ValueReceiver expectFieldValueReceiverCreation(String field, Class type) throws Exception {
        return prepareFieldValueReceiverCreation(field, type, true);
    }

    protected ValueReceiver prepareFieldValueReceiverCreation(String field, Class type) throws Exception {
        return prepareFieldValueReceiverCreation(field, type, false);
    }

    protected ValueReceiver prepareFieldValueReceiverCreation(final String field, Class type, boolean expect) throws Exception {
        final ValueReceiver valueReceiver;
        if (!receiverCache.containsKey(field)) {
            valueReceiver = mock(ValueReceiver.class, "mock for ValueReceiver(" + field + ")");
            receiverCache.put(field, valueReceiver);

            when(valueReceiver.getType()).thenReturn(type);
            when(valueReceiverFactory.createReceiver(any(Object.class), argThat(is(equalTo(field)))))
                .thenReturn(valueReceiver);
            when(valueReceiver.canSet()).thenReturn(true);
        } else {
            valueReceiver = receiverCache.get(field);
        }


        if (expect) {
            expectations.add(new Task() {
                @Override
                public void run() throws Exception {
                    verify(valueReceiverFactory, atLeastOnce()).createReceiver(any(Object.class),
                        argThat(is(equalTo(field))));
                }
            });
        }

        return valueReceiver;
    }

    protected ValueReceiver expectValueReceiverCreation(final String method) throws Exception {
        ValueReceiver valueReceiver;
        if (!receiverCache.containsKey(method + "()")) {
            valueReceiver = mock(ValueReceiver.class, "mock for ValueReceiver(" + method + ")");
            receiverCache.put(method + "()", valueReceiver);

            when(valueReceiverFactory.createReceiver(any(Object.class),
                argThat(is(anyOf(equalTo(method + "()"), equalTo(method + "?"))))))
                .thenReturn(valueReceiver);

            when(valueReceiverFactory.createReceiver(any(Object.class),
                argThat(MethodMatcher.aMethodNamed(method))))
                .thenReturn(valueReceiver);

            when(valueReceiver.canSet()).thenReturn(false);


            expectations.add(new Task() {
                @Override
                public void run() throws Exception {
                    verify(valueReceiverFactory, atLeast(0)).createReceiver(any(Object.class),
                        argThat(MethodMatcher.aMethodNamed(method)));
                    verify(valueReceiverFactory, atLeast(0)).createReceiver(any(Object.class),
                        argThat(is(anyOf(equalTo(method + "()"), equalTo(method + "?")))));
                }
            });
        } else {
            valueReceiver = receiverCache.get(method + "()");
        }

        return valueReceiver;
    }

    protected ValueReceiver expectValueReceiverCreation(final Object object, final String fieldOrMethod) throws Exception {
        ValueReceiver valueReceiver;

        String key = String.format("%s@%x", fieldOrMethod, object.hashCode());
        if (!receiverCache.containsKey(key)) {
            valueReceiver = mock(ValueReceiver.class, "mock for ValueReceiver(" + key + ")");
            receiverCache.put(key, valueReceiver);

            when(valueReceiverFactory.createReceiver(object, fieldOrMethod))
                .thenReturn(valueReceiver);

            expectations.add(new Task() {
                @Override
                public void run() throws Exception {
                    verify(valueReceiverFactory, atLeastOnce()).createReceiver(object, fieldOrMethod);
                }
            });
        } else {
            valueReceiver = receiverCache.get(key);
        }

        return valueReceiver;
    }

    protected <S> S argThatSame(S object) {
        return argThat(sameInstance(object));
    }

    protected FitCell cellThat(final int row, final int col) {
        return argThat(CellAtMatcher.cellAt(lastFitTable, row, col));
    }

    protected void expectParameterApply(String fieldName, final String text, final Object result) throws Exception {
        prepareParameterApply(fieldName, text, result, true);
    }

    protected void prepareParameterApply(String fieldName, final String text, final Object result) throws Exception {
        prepareParameterApply(fieldName, text, result, false);
    }

    private void prepareParameterApply(String fieldName, final String text, final Object result, boolean expect) throws Exception {
        @SuppressWarnings("RedundantStringConstructorCall")
        final String s = new String();

        final ValueReceiver valueReceiver = prepareFieldValueReceiverCreation(fieldName, result.getClass(), expect);

        final TypeHandler typeHandler = prepareGetTypeHandler(valueReceiver.getType(), null);
        when(validator.preProcess(text)).thenReturn(s);
        when(typeHandler.parse(argThatSame(s))).thenReturn(result);

        if (expect) {
            expectations.add(new Task() {
                @Override
                public void run() throws Exception {
                    verify(validator, atLeastOnce()).preProcess(text);
                    verify(typeHandler, atLeastOnce()).parse(argThatSame(s));
                }
            });
        }
    }

    protected void prepareNonExistingArg(String fieldName) {
        try {
            when(valueReceiverFactory.createReceiver(fixture, fieldName)).thenThrow(new NoSuchFieldException(fieldName));
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    protected <S> void preparePreprocessWithConversion(Class<S> clazz, final String text, final S result) {
        @SuppressWarnings("RedundantStringConstructorCall")
        final String s = new String();

        TypeHandler typeHandler = prepareGetTypeHandler(clazz, null);
        when(validator.preProcess(text)).thenReturn(s);

        try {
            when(typeHandler.parse(argThat(sameInstance(s)))).thenReturn(result);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected <S> void preparePreprocessWithConversionError(Class<S> clazz, final String text) {
        @SuppressWarnings("RedundantStringConstructorCall")
        final String s = new String();

        TypeHandler typeHandler = prepareGetTypeHandler(clazz, null);
        when(validator.preProcess(text)).thenReturn(s);

        try {
            when(typeHandler.parse(argThat(sameInstance(s)))).thenThrow(new ParseException("expected", 0));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected void expectConstantValidation(final int row, final int col, final String text) {
        expectations.add(new Task() {
            @Override
            public void run() throws Exception {
                verify(validator).process(cellThat(row, col),
                    constantThat(equalTo(text)),
                    argThat(nullValue(String.class)),
                    argThatSame(typeHandlerFactory));

            }
        });
    }

    private ConstantReceiver constantThat(final Matcher<?> expected) {
        return argThat(constantReceiverThat(expected));
    }

}
