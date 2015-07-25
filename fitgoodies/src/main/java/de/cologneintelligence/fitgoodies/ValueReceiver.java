package de.cologneintelligence.fitgoodies;

import java.lang.reflect.InvocationTargetException;


// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

import java.lang.reflect.Field;
import java.lang.reflect.Method;

// FIXME: split in 2 classes: MethodTypeAdapter and FieldTypeAdapter
public abstract class ValueReceiver {
	public abstract Object get() throws IllegalAccessException, InvocationTargetException;
	public abstract Class<?> getType();
	public abstract void set(Object target, Object fieldValue) throws IllegalAccessException;
	public abstract boolean canSet();

	public static ValueReceiver on(final Object target, final Field field) {
		return new ValueReceiver() {
			@Override
			public Object get() throws IllegalAccessException {
				return field.get(target);
			}

			@Override
			public Class<?> getType() {
				return field.getType();
			}

			@Override
			public void set(Object target, Object fieldValue) throws IllegalAccessException {
				field.set(target, fieldValue);
			}

			@Override
			public boolean canSet() {
				return true;
			}
		};
	}

	public static ValueReceiver on(final Object target, final Method method) {
		return new ValueReceiver() {
			@Override
			public Object get() throws IllegalAccessException, InvocationTargetException {
				return method.invoke(target);
			}

			@Override
			public Class<?> getType() {
				return method.getReturnType();
			}

			@Override
			public void set(Object target, Object fieldValue) throws IllegalAccessException {
				throw new UnsupportedOperationException("Cannot set value on method");
			}

			@Override
			public boolean canSet() {
				return false;
			}
		};
	}

}

