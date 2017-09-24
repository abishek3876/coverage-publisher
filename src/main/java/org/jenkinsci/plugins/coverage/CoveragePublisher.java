package org.jenkinsci.plugins.coverage;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.sun.corba.se.impl.encoding.CodeSetConversion;
import hudson.*;
import hudson.model.*;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coverage.model.BuildCoverage;
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
    public boolean runAlways = false;
    @DataBoundSetter
    public boolean changeBuildStatusOnThresholdViolation = false;
    @DataBoundSetter
    public boolean changeBuildStatusOnDeltaViolation = false;

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

        CoverageThreshold coverageThreshold = new CoverageThreshold(envVars, thresholdBranchMin, thresholdBranchMax,
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

        final Map<String, List<PackageCoverage>> coverageMap = new TreeMap<>();
        for (CoverageTool tool : coverageTools) {
            try {
                coverageMap.put(tool.getDescriptor().getDisplayName(), tool.perform(run, workspace, listener));
            } catch (Exception e) {
                logger.println("[CodeCoverage] ERROR: " + tool.getDescriptor().getDisplayName() + " failed processing");
                e.printStackTrace(logger);
            }
        }
        BuildCoverage buildCoverage = new BuildCoverage() {
            @Override
            public Map<String, List<PackageCoverage>> getCoverage() {
                return coverageMap;
            }
        };

        run.addAction(new CoverageBuildAction(buildCoverage));

        if (deltaThreshold.isTargetMet(buildCoverage) != 1) {
            logger.println("[Code Coverage] Delta thresholds not met. Failing build.");
            run.setResult(Result.FAILURE);
        }
    }

    private CoverageThreshold getDeltaThreshold(Run run, EnvVars envVars) {
        Job parent = run.getParent();
        Run previousRun = (parent == null)? null : parent.getLastSuccessfulBuild();
        CoverageBuildAction previousAction = (previousRun == null)? null : previousRun.getAction(CoverageBuildAction.class);
        BuildCoverage lastCoverage = (previousAction == null)? null : previousAction.getTarget();

        if (lastCoverage == null) {
            return new CoverageThreshold(envVars, "0", "0", "0", "0", "0", "0", "0", "0");
        } else {
            String branchDelta = Float.toString(lastCoverage.getBranchCounter().getCoveredPercent() - CoverageThreshold.getResolvedThreshold(envVars, deltaBranch));
            String lineDelta = Float.toString(lastCoverage.getLineCounter().getCoveredPercent() - CoverageThreshold.getResolvedThreshold(envVars, deltaLine));
            String methodDelta = Float.toString(lastCoverage.getMethodCounter().getCoveredPercent() - CoverageThreshold.getResolvedThreshold(envVars, deltaMethod));
            String classDelta = Float.toString(lastCoverage.getClassCounter().getCoveredPercent() - CoverageThreshold.getResolvedThreshold(envVars, deltaClass));
            return new CoverageThreshold(envVars, branchDelta, branchDelta, lineDelta, lineDelta, methodDelta, methodDelta, classDelta, classDelta);
        }
    }

    private void persistSourceFiles(Run run, PackageCoverage coverage) {
        File buildPath = run.getRootDir();
    }

 /*   private void getCoveragePOJO(Map<String, List<PackageCoverage>> buildCoverage) {
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
*/
    private void updatePackagePOJO(CoveragePOJO.PackagePOJO packagePOJO, PackageCoverage packageCoverage) {
        for (ClassCoverage classCoverage : packageCoverage.getClasses()) {
            //packagePOJO.classes.
        }
       // packagePOJO.
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
