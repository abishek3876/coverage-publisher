package org.jenkinsci.plugins.coverage;

import hudson.EnvVars;
import hudson.ExtensionPoint;
import hudson.FilePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.jenkinsci.plugins.coverage.model.ToolCoverage;

import java.io.PrintStream;

public abstract class CoverageTool extends AbstractDescribableImpl<CoverageTool> implements ExtensionPoint {

    @Override
    public CoverageToolDescriptor getDescriptor() {
        return (CoverageToolDescriptor) super.getDescriptor();
    }

    /**
     * Process the coverage files based on the user input.
     * This method is called on the workspace of the Jenkins Agent (as opposed to the master), so implementations can
     * directly use file operations instead of having to rely on {@link FilePath}.
     * @param envVars The environment variables present for the invocation of the tool.
     * @param workspace The path to the workspace. Since this is called on the agent that it's running, implementations
     *                  can also directly use workspace.getRemote() and use it for file operations.
     * @param logger A print stream to use for printing logs and other output.
     * @return A {@link ToolCoverage} object which contains the coverage information for this particular tool invocation.
     * @throws Exception
     */
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
