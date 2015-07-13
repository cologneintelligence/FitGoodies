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


package de.cologneintelligence.fitgoodies.dynamic;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.Type;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

/**
 * Factory that allows to generate java classes on the fly.
 * The class will have a public constructor and only public variables.
 * <p>
 *
 * This class is primary written for internal use.
 *
 */
public class DynamicObjectFactory {
	/**
	 * Class loader which is able to load the dynamic generated class.
	 *
	 * @author jwierum
	 */
	public static class JavaClassClassloader extends java.lang.ClassLoader {
		/**
		 * Default constructor. Creates a new {@code ClassLoader}.
		 */
		public JavaClassClassloader() {
			super(JavaClassClassloader.class.getClassLoader());
		}

		@Override
		public final Class<?> loadClass(final String name)
				throws ClassNotFoundException {
			if (cache.containsKey(name)) {
				return cache.get(name);
			} else {
				return super.loadClass(name);
			}
		}

		private final HashMap<String, Class<?>> cache = new HashMap<>();

		/**
		 * Loads the byte code of {@code javaClass}, defines a class
		 * and resolves it. The loaded class is returned.
		 * @param name class name
		 * @param javaClass {@code JavaClass} object which holds the
		 * 		dynamically generated class.
		 * @return the loaded class
		 */
		public final Class<?> loadJavaClass(final String name,
				final JavaClass javaClass) {
			byte[] binClass = javaClass.getBytes();

			Class<?> c = defineClass(javaClass.getClassName(),
					binClass, 0, binClass.length);

			resolveClass(c);
			cache.put(name, c);
			return c;
		}
	}

	private final ClassGen cg;
	private Class<?> result;
	private static int classCount = 1;
	private static JavaClassClassloader loader;

	static {
		loader = AccessController.doPrivileged(
				new PrivilegedAction<JavaClassClassloader>() {
			@Override
			public JavaClassClassloader run() {
				return new JavaClassClassloader();
			}
		});
	}

	/**
	 * Default constructor. Prepares a new class.
	 */
	public DynamicObjectFactory() {
		cg = new ClassGen(
			"$DynamicGeneratedObject$" + classCount,
			"java.lang.Object", "<generated>",
			Constants.ACC_PUBLIC | Constants.ACC_SUPER, null);
		++classCount;
		cg.addEmptyConstructor(Constants.ACC_PUBLIC);
	}

	/**
	 * Adds a public field to the constructed class.
	 *
	 * @param type type of the field
	 * @param name name of the field
	 * @throws ClassNotFoundException indicates a problem with {@code type}
	 */
	public void add(final Class<?> type, final String name) throws ClassNotFoundException {
		FieldGen fg;

		if (result != null) {
			throw new IllegalStateException("Class already generated");
		}

		fg = new FieldGen(Constants.ACC_PUBLIC | Constants.ACC_SUPER,
				Type.getType(type), name, cg.getConstantPool());
		cg.addField(fg.getField());
	}

	/**
	 * Compiles the class and returns a class object which contains all
	 * added fields.
	 * @return dynamic generated class with all added fields.
	 */
	public final Class<?> compile() {
		if (result == null) {
			loader.loadJavaClass(cg.getClassName(), cg.getJavaClass());
			try {
				result = loader.loadClass(cg.getClassName());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
}
