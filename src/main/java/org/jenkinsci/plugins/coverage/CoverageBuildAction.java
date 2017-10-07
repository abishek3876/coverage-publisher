package org.jenkinsci.plugins.coverage;

import hudson.model.*;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.plugins.coverage.model.CoverageCounter;
import org.jenkinsci.plugins.coverage.model.CoverageType;
import org.kohsuke.stapler.StaplerProxy;

import jenkins.model.RunAction2;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CoverageBuildAction implements HealthReportingAction, StaplerProxy, RunAction2, SimpleBuildStep.LastBuildAction {
    /*package*/ static final String COVERAGE_ICON_FILE = "/plugin/coverage-publisher/icons/coverage.png";
    /*package*/ static final String COVERAGE_URL = "coverage";

    private final Map<CoverageType, CoverageCounter> coverageSummary;
    private final int healthScore;
    private Run<?,?> run;

    /*package*/ CoverageBuildAction(Map<CoverageType, CoverageCounter> coverageSummary, int healthScore) {
        this.coverageSummary = coverageSummary;
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
        StringBuilder coverageValues = new StringBuilder();
        for (CoverageType type : CoverageType.values()) {
            coverageValues.append(",").append(type).append(":").append(CoverageThreshold.getCoveredPercent(coverageSummary.get(type)));
        }
        return new HealthReport(healthScore, Messages._CoveragePublisher_CoverageValues(coverageValues.substring(1)));
    }

    @Override
    public void onAttached(Run<?, ?> run) {
        this.run = run;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        this.run = run;
    }

    @Override
    public ReportRenderer getTarget() {
        return new ReportRenderer(run.getRootDir());
    }

    @Override
    public Collection<? extends Action> getProjectActions() {
        return Collections.singleton(new CoverageProjectAction(this.run.getParent()));
    }

    public Map<CoverageType, CoverageCounter> getCoverageSummary() {
        return this.coverageSummary;
    }
}
