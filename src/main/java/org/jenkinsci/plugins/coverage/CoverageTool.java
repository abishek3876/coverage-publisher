package org.jenkinsci.plugins.coverage;

import hudson.EnvVars;
import hudson.ExtensionPoint;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.coverage.model.PackageCoverage;
import org.jenkinsci.plugins.coverage.model.ToolCoverage;

import java.io.PrintStream;
import java.util.List;

public abstract class CoverageTool extends AbstractDescribableImpl<CoverageTool> implements ExtensionPoint {

    @Override
    public CoverageToolDescriptor getDescriptor() {
        return (CoverageToolDescriptor) super.getDescriptor();
    }

    protected abstract ToolCoverage perform(EnvVars envVars, FilePath workspace, PrintStream logger) throws Exception;

    /**
     * Returns a readable representation of the tool name and <b>resolved</b> input parameters
     * in the form "ToolName:{parameterName:resolvedValue, parameter2Name:resolvedValue, ...}"
     * @return a string representation of the tool.
     */
    @Override
    public abstract String toString();

    public static abstract class CoverageToolDescriptor extends Descriptor<CoverageTool> {
    }
}
