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

/**
 * Goal which touches a timestamp file.
 * 
 * @goal run
 * 
 * @phase process-sources
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

	public void execute() throws MojoExecutionException {
		String[] args = new String[]{sourceDirectory, outputDirectory, fileEncoding};
		getLog().info("starting the fitgoodies maven plugin run goal. " + sourceDirectory + "->" + outputDirectory + " " + fileEncoding);
		DirectoryRunner.main(args);
	}
}
