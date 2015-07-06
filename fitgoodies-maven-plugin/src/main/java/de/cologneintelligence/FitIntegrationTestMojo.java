package de.cologneintelligence;

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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

@Mojo(name = "integration-test",
        defaultPhase = LifecyclePhase.INTEGRATION_TEST,
        requiresDependencyResolution = ResolutionScope.TEST)
public class FitIntegrationTestMojo extends AbstractMojo {
    public static final String FIT_MOJO_RESULT_FAILURE = "fit.mojo.result.failure";

    @Parameter(defaultValue = "target/fit", property = "outputDir", required = true )
    private File outputDirectory;

    @Parameter(defaultValue = "src/test/fixtures", property="fixturesDir", required = true)
    private File fixturesDirectory;

    @Parameter(defaultValue = "UTF-8", property="project.build.sourceEncoding", required = false)
    private String encoding;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Copy static resources into output directory");
        copyNonTestFiles(fixturesDirectory, outputDirectory);
        getLog().info("Running Tests");
        ClassLoader loader = createClassloader();
        runFit(loader);
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

    private ClassLoader createClassloader() throws MojoExecutionException {
        List<String> classpathElements = getClasspath();
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

    private List<String> getClasspath() {
        try {
            @SuppressWarnings("unchecked")
            final List<String> temp = project.getTestClasspathElements();
            return temp;
        } catch (DependencyResolutionRequiredException e) {
            throw new RuntimeException("Could not determine runtime classpath", e);
        }
    }

    private void saveResult(boolean result) {
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) this.getPluginContext();
        context.put(FIT_MOJO_RESULT_FAILURE, result);
    }

    private void runFit(ClassLoader loader) throws MojoExecutionException, MojoFailureException {
        Class<?> runner;
        try {
            runner = loader.loadClass("de.cologneintelligence.fitgoodies.runners.FitRunner");
        } catch (ClassNotFoundException e) {
            throw new MojoFailureException("FitGoodies must be in the projects test scope!", e);
        }

        final String[] methodArgs = {
                "-e", encoding,
                "-s", fixturesDirectory.getPath(),
                "-d", outputDirectory.getPath(),
                "--ne"
        };

        try {
            Method mainMethod = runner.getMethod("main", String[].class);
            mainMethod.invoke(null, new Object[] {methodArgs});
            saveResult(true);
        } catch (NoSuchMethodException e) {
            throw new MojoExecutionException("Error while running fit", e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof AssertionError) {
                saveResult(false);
                getLog().info("One or more fit test(s) failed. Will fail in verify phase!");
            } else {
                e.getTargetException().printStackTrace();
                throw new MojoExecutionException("Error while running fit", e);
            }
        } catch (IllegalAccessException e) {
            throw new MojoExecutionException("Error while running fit", e);
        }
    }
}
