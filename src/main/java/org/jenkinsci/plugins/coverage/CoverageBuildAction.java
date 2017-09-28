package org.jenkinsci.plugins.coverage;

import hudson.model.*;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.plugins.coverage.model.BuildCoverage;
import org.kohsuke.stapler.StaplerProxy;

import jenkins.model.RunAction2;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class CoverageBuildAction implements HealthReportingAction, StaplerProxy, RunAction2, SimpleBuildStep.LastBuildAction {
    public static final String COVERAGE_ICON_FILE = "/plugin/coverage-publisher/icons/coverage.png";
    public static final String COVERAGE_URL = "coverage";

    private final BuildCoverage buildCoverage;
    private final int healthScore;

    public CoverageBuildAction(BuildCoverage buildCoverage, int healthScore) {
        this.buildCoverage = buildCoverage;
        this.healthScore = healthScore;
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
        HealthReport report = new HealthReport(healthScore, Messages._CoveragePublisher_CoverageValues(buildCoverage.getClassCounter().getCoveredPercent(),
                buildCoverage.getMethodCounter().getCoveredPercent(), buildCoverage.getLineCounter().getCoveredPercent(), buildCoverage.getBranchCounter().getCoveredPercent()));
        return report;
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
        return Collections.singleton(new CoverageProjectAction());
    }
}
