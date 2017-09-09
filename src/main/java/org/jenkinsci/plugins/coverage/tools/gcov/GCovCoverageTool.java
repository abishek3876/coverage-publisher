package org.jenkinsci.plugins.coverage.tools.gcov;

import hudson.Extension;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coverage.CoverageTool;
import org.kohsuke.stapler.DataBoundConstructor;

public class GCovCoverageTool extends CoverageTool {

    @DataBoundConstructor
    public GCovCoverageTool() {}

    @Extension
    @Symbol("gCovTool")
    public static class DescriptorImpl extends CoverageToolDescriptor {
        @Override
        public String getDisplayName() {
            return "GCov";
        }
    }
}
