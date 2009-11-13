package de.coin;

import java.net.URL;
import java.net.URLClassLoader;

public class FitGoodiesPluginClassLoader extends URLClassLoader {
	private final ClassLoader parent;
	
	public FitGoodiesPluginClassLoader(URL[] urls) {
		super(urls);
		parent = this.getClass().getClassLoader();
	}

	public Class loadClass(String name) throws ClassNotFoundException {
		Class c;
	
		System.err.println("Searching " + name);
		c = super.findLoadedClass(name);
		if(c == null) {
			System.err.println("Loading " + name);
			c = findAndLoadMissingClass(name);
		}
		return c;
	}

	private Class findAndLoadMissingClass(String name)
			throws ClassNotFoundException {
		try {
			return super.loadClass(name);
		} catch(ClassNotFoundException e) {
			System.err.println("Asking parent for " + name);
			Class clazz = parent.loadClass(name);
			
			return clazz;
		}
	}
}
