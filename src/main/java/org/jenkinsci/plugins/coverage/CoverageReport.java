package org.jenkinsci.plugins.coverage;

import javax.annotation.Nonnull;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

public class CoverageReport extends AbstractDescribableImpl<CoverageReport> {
	
	@Nonnull
	private final String coverageName;
	
	@DataBoundConstructor
	public CoverageReport(String coverageName) {
		this.coverageName = coverageName;
	}
	
    @Extension
    public static class DescriptorImpl extends Descriptor<CoverageReport> {
        @Override
        public String getDisplayName() {
            return Messages.CoveragePublisher_CoverageReport();
        }
    }
}
