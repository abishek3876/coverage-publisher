package org.jenkinsci.plugins.coverage;

import hudson.model.Action;

public class CoverageProjectAction implements Action {
    @Override
    public String getIconFileName() {
        return CoverageBuildAction.COVERAGE_ICON_FILE;
    }

    @Override
    public String getDisplayName() {
        return Messages.CoveragePublisher_CoverageTrend();
    }

    @Override
    public String getUrlName() {
        return CoverageBuildAction.COVERAGE_URL;
    }
}
