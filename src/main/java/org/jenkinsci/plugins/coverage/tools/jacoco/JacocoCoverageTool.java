package org.jenkinsci.plugins.coverage.tools.jacoco;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.codehaus.plexus.util.FileUtils;
import org.jacoco.core.analysis.*;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.tools.ExecFileLoader;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coverage.CoverageTool;
import org.jenkinsci.plugins.coverage.Utils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.*;
import java.util.List;

public class JacocoCoverageTool extends CoverageTool {

    @DataBoundConstructor
    public JacocoCoverageTool() {}

    @DataBoundSetter
    public String sourcePattern;
    @DataBoundSetter
    public String classPattern;
    @DataBoundSetter
    public String execPattern;
    @DataBoundSetter
    public String includePattern;
    @DataBoundSetter
    public String excludePattern;

    private ExecutionDataStore executionDataStore;
    private SessionInfoStore sessionInfoStore;

    private Run run;
    private FilePath workspace;
    private TaskListener listener;
    private EnvVars envVars;
    private IBundleCoverage bundleCoverage;

    @Override
    protected void perform(Run run, FilePath workspace, TaskListener listener) throws Exception {
        this.run = run;
        this.workspace = workspace;
        this.listener = listener;
        this.envVars = Utils.getEnvVars(run, listener);

        loadExecutionData();
        this.bundleCoverage = analyzeStructure();
        List<FilePath> sourceDirs = Utils.resolveDirectories(sourcePattern, workspace, envVars);
    }

    public void getPackages() {
        for (IPackageCoverage packageCoverage : bundleCoverage.getPackages()) {
        }
    }

    private void loadExecutionData() throws Exception {
        List<FilePath> execFiles = Utils.resolveFiles(execPattern, workspace, envVars);

        executionDataStore = new ExecutionDataStore();
        sessionInfoStore = new SessionInfoStore();

        for (FilePath execFile : execFiles) {
            try (InputStream execStream = new FileInputStream(execFile.getRemote())) {
                ExecutionDataReader execReader = new ExecutionDataReader(execStream);
                execReader.setExecutionDataVisitor(executionDataStore);
                execReader.setSessionInfoVisitor(sessionInfoStore);
                execReader.read();
            }
        }
    }

    private IBundleCoverage analyzeStructure() throws Exception {
        List<FilePath> classDirs = Utils.resolveDirectories(classPattern, workspace, envVars);
        String includes = envVars.expand(includePattern);
        String excludes = envVars.expand(excludePattern);

        CoverageBuilder coverageBuilder = new CoverageBuilder();
        Analyzer analyzer = new Analyzer(executionDataStore, coverageBuilder);
        for (FilePath classDir : classDirs) {
            List<File> classFiles = FileUtils.getFiles(new File(classDir.getRemote()), includes, excludes);
            for (File classFile : classFiles) {
                analyzer.analyzeAll(classFile);
            }
        }
        return coverageBuilder.getBundle(null);
    }

    @Override
    public String toString() {
        return "jacoco:{sourcePattern: " + sourcePattern + ", classPattern: " + classPattern
                + ", execPattern: " + execPattern + ", includePattern: " + includePattern
                + ", excludePattern: " + excludePattern + "}";
    }

    @Extension
    @Symbol("jacocoTool")
    public static class DescriptorImpl extends CoverageToolDescriptor {
        @Override
        public String getDisplayName() {
            return "Jacoco";
        }
    }
}
