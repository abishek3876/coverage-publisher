package org.jenkinsci.plugins.coveragepublisher.jacoco;

import org.jenkinsci.plugins.coveragepublisher.CoverageReport;
import org.jenkinsci.plugins.coveragepublisher.Messages;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

public class JacocoPublisher extends AbstractDescribableImpl<JacocoPublisher> {
	
	private String execPattern;
	private String classPattern;

    @Extension
    public static class DescriptorImpl extends Descriptor<JacocoPublisher> {
        @Override
        public String getDisplayName() {
            return "Jacoco";
        }
    }
}
