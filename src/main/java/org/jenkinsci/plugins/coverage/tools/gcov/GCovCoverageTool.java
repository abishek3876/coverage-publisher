package org.jenkinsci.plugins.coverage.tools.gcov;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coverage.CoverageTool;
import org.jenkinsci.plugins.coverage.model.PackageCoverage;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.List;

public class GCovCoverageTool extends CoverageTool {

    @DataBoundConstructor
    public GCovCoverageTool() {}

    @Override
    protected List<PackageCoverage> perform(Run run, FilePath workspace, Launcher launcher, TaskListener listener) throws Exception {
        return new ArrayList<>(); //TODO
    }

    @Override
    public String toString() {
        return "TODO"; //TODO
    }

    @Extension
    @Symbol("gcovTool")
    public static class DescriptorImpl extends CoverageToolDescriptor {
        @Override
        public String getDisplayName() {
            return "GCov";
        }
    }
}
