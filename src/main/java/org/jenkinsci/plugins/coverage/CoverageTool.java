package org.jenkinsci.plugins.coverage;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

public abstract class CoverageTool extends AbstractDescribableImpl<CoverageTool> implements ExtensionPoint {

    @Override
    public CoverageToolDescriptor getDescriptor() {
        return (CoverageToolDescriptor) super.getDescriptor();
    }

    public static abstract class CoverageToolDescriptor extends Descriptor<CoverageTool> {
    }
}
