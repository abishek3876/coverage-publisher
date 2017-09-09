package org.jenkinsci.plugins.coverage.tools.jacoco;

import hudson.Extension;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.coverage.CoverageTool;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class JacocoCoverageTool extends CoverageTool {

    @DataBoundConstructor
    public JacocoCoverageTool() {}

    @DataBoundSetter
    public String sourcePattern;
    @DataBoundSetter
    public String classPattern;
    @DataBoundSetter
    public String execPattern;
    @DataBoundSetter
    public String includePattern;
    @DataBoundSetter
    public String excludePattern;

    @Extension
    @Symbol("jacocoTool")
    public static class DescriptorImpl extends CoverageToolDescriptor {
        @Override
        public String getDisplayName() {
            return "Jacoco";
        }
    }
}
