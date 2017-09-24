package org.jenkinsci.plugins.coverage;

import hudson.model.*;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.plugins.coverage.model.BuildCoverage;
import org.kohsuke.stapler.StaplerProxy;

import jenkins.model.RunAction2;

import java.util.Collection;
import java.util.Collections;

public class CoverageBuildAction implements HealthReportingAction, StaplerProxy, RunAction2, SimpleBuildStep.LastBuildAction {
    public static final String COVERAGE_ICON_FILE = "/plugin/coverage-publisher/icons/coverage.png";
    public static final String COVERAGE_URL = "coverage";

    private final BuildCoverage buildCoverage;

    public CoverageBuildAction(BuildCoverage buildCoverage) {
        this.buildCoverage = buildCoverage;
    }

    @Override
    public String getIconFileName() {
        return COVERAGE_ICON_FILE;
    }

    @Override
    public String getDisplayName() {
        return Messages.CoveragePublisher_CoverageReport();
    }

    @Override
    public String getUrlName() {
        return COVERAGE_URL;
    }

    @Override
    public HealthReport getBuildHealth() {
        HealthReport report = new HealthReport();
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
    public BuildCoverage getTarget() {
        return buildCoverage;
    }

    @Override
    public Collection<? extends Action> getProjectActions() {
        //TODO Auto-generated method stub
        return Collections.EMPTY_LIST;
    }
}
