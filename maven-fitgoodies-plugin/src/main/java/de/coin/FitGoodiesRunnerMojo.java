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

import fitgoodies.runners.DirectoryRunner;

import java.io.File;

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
getLog().error(System.getProperty("java.class.path", "."));
getLog().error(classesDirectory.toString());
		String[] args = new String[]{sourceDirectory, outputDirectory, fileEncoding};
		getLog().info("starting the fitgoodies maven plugin run goal. " + sourceDirectory + "->" + outputDirectory + " " + fileEncoding);
		DirectoryRunner.main(args);
	}
}
