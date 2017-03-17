package org.jenkinsci.plugins.coveragepublisher;

import org.kohsuke.stapler.StaplerProxy;

import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.Run;
import jenkins.model.RunAction2;

public class CoverageBuildAction implements HealthReportingAction, StaplerProxy, RunAction2 {
	private static final String GRAPH_ICON_FILE = "graph.gif";
	private static final String COVERAGE_URL = "codecoverage";
	
	private final String displayName;
	
	public CoverageBuildAction(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getIconFileName() {
		return GRAPH_ICON_FILE;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getUrlName() {
		return COVERAGE_URL + "/" + displayName;
	}

	@Override
	public HealthReport getBuildHealth() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAttached(Run<?, ?> r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoad(Run<?, ?> r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
