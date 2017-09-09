package org.jenkinsci.plugins.coverage;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import hudson.EnvVars;
import hudson.model.Result;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coveragepublisher.Messages;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;

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

        EnvVars envVars = run.getEnvironment(listener);
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
