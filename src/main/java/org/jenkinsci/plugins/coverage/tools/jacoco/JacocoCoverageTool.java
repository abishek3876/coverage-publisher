package org.jenkinsci.plugins.coverage.tools.jacoco;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.Callable;
import org.codehaus.plexus.util.FileUtils;
import org.jacoco.core.analysis.*;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coverage.CoverageTool;
import org.jenkinsci.plugins.coverage.Utils;
import org.jenkinsci.plugins.coverage.model.*;
import org.jenkinsci.remoting.RoleChecker;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JacocoCoverageTool extends CoverageTool {

    @DataBoundConstructor
    public JacocoCoverageTool() {}

    @DataBoundSetter
    public String sourcePattern = "**/src/main/java";
    @DataBoundSetter
    public String classPattern = "**/classes";
    @DataBoundSetter
    public String execPattern = "**/*.exec,**/*.ec";
    @DataBoundSetter
    public String includePattern = "**";
    @DataBoundSetter
    public String excludePattern = "";

    @Override
    protected List<PackageCoverage> perform(Run run, final FilePath workspace, Launcher launcher, TaskListener listener) throws Exception {
        final EnvVars envVars = Utils.getEnvVars(run, listener);

        return launcher.getChannel().call(new Callable<List<PackageCoverage>, Exception>() {
            @Override
            public void checkRoles(RoleChecker checker) throws SecurityException {
                //No role checking for now.
            }

            @Override
            public List<PackageCoverage> call() throws Exception {
                ExecutionDataStore executionDataStore = loadExecutionData(workspace, envVars);
                IBundleCoverage bundleCoverage = analyzeStructure(executionDataStore, workspace, envVars);
                List<FilePath> sourceDirs = Utils.resolveDirectories(sourcePattern, workspace, envVars);
                return getPackageCoverageList(bundleCoverage);
            }
        });
    }

    private List<PackageCoverage> getPackageCoverageList(IBundleCoverage bundleCoverage) {
        List<PackageCoverage> packageCoverages = new ArrayList<>();
        for (final IPackageCoverage packageCoverage : bundleCoverage.getPackages()) {
            packageCoverages.add(new PackageCoverage(packageCoverage.getName(), getClassCoverageList(packageCoverage)));
        }

        return packageCoverages;
    }

    private List<ClassCoverage> getClassCoverageList(IPackageCoverage packageCoverage) {
        List<ClassCoverage> classCoverages = new ArrayList<>();
        for (final IClassCoverage classCoverage : packageCoverage.getClasses()) {
            classCoverages.add(new ClassCoverage(classCoverage.getName(), getMethodCoverageList(classCoverage)));
        }
        return classCoverages;
    }

    private List<MethodCoverage> getMethodCoverageList(IClassCoverage classCoverage) {
        List<MethodCoverage> methodCoverages = new ArrayList<>();
        for (final IMethodCoverage methodCoverage : classCoverage.getMethods()) {
            ICounter bCounter = methodCoverage.getBranchCounter();
            ICounter lCounter = methodCoverage.getLineCounter();

            methodCoverages.add(new MethodCoverage(methodCoverage.getName(), bCounter.getTotalCount(), bCounter.getCoveredCount(), lCounter.getTotalCount(), lCounter.getCoveredCount()));
        }
        return methodCoverages;
    }

    private ExecutionDataStore loadExecutionData(FilePath workspace, EnvVars envVars) throws Exception {
        List<FilePath> execFiles = Utils.resolveFiles(execPattern, workspace, envVars);

        ExecutionDataStore executionDataStore = new ExecutionDataStore();
        SessionInfoStore sessionInfoStore = new SessionInfoStore();

        for (FilePath execFile : execFiles) {
            try (InputStream execStream = new FileInputStream(execFile.getRemote())) {
                ExecutionDataReader execReader = new ExecutionDataReader(execStream);
                execReader.setExecutionDataVisitor(executionDataStore);
                execReader.setSessionInfoVisitor(sessionInfoStore);
                execReader.read();
            }
        }

        return executionDataStore;
    }

    private IBundleCoverage analyzeStructure(ExecutionDataStore executionDataStore, FilePath workspace, EnvVars envVars) throws Exception {
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
