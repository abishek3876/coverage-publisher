package org.jenkinsci.plugins.coverage.tools.jacoco;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import org.codehaus.plexus.util.FileUtils;
import org.jacoco.core.analysis.*;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coverage.CoverageTool;
import org.jenkinsci.plugins.coverage.Utils;
import org.jenkinsci.plugins.coverage.model.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JacocoCoverageTool extends CoverageTool {

    private static final String TOOL_NAME = "Jacoco";

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
    protected ToolCoverage perform(EnvVars envVars, FilePath workspace, PrintStream logger) throws Exception {
        ExecutionDataStore executionDataStore = loadExecutionData(workspace, envVars);
        IBundleCoverage bundleCoverage = analyzeStructure(executionDataStore, workspace, envVars);
        List<FilePath> sourceDirs = Utils.resolveDirectories(sourcePattern, workspace, envVars);
        List<PackageCoverage> packageCoverages = getPackageCoverageList(sourceDirs, bundleCoverage);
        return new ToolCoverage(TOOL_NAME, packageCoverages);
    }

    private List<PackageCoverage> getPackageCoverageList(List<FilePath> sourceDirs, IBundleCoverage bundleCoverage) throws IOException, InterruptedException {
        List<PackageCoverage> packageCoverages = new ArrayList<>();
        for (final IPackageCoverage packageCoverage : bundleCoverage.getPackages()) {
            packageCoverages.add(new PackageCoverage(packageCoverage.getName().replace("/", "."), getClassCoverageList(packageCoverage), getSourceFileCoverages(sourceDirs, packageCoverage)));
        }

        return packageCoverages;
    }

    private List<SourceFileCoverage> getSourceFileCoverages(List<FilePath> sourceDirs, IPackageCoverage packageCoverage) throws IOException, InterruptedException {
        Collection<ISourceFileCoverage> sourceFiles = packageCoverage.getSourceFiles();
        List<SourceFileCoverage> sourceFileCoverages = new ArrayList<>();
        if (sourceFiles != null) {
            for (final ISourceFileCoverage sourceFile : sourceFiles) {
                final String filePath = getSourceFilePath(sourceDirs, sourceFile);
                if (filePath != null) {
                    ArrayList<SourceFileCoverage.Line> lines = new ArrayList<>();
                    for (int i = 1; i <= sourceFile.getLastLine(); i++) {
                        ILine lineCoverage = sourceFile.getLine(i);
                        SourceFileCoverage.CoverageStatus coverageStatus;
                        switch (lineCoverage.getStatus()) {
                            case ICounter.FULLY_COVERED:
                                coverageStatus = SourceFileCoverage.CoverageStatus.FULLY_COVERED;
                                break;
                            case ICounter.NOT_COVERED:
                                coverageStatus = SourceFileCoverage.CoverageStatus.NOT_COVERED;
                                break;
                            case ICounter.PARTLY_COVERED:
                                coverageStatus = SourceFileCoverage.CoverageStatus.PARTIALLY_COVERED;
                                break;
                            default:
                                coverageStatus = SourceFileCoverage.CoverageStatus.NOT_SOURCE;
                                break;
                        }
                        ICounter branchCounter = lineCoverage.getBranchCounter();
                        CoverageCounter bCounter = new CoverageCounter(branchCounter.getCoveredCount(), branchCounter.getMissedCount());
                        lines.add(new SourceFileCoverage.Line(coverageStatus, bCounter));
                    }
                    sourceFileCoverages.add(new SourceFileCoverage(filePath, lines));
                }
            }
        }
        return sourceFileCoverages;
    }

    private String getSourceFilePath(List<FilePath> sourceDirs, ISourceFileCoverage sourceFile) throws IOException, InterruptedException {
        String filePath = sourceFile.getPackageName().replace(".", File.separator) + File.separator + sourceFile.getName();
        for (FilePath sourceDir : sourceDirs) {
            FilePath sourcePath = sourceDir.child(filePath);
            if (sourcePath.exists() && !sourcePath.isDirectory()) {
                return sourcePath.getRemote();
            }
        }
        return null;
    }

    private List<ClassCoverage> getClassCoverageList(IPackageCoverage packageCoverage) {
        List<ClassCoverage> classCoverages = new ArrayList<>();
        for (final IClassCoverage classCoverage : packageCoverage.getClasses()) {
            String className = classCoverage.getName();
            className = className.substring(className.lastIndexOf("/") + 1);
            classCoverages.add(new ClassCoverage(className, getMethodCoverageList(classCoverage), classCoverage.getSourceFileName()));
        }
        return classCoverages;
    }

    private List<MethodCoverage> getMethodCoverageList(IClassCoverage classCoverage) {
        List<MethodCoverage> methodCoverages = new ArrayList<>();
        for (final IMethodCoverage methodCoverage : classCoverage.getMethods()) {
            final ICounter bCounter = methodCoverage.getBranchCounter();
            final ICounter lCounter = methodCoverage.getLineCounter();

            methodCoverages.add(new MethodCoverage(methodCoverage.getName()) {
                @Override
                @Nonnull
                public CoverageCounter getCoverage(CoverageType coverageType) {
                    switch (coverageType) {
                        case BRANCH:
                            return new CoverageCounter(bCounter.getCoveredCount(), bCounter.getMissedCount());
                        case LINE:
                            return new CoverageCounter(lCounter.getCoveredCount(), lCounter.getMissedCount());
                        default:
                            return super.getCoverage(coverageType);
                    }
                }
            });
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
            return TOOL_NAME;
        }
    }
}
