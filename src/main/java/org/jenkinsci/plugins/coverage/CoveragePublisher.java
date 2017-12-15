package org.jenkinsci.plugins.coverage;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.remoting.Callable;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import org.codehaus.plexus.util.StringUtils;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coverage.model.*;
import org.jenkinsci.remoting.RoleChecker;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoveragePublisher extends Recorder implements SimpleBuildStep {

    /*package*/ static final String COVERAGE_PATH = "coverage";
    /*package*/ static final String COVERAGE_FILE_SUFFIX = ".coverage";

    @DataBoundSetter
    public boolean runAlways = false;
    @DataBoundSetter
    public boolean changeBuildStatusOnThresholdViolation = false;
    @DataBoundSetter
    public boolean changeBuildStatusOnDeltaViolation = false;

    @DataBoundSetter
    public String thresholdInstructionMin = "0";
    @DataBoundSetter
    public String thresholdInstructionMax = "0";
    @DataBoundSetter
    public String thresholdBranchMin = "0";
    @DataBoundSetter
    public String thresholdBranchMax = "0";
    @DataBoundSetter
    public String thresholdLineMin = "0";
    @DataBoundSetter
    public String thresholdLineMax = "0";
    @DataBoundSetter
    public String thresholdMethodMin = "0";
    @DataBoundSetter
    public String thresholdMethodMax = "0";
    @DataBoundSetter
    public String thresholdClassMin = "0";
    @DataBoundSetter
    public String thresholdClassMax = "0";

    @DataBoundSetter
    public String deltaInstruction = "0";
    @DataBoundSetter
    public String deltaBranch = "0";
    @DataBoundSetter
    public String deltaLine = "0";
    @DataBoundSetter
    public String deltaMethod = "0";
    @DataBoundSetter
    public String deltaClass = "0";
    @DataBoundSetter
    public List<CoverageTool> coverageTools = new ArrayList<>();

    @DataBoundConstructor
    public CoveragePublisher(){
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener)
            throws InterruptedException, IOException {
        final PrintStream logger = listener.getLogger();
        if (run.getResult() == Result.ABORTED || (!runAlways && run.getResult() == Result.FAILURE)) {
            logger.println("[CodeCoverage] Skipping coverage publishing because of build result");
            return;
        }

        if (coverageTools == null || coverageTools.size() == 0) {
            logger.println("[CodeCoverage] Error: No coverage tool specified");
            if (run.getResult() == null || Result.FAILURE.isWorseThan(run.getResult())) {
                run.setResult(Result.FAILURE);
            }
            return;
        }

        final EnvVars envVars = Utils.getEnvVars(run, listener);

        CoverageThreshold coverageThreshold = new CoverageThreshold(envVars, thresholdInstructionMin, thresholdInstructionMax, thresholdBranchMin, thresholdBranchMax,
                thresholdLineMin, thresholdLineMax, thresholdMethodMin, thresholdMethodMax,
                thresholdClassMin, thresholdClassMax);

        CoverageThreshold deltaThreshold = getDeltaThreshold(run, envVars);

        logger.println("[CodeCoverage] Collecting coverage data for input:\n"
                + "\tRunAlways: " + runAlways + "\n"
                + "\tChangeBuildStatusOnThresholdViolation: " + changeBuildStatusOnThresholdViolation + "\n"
                + "\tChangeBuildStatusOnDeltaViolation: " + changeBuildStatusOnDeltaViolation + "\n"
                + "\tCoverageThreshold: " + coverageThreshold + "\n"
                + "\tDeltaThreshold: " + deltaThreshold + "\n"
                + "\tTools: ");
        for (CoverageTool tool : coverageTools) {
            logger.println("\t\t" + tool);
        }

        final File coverageDirectory = new File(run.getRootDir(), COVERAGE_PATH);

        final List<ToolCoverage> toolCoverages = new ArrayList<>();
        for (final CoverageTool tool : coverageTools) {
            try {
                ToolCoverage toolCoverage = launcher.getChannel().call(new Callable<ToolCoverage, Exception>() {
                    @Override
                    public void checkRoles(RoleChecker checker) throws SecurityException {
                        // No role checking for now.
                    }

                    @Override
                    public ToolCoverage call() throws Exception {
                        return tool.perform(envVars, workspace, logger);
                    }
                });
                toolCoverages.add(toolCoverage);
                saveSourceFiles(coverageDirectory, workspace, toolCoverage, logger);
            } catch (Exception e) {
                logger.println("[CodeCoverage] ERROR: " + tool.getDescriptor().getDisplayName() + " failed processing");
                e.printStackTrace(logger);
            }
        }
        BuildCoverage buildCoverage = new BuildCoverage(toolCoverages);
        storeCoverage(logger, coverageDirectory, Collections.<String>emptyList(), buildCoverage);

        int healthScore = coverageThreshold.getScoreForCoverage(buildCoverage);

        Map<CoverageType, CoverageCounter> buildCoverageSummary = new HashMap<>();
        for (CoverageType type : CoverageType.values()) {
            buildCoverageSummary.put(type, buildCoverage.getCoverage(type));
        }

        int deltaHealth = deltaThreshold.getScoreForCoverage(buildCoverage);
        if (changeBuildStatusOnDeltaViolation && deltaHealth != 100) {
            logger.println("[Code Coverage] Delta thresholds not met. Failing build.");
            run.setResult(Result.FAILURE);
        }

        run.addAction(new CoverageBuildAction(buildCoverageSummary, Math.min(healthScore, deltaHealth)));
    }

    private void saveSourceFiles(File coverageDirectory, FilePath workspace, ToolCoverage toolCoverage, PrintStream logger) throws IOException, InterruptedException {
        File toolDirectory = new File(coverageDirectory, Utils.normalizeForFileName(toolCoverage.getName()));
        for (PackageCoverage packageCoverage : toolCoverage.getChildren()) {
            File packageDirectory = new File(toolDirectory, Utils.normalizeForFileName(packageCoverage.getName()));
            if (!packageDirectory.exists() && !packageDirectory.mkdirs()) {
                logger.println("[Code Coverage] ERROR: Unable to create package directory: " + packageDirectory.getAbsolutePath());
                throw new IOException("Create directory failed");
            }
            for (SourceFileCoverage sourceFileCoverage : packageCoverage.getSourceFiles()) {
                FilePath sourcePath = workspace.child(sourceFileCoverage.getFilePath());
                JSONArray sourceFile = new JSONArray();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(sourcePath.read()))) {
                    String line;
                    int availableLineSize = sourceFileCoverage.getLines().size();
                    for (int i = 0; (line = reader.readLine()) != null; i++) {
                        JSONArray lineData = new JSONArray();
                        lineData.put(line);
                        if (i < availableLineSize) {
                            SourceFileCoverage.Line coverageLine = sourceFileCoverage.getLines().get(i);
                            lineData.put(coverageLine.getCoverageStatus().value);
                            lineData.put(coverageLine.getInstructionCounter().getCovered());
                            lineData.put(coverageLine.getInstructionCounter().getMissed());
                            lineData.put(coverageLine.getBranchCounter().getCovered());
                            lineData.put(coverageLine.getBranchCounter().getMissed());
                        } else {
                            lineData.put(SourceFileCoverage.CoverageStatus.NOT_SOURCE.value);
                            lineData.put(CoverageCounter.UNKNOWN_COUNTER.getCovered());
                            lineData.put(CoverageCounter.UNKNOWN_COUNTER.getMissed());
                            lineData.put(CoverageCounter.UNKNOWN_COUNTER.getCovered());
                            lineData.put(CoverageCounter.UNKNOWN_COUNTER.getMissed());
                        }
                        sourceFile.put(lineData);
                    }
                }
                try (FileWriter writer = new FileWriter(new File(packageDirectory, sourcePath.getName()))) {
                    sourceFile.write(writer);
                }
            }
        }
    }

    private CoverageThreshold getDeltaThreshold(Run run, EnvVars envVars) {
        Run previousRun = run.getParent().getLastSuccessfulBuild();
        CoverageBuildAction previousAction = (previousRun == null)? null : previousRun.getAction(CoverageBuildAction.class);
        Map<CoverageType, CoverageCounter> lastCoverage = (previousAction == null)? null : previousAction.coverageSummary;

        if (lastCoverage == null) {
            return new CoverageThreshold(envVars, "0", "0", "0", "0", "0", "0", "0", "0", "0", "0");
        } else {
            String instructionDelta = Float.toString(CoverageThreshold.getCoveredPercent(lastCoverage.get(CoverageType.INSTRUCTION)) - CoverageThreshold.getResolvedThreshold(envVars, deltaInstruction));
            String branchDelta = Float.toString(CoverageThreshold.getCoveredPercent(lastCoverage.get(CoverageType.BRANCH)) - CoverageThreshold.getResolvedThreshold(envVars, deltaBranch));
            String lineDelta = Float.toString(CoverageThreshold.getCoveredPercent(lastCoverage.get(CoverageType.LINE)) - CoverageThreshold.getResolvedThreshold(envVars, deltaLine));
            String methodDelta = Float.toString(CoverageThreshold.getCoveredPercent(lastCoverage.get(CoverageType.METHOD)) - CoverageThreshold.getResolvedThreshold(envVars, deltaMethod));
            String classDelta = Float.toString(CoverageThreshold.getCoveredPercent(lastCoverage.get(CoverageType.CLASS)) - CoverageThreshold.getResolvedThreshold(envVars, deltaClass));
            return new CoverageThreshold(envVars, instructionDelta, instructionDelta, branchDelta, branchDelta, lineDelta, lineDelta, methodDelta, methodDelta, classDelta, classDelta);
        }
    }

    private void storeCoverage(PrintStream logger, final File coverageDirectory, List<String> path, Coverage coverage) throws IOException {
        File directory = new File(coverageDirectory, StringUtils.join(path.iterator(), "/"));
        if (!directory.exists() && !directory.mkdirs()) {
            logger.println("[Code Coverage] ERROR: Unable to create directory: " + directory.getAbsolutePath());
            throw new IOException("Create directory failed");
        }

        JSONObject summaryJson = new JSONObject();
        summaryJson.put("name", coverage.getName());
        summaryJson.put("path", new JSONArray(path));
        summaryJson.put("coverageSummary", getCoverageSummaryJSon(coverage));

        JSONObject children = new JSONObject();
        children.put("type", coverage.getChildrenType());
        children.put("isRefed", coverage.isChildrenRefed());
        children.put("data", getCoverageChildren(coverage));
        summaryJson.put("children", children);

        if (coverage instanceof ClassCoverage) {
            ClassCoverage classCoverage = (ClassCoverage) coverage;
            String sourceFilePath = classCoverage.getSourceFilePath();
            if (sourceFilePath != null) {
                summaryJson.put("sourceFilePath", sourceFilePath);
            }
        }

        try (FileWriter summaryWriter = new FileWriter(new File(directory, coverage.getName() + COVERAGE_FILE_SUFFIX))) {
            summaryJson.write(summaryWriter);
        }

        if (coverage.isChildrenRefed()) {
            for (Coverage childCoverage : coverage.getChildren()) {
                List<String> childPath = new ArrayList<>(path);
                childPath.add(coverage.getName());
                storeCoverage(logger, coverageDirectory, childPath, childCoverage);
            }
        }
    }

    private JSONObject getCoverageSummaryJSon(Coverage coverage) {
        JSONObject summary = new JSONObject();
        for (CoverageType type : CoverageType.values()) {
            CoverageCounter counter = coverage.getCoverage(type);
            summary.put(type.name(), new JSONObject().put("covered", counter.getCovered())
                                                     .put("missed", counter.getMissed()));
        }
        return summary;
    }

    private JSONArray getCoverageChildren(Coverage coverage) {
        JSONArray childrenArray = new JSONArray();
        for (Coverage childCoverage : coverage.getChildren()) {
            JSONObject summaryJson = new JSONObject();
            summaryJson.put("name", childCoverage.getName());
            summaryJson.put("coverageSummary", getCoverageSummaryJSon(childCoverage));
            childrenArray.put(summaryJson);
        }
        return childrenArray;
    }

    @Symbol("coverage")
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.CoveragePublisher_DisplayName();
        }

    }
}
