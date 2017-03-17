package org.jenkinsci.plugins.coveragepublisher;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;

public class CoveragePublisher extends Recorder implements SimpleBuildStep {
	
	@Nullable
	private String sourcePattern;
	@Nullable
	private List<CoverageReport> coverageReports;
	
	@DataBoundConstructor
	public CoveragePublisher() {
	}
	
	@DataBoundSetter
	public void setSourcePattern(String sourcePattern) {
		this.sourcePattern = sourcePattern;
	}
	
	@DataBoundSetter
	public void setCoverageReports(List<CoverageReport> coverageReports) {
		this.coverageReports = coverageReports;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
			throws InterruptedException, IOException {
		// TODO Auto-generated method stub

	}
	
	@Override
	public DescriptorImpl getDescriptor() {
		return DESCRIPTOR;
	}
	
    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
		
		@Override
		@SuppressWarnings("rawtypes")
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return Messages.CoveragePublisher_DisplayName();
		}
		
	}
}