/*
class Backup {
	public Fixture fixture;


	// Factory //////////////////////////////////

	public static ValueReceiver on(Object target, Fixture fixture, Class type) {
		ValueReceiver a = adapterFor(type);
		a.init(target, fixture, type);
		return a;
	}

	public static ValueReceiver on(Object target, Fixture fixture, Field field) {
		ValueReceiver a = on(target, fixture, field.getType());
		a.field = field;
		return a;
	}

	public static ValueReceiver on(Object target, Fixture fixture, Method method) {
		ValueReceiver a = on(target, fixture, method.getReturnType());
		a.method = method;
		return a;
	}

	public static ValueReceiver adapterFor(Class type) throws UnsupportedOperationException {
		if (type.isPrimitive()) {

			if (type.equals(byte.class)) return new ByteAdapter();
			if (type.equals(short.class)) return new ShortAdapter();
			if (type.equals(int.class)) return new IntAdapter();
			if (type.equals(long.class)) return new LongAdapter();
			if (type.equals(float.class)) return new FloatAdapter();
			if (type.equals(double.class)) return new DoubleAdapter();
			if (type.equals(char.class)) return new CharAdapter();
			if (type.equals(boolean.class)) return new BooleanAdapter();
			throw new UnsupportedOperationException("can't yet adapt " + type);
		} else {
			if (type.equals(Byte.class)) return new ClassByteAdapter();
			if (type.equals(Short.class)) return new ClassShortAdapter();
			if (type.equals(Integer.class)) return new ClassIntegerAdapter();
			if (type.equals(Long.class)) return new ClassLongAdapter();
			if (type.equals(Float.class)) return new ClassFloatAdapter();
			if (type.equals(Double.class)) return new ClassDoubleAdapter();
			if (type.equals(Character.class)) return new ClassCharacterAdapter();
			if (type.equals(Boolean.class)) return new ClassBooleanAdapter();
			if (type.isArray()) return new ArrayAdapter();
			return new ValueReceiver();
		}
	}


	// Accessors ////////////////////////////////

	protected void init(Object target, Fixture fixture, Class type) {
		this.target = target;
		this.fixture = fixture;
		this.type = type;
	}

	public Object get() throws IllegalAccessException, InvocationTargetException {
		if (field != null) {
			return field.get(target);
		}
		if (method != null) {
			return invoke();
		}
		return null;
	}

	public void set(Object value) throws IllegalAccessException {
		field.set(target, value);
	}

	public Object invoke() throws IllegalAccessException, InvocationTargetException {
		Object params[] = {};
		return method.invoke(target, params);
	}

	public Object parse(String s) throws Exception {
		return fixture.parse(s, type);
	}

	public boolean equals(Object a, Object b) {
		if (a == null) {
			return b == null;
		}
		return a.equals(b);
	}

	public String toString(Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString();
	}


	// Subclasses ///////////////////////////////

	static class ByteAdapter extends ClassByteAdapter {
		public void set(Object i) throws IllegalAccessException {
			field.setByte(target, (Byte) i);
		}
	}

	static class ClassByteAdapter extends ValueReceiver {
		public Object parse(String s) {
			return Byte.parseByte(s);
		}
	}

	static class ShortAdapter extends ClassShortAdapter {
		public void set(Object i) throws IllegalAccessException {
			field.setShort(target, (Short) i);
		}
	}

	static class ClassShortAdapter extends ValueReceiver {
		public Object parse(String s) {
			return Short.parseShort(s);
		}
	}

	static class IntAdapter extends ClassIntegerAdapter {
		public void set(Object i) throws IllegalAccessException {
			field.setInt(target, (Integer) i);
		}
	}

	static class ClassIntegerAdapter extends ValueReceiver {
		public Object parse(String s) {
			return Integer.parseInt(s);
		}
	}

	static class LongAdapter extends ClassLongAdapter {
		public void set(Object i) throws IllegalAccessException {
			field.setLong(target, (Long) i);
		}
	}

	static class ClassLongAdapter extends ValueReceiver {
		public Object parse(String s) {
			return Long.parseLong(s);
		}
	}

	static class FloatAdapter extends ClassFloatAdapter {
		public void set(Object i) throws IllegalAccessException {
			field.setFloat(target, ((Number) i).floatValue());
		}

		public Object parse(String s) {
			return Float.parseFloat(s);
		}
	}

	static class ClassFloatAdapter extends ValueReceiver {
		public Object parse(String s) {
			return Float.parseFloat(s);
		}
	}

	static class DoubleAdapter extends ClassDoubleAdapter {
		public void set(Object i) throws IllegalAccessException {
			field.setDouble(target, ((Number) i).doubleValue());
		}

		public Object parse(String s) {
			return Double.parseDouble(s);
		}
	}

	static class ClassDoubleAdapter extends ValueReceiver {
		public Object parse(String s) {
			return Double.parseDouble(s);
		}
	}

	static class CharAdapter extends ClassCharacterAdapter {
		public void set(Object i) throws IllegalAccessException {
			field.setChar(target, (Character) i);
		}
	}

	static class ClassCharacterAdapter extends ValueReceiver {
		public Object parse(String s) {
			return s.charAt(0);
		}
	}

	static class BooleanAdapter extends ClassBooleanAdapter {
		public void set(Object i) throws IllegalAccessException {
			field.setBoolean(target, (Boolean) i);
		}
	}

	static class ClassBooleanAdapter extends ValueReceiver {
		public Object parse(String s) {
			return Boolean.valueOf(s);
		}
	}

	static class ArrayAdapter extends ValueReceiver {
		Class componentType;
		ValueReceiver componentAdapter;

		protected void init(Object target, Fixture fixture, Class type) {
			super.init(target, fixture, type);
			componentType = type.getComponentType();
			componentAdapter = on(target, fixture, componentType);
		}

		public Object parse(String s) throws Exception {
			StringTokenizer t = new StringTokenizer(s, ",");
			Object array = Array.newInstance(componentType, t.countTokens());
			for (int i = 0; t.hasMoreTokens(); i++) {
				Array.set(array, i, componentAdapter.parse(t.nextToken().trim()));
			}
			return array;
		}

		public String toString(Object o) {
			if (o == null) return "";
			int length = Array.getLength(o);
			StringBuilder b = new StringBuilder(5 * length);
			for (int i = 0; i < length; i++) {
				b.append(componentAdapter.toString(Array.get(o, i)));
				if (i < (length - 1)) {
					b.append(", ");
				}
			}
			return b.toString();
		}

		public boolean equals(Object a, Object b) {
			int length = Array.getLength(a);
			if (length != Array.getLength(b)) return false;
			for (int i = 0; i < length; i++) {
				if (!componentAdapter.equals(Array.get(a, i), Array.get(b, i))) return false;
			}
			return true;
		}
	}
}
*/
