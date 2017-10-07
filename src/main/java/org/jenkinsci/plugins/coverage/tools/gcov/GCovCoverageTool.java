package org.jenkinsci.plugins.coverage.tools.gcov;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coverage.CoverageTool;
import org.jenkinsci.plugins.coverage.model.PackageCoverage;
import org.jenkinsci.plugins.coverage.model.ToolCoverage;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.PrintStream;
import java.util.ArrayList;

public class GCovCoverageTool extends CoverageTool {
    private static final String TOOL_NAME = "GCov";

    @DataBoundConstructor
    public GCovCoverageTool() {}

    @Override
    protected ToolCoverage perform(EnvVars envVars, FilePath workspace, PrintStream logger) throws Exception {
        return new ToolCoverage(TOOL_NAME, new ArrayList<PackageCoverage>()); //TODO
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
            return TOOL_NAME;
        }
    }
}
