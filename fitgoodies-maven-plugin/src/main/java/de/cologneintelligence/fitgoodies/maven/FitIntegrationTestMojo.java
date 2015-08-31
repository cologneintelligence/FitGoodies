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

package de.cologneintelligence.fitgoodies.maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

@Mojo(name = "integration-test",
		defaultPhase = LifecyclePhase.INTEGRATION_TEST,
		requiresDependencyResolution = ResolutionScope.TEST)
public class FitIntegrationTestMojo extends AbstractMojo {
	public static final String FIT_MOJO_RESULT_FAILURE = "fit.mojo.result.failure";
	public static final String MAIN_CLASS = "de.cologneintelligence.fitgoodies.runners.FitRunner";

	@Parameter(defaultValue = "target/fit", property = "outputDir", required = true)
	private File outputDirectory;

	@Parameter(defaultValue = "src/test/fixtures", property = "fixturesDir", required = true)
	private File fixturesDirectory;

	@Parameter(property = "limits", required = false)
	private String[] limits = new String[0];

	@Parameter(property = "additionalClasspathElements", required = false)
	private File[] additionalClasspathElements = new File[0];

	@Parameter(property = "jvmArgs", required = false)
	private String[] jvmArgs = new String[0];

	@Parameter(defaultValue = "UTF-8", property = "project.build.sourceEncoding", required = false)
	private String encoding;

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Copy static resources into output directory");
		copyNonTestFiles(fixturesDirectory, outputDirectory);
		getLog().info("Running Tests");

		URL[] classpath = createClasspath();
		runFit(classpath);
	}

	private void copyNonTestFiles(File sourceDir, File targetDir) throws MojoExecutionException {
		File[] files = sourceDir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					File newTarget = new File(targetDir, file.getName());
					//noinspection ResultOfMethodCallIgnored
					newTarget.mkdirs();
					copyNonTestFiles(file, newTarget);
				} else if (isNonTestFile(file.getName())) {
					try {
						FileUtils.copyFile(file, new File(targetDir, file.getName()));
					} catch (IOException e) {
						throw new MojoExecutionException("Could not copy file: " + file.getAbsolutePath(), e);
					}
				}
			}
		}
	}

	private boolean isNonTestFile(String name) {
		return !name.matches("(?i).*\\.html?$");
	}

	private URL[] createClasspath() throws MojoExecutionException {
		List<String> classpathElements = getClasspath();
		classpathElements.add(project.getBuild().getOutputDirectory());
		classpathElements.add(project.getBuild().getTestOutputDirectory());

		URL[] urls = new URL[classpathElements.size()];
		for (int i = 0; i < classpathElements.size(); i++) {
			File file = new File(classpathElements.get(i));
			try {
				urls[i] = file.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new MojoExecutionException("Could not build classpath with '" + file + "'", e);
			}
		}

		return urls;
	}

	private List<String> getClasspath() throws MojoExecutionException {
		try {
			@SuppressWarnings("unchecked")
			final List<String> temp = project.getTestClasspathElements();
			return temp;
		} catch (DependencyResolutionRequiredException e) {
			throw new MojoExecutionException("Could not determine runtime classpath", e);
		}
	}

	private void saveResult(boolean result) {
		@SuppressWarnings("unchecked")
		Map<String, Object> context = (Map<String, Object>) this.getPluginContext();
		context.put(FIT_MOJO_RESULT_FAILURE, result);
	}

	private void runFit(URL[] classpath) throws MojoExecutionException, MojoFailureException {
		File bootJar;
		try {
			bootJar = writeBootJar(createClassPathString(classpath));
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write boot jar", e);
		}

		try {
			ProcessBuilder builder = prepareProcess(bootJar);
			startProcess(builder);
		} finally {
			bootJar.delete();
		}
	}

	private void startProcess(ProcessBuilder builder) throws MojoExecutionException {
		try {
			Process process = builder.start();

			new StreamLogger(process.getErrorStream(), true, getLog()).start();
			new StreamLogger(process.getInputStream(), false, getLog()).start();

			int result = process.waitFor();

			boolean success = result == 0;
			saveResult(success);

			if (!success) {
				getLog().info("One or more fit test(s) failed with return code " + result + ". Will fail in verify phase!");
			}

		} catch (Exception e) {
			throw new MojoExecutionException("Error while running fit", e);
		}
	}

	private ProcessBuilder prepareProcess(File bootJar) throws MojoExecutionException {
		try {
            String executable = System.getProperty( "java.home" ) + File.separator + "bin" + File.separator + "java";
			List<String> args = createJavaArgs(executable, bootJar);
			getLog().debug("Running process: " + args.toString());
			return new ProcessBuilder(args)
					.directory(project.getBasedir());
		} catch (Exception e) {
			throw new MojoExecutionException("Error while preparing java process", e);
		}
	}

	private List<String> createJavaArgs(String executable, File bootJar) throws URISyntaxException {

		List<String> args = new LinkedList<>();
		args.add(executable);
		args.add("-cp");
		args.add(bootJar.getAbsolutePath());

		args.addAll(Arrays.asList(jvmArgs));

		args.add(MAIN_CLASS);

		args.add("-d");
		args.add(outputDirectory.getPath());
		args.add("-e");
		args.add(encoding);
		args.add("-s");
		args.add(fixturesDirectory.getPath());

		for (String limit : limits) {
			args.add("-o");
			args.add(limit);
		}

		return args;
	}

	private String createClassPathString(URL[] classpath) {
		StringBuilder classPathBuilder = new StringBuilder();

		for (URL url : classpath) {
			appendToClasspath(url, classPathBuilder);
		}

		for (File element : additionalClasspathElements) {
			try {
				appendToClasspath(element.toURI().toURL(), classPathBuilder);
			} catch (MalformedURLException e) {
				throw new RuntimeException("Cannot convert file to url: " + element, e);
			}
		}

		return classPathBuilder.toString();
	}

	private void appendToClasspath(URL url, StringBuilder classPathBuilder) {
		if (classPathBuilder.length() > 0) {
			classPathBuilder.append(' ');
		}

		classPathBuilder.append(url.toString());
	}

	public File writeBootJar(String classpath) throws IOException {
		File bootJar = new File(outputDirectory, "boot.jar");

		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, classpath);

		JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(bootJar), manifest);
		jarOutputStream.close();

		return bootJar;
	}
}
