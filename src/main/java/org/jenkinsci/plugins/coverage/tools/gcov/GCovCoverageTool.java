package org.jenkinsci.plugins.coverage.tools.gcov;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coverage.CoverageTool;
import org.jenkinsci.plugins.coverage.Utils;
import org.jenkinsci.plugins.coverage.model.PackageCoverage;
import org.jenkinsci.plugins.coverage.model.ToolCoverage;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class GCovCoverageTool extends CoverageTool {
    private static final String TOOL_NAME = "GCov";

    @DataBoundSetter
    public String coverageName = TOOL_NAME;
    @DataBoundSetter
    public String dataIncludePattern = "**";
    @DataBoundSetter
    public String dataExcludePattern = "";
    @DataBoundSetter
    public String gcovIncludePattern = "**";
    @DataBoundSetter
    public String gcovExcludePattern = "";
    @DataBoundSetter
    public String objectDirectory;
    @DataBoundSetter
    public String rootDirectory = ".";
    @DataBoundSetter
    public String gcovExecutablePath = "gcov";

    @DataBoundConstructor
    public GCovCoverageTool() {}

    @Override
    protected ToolCoverage perform(EnvVars envVars, FilePath workspace, PrintStream logger) throws Exception {
        if (objectDirectory != null) {
            List<FilePath> objectDirectories = Utils.resolveDirectories(objectDirectory, workspace, envVars);
        }
        Utils.resolveDirectories(rootDirectory, workspace, envVars);
        FilePath rootPath = workspace.child(rootDirectory);

        return null;
    }

    private void getDataFiles() {

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
