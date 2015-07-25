/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
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

package de.cologneintelligence.fitgoodies.typehandler;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public final class TypeHandlerFactory {
    private final Map<Class<?>, Class<? extends TypeHandler<?>>> adapters = new HashMap<>();

    public TypeHandlerFactory() {
        register(BigDecimalTypeHandler.class);
        register(BigIntegerTypeHandler.class);
        register(BooleanTypeHandler.class);
        register(ByteTypeHandler.class);
        register(CharTypeHandler.class);
        register(DateTypeHandler.class);
        register(DoubleTypeHandler.class);
        register(FloatTypeHandler.class);
        register(IntTypeHandler.class);
        register(LongTypeHandler.class);
        register(ObjectTypeHandler.class);
        register(ScientificDoubleTypeHandler.class);
        register(ShortTypeHandler.class);
        register(SqlDateTypeHandler.class);
        register(SqlTimestampTypeHandler.class);
        register(StringBufferTypeHandler.class);
        register(StringBuilderTypeHandler.class);
        register(StringTypeHandler.class);

        // FIXME
        adapters.put(Boolean.TYPE, BooleanTypeHandler.class);
        adapters.put(Character.TYPE, CharTypeHandler.class);
        adapters.put(Byte.TYPE, ByteTypeHandler.class);
        adapters.put(Double.TYPE, DoubleTypeHandler.class);
        adapters.put(Float.TYPE, FloatTypeHandler.class);
        adapters.put(Integer.TYPE, IntTypeHandler.class);
        adapters.put(Long.TYPE, LongTypeHandler.class);
        adapters.put(Short.TYPE, LongTypeHandler.class);
    }

    /**
     * Returns a new instance of <class>typeAdapter</class>, passing
     * <class>baseTypeAdapter</class> as the only constructor argument.
     *
     * @param typeAdapter subclass of {@link TypeHandler}
     * 		which is used to create the new adapter.
     * @param parameter column/row parameter
     * @return an instance of {@code typeAdapter}
     */
    private TypeHandler<?> instantiate(Class<? extends TypeHandler<?>> typeAdapter, String parameter) {
        try {
            final Constructor<? extends TypeHandler<?>> constructor = typeAdapter.getConstructor(String.class);
            return constructor.newInstance(parameter);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Instantiates a new {@code TypeAdapter} which is capable of handling
     * {@code type}.
     *
     * If no such adapter exists, the old one is returned.
     *
     * @param type {@code Class} to process
     * @param parameter Column/row parameter
     * @return an instance of {@code TypeAdapter}, either a specialized one,
     * 		or {@code typeAdapter} itself.
     */
    public TypeHandler getHandler(Class<?> type, String parameter) {
        if (type == null) {
            return null;
        } else if (type.isArray()) {
            return new ArrayTypeHandler(parameter, getHandler(type.getComponentType(), parameter));
        } else if (adapters.containsKey(type)) {
            return instantiate(adapters.get(type), parameter);
        } else {
            throw new IllegalArgumentException("Unknown type: " + type.getName());
        }
    }

    /**
     * Registers a new {@code AbstractTypeAdapter}.
     *
     * After registering an adapter, {@link #getHandler(Class, String)}
     * is able to return it.
     * @param adapterClass subclass of {@code AbstractTypeAdapter} to register
     */
    public void register(Class<? extends TypeHandler<?>> adapterClass) {
        final Class<?> targetType = ((TypeHandler<?>) instantiate(adapterClass, null)).getType();
        adapters.put(targetType, adapterClass);
    }
}
