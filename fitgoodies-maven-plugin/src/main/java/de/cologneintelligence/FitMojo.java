package de.cologneintelligence;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@Mojo(name = "run-tests",
        defaultPhase = LifecyclePhase.INTEGRATION_TEST,
        requiresDependencyResolution = ResolutionScope.TEST)
public class FitMojo extends AbstractMojo {
    @Parameter(defaultValue = "target/fit", property = "outputDir", required = true )
    private File outputDirectory;

    @Parameter(defaultValue = "src/test/fixtures", property="fixturesDir", required = true)
    private File fixturesDirectory;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException {
        ClassLoader loader = createClassloader();
        runFit(loader);
    }

    private ClassLoader createClassloader() throws MojoExecutionException {
        List<String> classpathElements = getRuntimeClasspath();
        classpathElements.add(project.getBuild().getOutputDirectory());
        classpathElements.add(project.getBuild().getTestOutputDirectory());

        URL[] urls = new URL[classpathElements.size()];
        for (int i = 0; i < classpathElements.size(); i++) {
            File file = new File(classpathElements.get(i));
            try {
                urls[i] = file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new MojoExecutionException("Could not build classpath with '"+file+"'", e);
            }
        }

        return new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
    }

    private List<String> getRuntimeClasspath() {
        try {
            @SuppressWarnings("unchecked")
            final List<String> temp = project.getRuntimeClasspathElements();
            return temp;
        } catch (DependencyResolutionRequiredException e) {
            throw new RuntimeException("Could not determine runtime classpath", e);
        }
    }

    private void runFit(ClassLoader loader) throws MojoExecutionException {
        Class<?> runner;
        try {
            runner = loader.loadClass("de.cologneintelligence.fitgoodies.runners.FitRunner");
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("FitGoodies must be in the projects test scope!", e);
        }

        final String[] methodArgs = {
                "-s", fixturesDirectory.getPath(),
                "-d", outputDirectory.getPath()
        };

        try {
            Method mainMethod = runner.getMethod("main", String[].class);
            mainMethod.invoke(null, new Object[] {methodArgs});
        } catch (NoSuchMethodException e) {
            throw new MojoExecutionException("Error while running fit", e);
        } catch (InvocationTargetException e) {
            throw new MojoExecutionException("Error while running fit", e);
        } catch (IllegalAccessException e) {
            throw new MojoExecutionException("Error while running fit", e);
        }
    }
}
