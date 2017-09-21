package org.jenkinsci.plugins.coverage;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import hudson.*;
import hudson.model.*;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coverage.model.ClassCoverage;
import org.jenkinsci.plugins.coverage.model.PackageCoverage;
import org.jenkinsci.plugins.coveragepublisher.Messages;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

public class CoveragePublisher extends Recorder implements SimpleBuildStep {

    @DataBoundSetter
    public boolean runAlways;
    @DataBoundSetter
    public boolean changeBuildStatusOnThresholdViolation;
    @DataBoundSetter
    public boolean changeBuildStatusOnDeltaViolation;

    @DataBoundSetter
    public String thresholdInstructionMin;
    @DataBoundSetter
    public String thresholdInstructionMax;
    @DataBoundSetter
    public String thresholdBranchMin;
    @DataBoundSetter
    public String thresholdBranchMax;
    @DataBoundSetter
    public String thresholdLineMin;
    @DataBoundSetter
    public String thresholdLineMax;
    @DataBoundSetter
    public String thresholdMethodMin;
    @DataBoundSetter
    public String thresholdMethodMax;
    @DataBoundSetter
    public String thresholdClassMin;
    @DataBoundSetter
    public String thresholdClassMax;

    @DataBoundSetter
    public String deltaInstruction;
    @DataBoundSetter
    public String deltaBranch;
    @DataBoundSetter
    public String deltaLine;
    @DataBoundSetter
    public String deltaMethod;
    @DataBoundSetter
    public String deltaClass;
    @DataBoundSetter
    public List<CoverageTool> coverageTools = new ArrayList<>();

    @DataBoundConstructor
    public CoveragePublisher(){}

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {

        PrintStream logger = listener.getLogger();
        if (!runAlways && (run.getResult() == Result.FAILURE || run.getResult() == Result.ABORTED)) {
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

        EnvVars envVars = Utils.getEnvVars(run, listener);

        CoverageThreshold coverageThreshold = new CoverageThreshold(envVars, thresholdInstructionMin, thresholdInstructionMax,
                thresholdBranchMin, thresholdBranchMax, thresholdLineMin, thresholdLineMax, thresholdMethodMin, thresholdMethodMax,
                thresholdClassMin, thresholdClassMax);

        CoverageThreshold deltaThreshold = null; //TODO: Populate delta threshold values

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

        Map<String, List<PackageCoverage>> buildCoverage = new TreeMap<>();
        for (CoverageTool tool : coverageTools) {
            try {
                buildCoverage.put(tool.getDescriptor().getDisplayName(), tool.perform(run, workspace, listener));
            } catch (Exception e) {
                logger.println("[CodeCoverage] ERROR: " + tool.getDescriptor().getDisplayName() + " failed processing");
                e.printStackTrace(logger);
            }
        }
    }

    private void persistSourceFiles(Run run, PackageCoverage coverage) {
        File buildPath = run.getRootDir();
    }

    private void getCoveragePOJO(Map<String, List<PackageCoverage>> buildCoverage) {
        CoveragePOJO coveragePOJO = new CoveragePOJO();
        for (BuildCoverage buildCoverage : buildCoverages) {
            for (PackageCoverage packageCoverage : buildCoverage.getPackages()) {
                String packageName = normalizePackageName(packageCoverage.getPackageName());
                CoveragePOJO.PackagePOJO packagePOJO = coveragePOJO.packages.get(packageName);
                if (packagePOJO == null) {
                    packagePOJO = new CoveragePOJO.PackagePOJO();
                    coveragePOJO.packages.put(packageName, packagePOJO);
                }
                updatePackagePOJO(packagePOJO, packageCoverage);
            }
        }
    }

    private void updatePackagePOJO(CoveragePOJO.PackagePOJO packagePOJO, PackageCoverage packageCoverage) {
        for (ClassCoverage classCoverage : packageCoverage.getClasses()) {
            packagePOJO.classes.
        }
        packagePOJO.
    }

    @SuppressWarnings("unused")
    @Symbol("codeCoverage")
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        @SuppressWarnings("rawtypes")
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.CoveragePublisher_DisplayName();
        }

    }
}
