package org.jenkinsci.plugins.coverage;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import jenkins.MasterToSlaveFileCallable;
import org.codehaus.plexus.util.DirectoryScanner;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Utils {
    private Utils() {}

    public static EnvVars getEnvVars(Run run, TaskListener listener) throws IOException, InterruptedException {
        Map<String, String> envs = (run instanceof AbstractBuild) ? ((AbstractBuild<?,?>) run).getBuildVariables() : Collections.<String, String>emptyMap();
        EnvVars envVars = run.getEnvironment(listener);
        envVars.overrideAll(envs);
        return envVars;
    }

    public static List<String> expandPattern(String pattern, EnvVars envVars, String defaultValue) {
        ArrayList<String> expandedPatterns = new ArrayList<>();
        pattern = envVars.expand(pattern);
        String[] patterns = pattern.split(",");
        for (String eachPattern : patterns) {
            if (eachPattern != null && eachPattern.length() > 0) {
                expandedPatterns.add(eachPattern);
            }
        }
        if (defaultValue != null && expandedPatterns.size() == 0) {
            expandedPatterns.add(defaultValue);
        }
        return expandedPatterns;
    }

    public static List<FilePath> resolveDirectories(String pattern, FilePath rootDir, EnvVars envVars) throws IOException, InterruptedException {
        String input = envVars.expand(pattern);
        FilePath[] paths = rootDir.act(new ResolveDirPaths(input));
        ArrayList<FilePath> filePaths = new ArrayList<>();
        for (FilePath path : paths) {
            if (path.exists() && path.isDirectory()) {
                filePaths.add(path);
            }
        }
        return filePaths;
    }

    public static @Nonnull List<FilePath> resolveFiles(String pattern, FilePath rootDir, EnvVars envVars) throws IOException, InterruptedException {
        FilePath[] paths = resolvePaths(pattern, rootDir, envVars);
        ArrayList<FilePath> filePaths = new ArrayList<>();
        for (FilePath path : paths) {
            if (path.exists() && !path.isDirectory()) {
                filePaths.add(path);
            }
        }
        return filePaths;
    }

    public static FilePath[] resolvePaths(String pattern, FilePath rootDir, EnvVars envVars) throws IOException, InterruptedException {
        return resolvePaths(pattern, null, rootDir, envVars);
    }

    public static FilePath[] resolvePaths(String pattern, String excludes, FilePath rootDir, EnvVars envVars) throws IOException, InterruptedException {
        pattern = envVars.expand(pattern);
        excludes = envVars.expand(excludes);
        return rootDir.list(pattern, excludes);
    }

    private static class ResolveDirPaths extends MasterToSlaveFileCallable<FilePath[]> {
        private static final long serialVersionUID = 1552178457453558870L;
        private static final String DIR_SEP = "\\s*,\\s*";
        private final String input;

        ResolveDirPaths(String input) {
            this.input = input;
        }

        @Override
        public FilePath[] invoke(File f, VirtualChannel channel) throws IOException {
            FilePath base = new FilePath(f);
            ArrayList<FilePath> localDirectoryPaths= new ArrayList<>();
            String[] includes = input.split(DIR_SEP);
            DirectoryScanner ds = new DirectoryScanner();

            ds.setIncludes(includes);
            ds.setCaseSensitive(false);
            ds.setBasedir(f);
            ds.scan();
            String[] dirs = ds.getIncludedDirectories();

            for (String dir : dirs) {
                localDirectoryPaths.add(base.child(dir));
            }
            FilePath[] lfp = {};//trick to have an empty array as a parameter, so the returned array will contain the elements
            return localDirectoryPaths.toArray(lfp);
        }
    }

    public static String normalizeForFileName(String input) {
        return input.toLowerCase(Locale.US).replaceAll("\\W+", ".");
    }
}
