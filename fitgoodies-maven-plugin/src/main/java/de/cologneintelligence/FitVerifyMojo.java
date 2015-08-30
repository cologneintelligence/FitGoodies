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

package de.cologneintelligence;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.Map;

@Mojo(name = "verify",
		defaultPhase = LifecyclePhase.VERIFY,
		requiresDependencyResolution = ResolutionScope.TEST)
public class FitVerifyMojo extends AbstractMojo {
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		@SuppressWarnings("unchecked")
		Map<String, Object> context = getPluginContext();

		if (!context.containsKey(FitIntegrationTestMojo.FIT_MOJO_RESULT_FAILURE)) {
			throw new MojoFailureException("FitGoodies integration tests did not run");
		}

		if (Boolean.FALSE.equals(context.get(FitIntegrationTestMojo.FIT_MOJO_RESULT_FAILURE))) {
			throw new MojoFailureException("One or more fit test(s) failed.");
		}
	}
}
