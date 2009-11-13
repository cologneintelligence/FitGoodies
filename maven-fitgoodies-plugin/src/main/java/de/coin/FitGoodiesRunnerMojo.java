package de.coin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @goal run
 * 
 * @phase process-sources
 * @requiresDependencyResolution runtime
 */
public class FitGoodiesRunnerMojo extends AbstractMojo {
	/**
	 * Target directory where the report files are written.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private String outputDirectory;

	/**
	 * Input directory where the fixtures are found.
	 * @parameter 
	 * @required
	 */
	private String sourceDirectory;

	File test;
	/**
	 * Encoding. default is set to utf-8.
	 * @parameter default-value="utf-8"
	 * 
	 */
	private String fileEncoding;

	/**
	* The directory containing generated test classes of the project being tested.
	* 
	* @parameter expression="${project.build.outputDirectory}"
	* @required
	*/
	private File classesDirectory;

	public void execute() throws MojoExecutionException {
// todo: implement http://maven.apache.org/plugins/maven-surefire-plugin/xref/org/apache/maven/plugin/surefire/SurefirePlugin.html
		// http://maven.apache.org/guides/mini/guide-maven-classloading.html
	
		ClassLoader loader = makeClassLoader();

		Class directoryRunner = getDirectoryRunnerClass(loader);
		
		String[] args = new String[]{sourceDirectory, outputDirectory, fileEncoding};
		getLog().info("starting the fitgoodies maven plugin run goal. " + 
				sourceDirectory + "->" + outputDirectory + " " + fileEncoding);
		invokeMain(directoryRunner, args);
	}

	private void invokeMain(Class directoryRunner, String[] args)
			throws MojoExecutionException {
		Method mainMethod;
		try {
			System.err.println(directoryRunner.getClassLoader());
			mainMethod = directoryRunner.getMethod("main", new Class[]{String[].class});
			mainMethod.invoke(directoryRunner, new Object[]{args});
		} catch (Exception e) {
			getLog().error("Could not invoke DirectoryRunner");
			throw new MojoExecutionException("Could not load DirectoryRunner");
		}
	}

	private Class getDirectoryRunnerClass(ClassLoader loader) throws MojoExecutionException {
		try {
			return loader.loadClass("fitgoodies.runners.DirectoryRunner");
		} catch (ClassNotFoundException e1) {
			getLog().error("Could not load DirectoryRunner");
			throw new MojoExecutionException("Could not load DirectoryRunner");
		}
	}

	private ClassLoader makeClassLoader()
			throws MojoExecutionException {
		try {
			File oldJar = new File(System.getProperty("java.class.path"));
			URL[] directories = new URL[] { classesDirectory.toURI().toURL(), oldJar.toURI().toURL() };
			return new FitGoodiesPluginClassLoader(directories);
		} catch (MalformedURLException e) {
			getLog().error("Invalid class path: " + classesDirectory.toString());
			throw new MojoExecutionException("Invalid class path");
		}
	}
}
