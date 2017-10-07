package org.jenkinsci.plugins.coverage;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import org.jenkinsci.plugins.coverage.model.CoverageType;

import java.io.Serializable;
import java.util.*;

public class CoverageProjectAction implements Action {
    public final List<Map<String, Serializable>> coverageTrend;

    /*package*/ CoverageProjectAction(Job<?,?> job) {
        List<Map<String, Serializable>> coverageTrend = new ArrayList<>();
        for (Run<?, ?> run = job.getLastBuild(); run != null; run = run.getPreviousBuild()) {
            if (run.isBuilding())
                continue;
            CoverageBuildAction runAction = run.getAction(CoverageBuildAction.class);
            if (runAction == null)
                continue;
            Map<String, Serializable> coverageData = new HashMap<>();
            coverageData.put("Build", run.getDisplayName());
            for (CoverageType type : CoverageType.values()) {
                float coveredPercent = CoverageThreshold.getCoveredPercent(runAction.getCoverageSummary().get(type));
                coverageData.put(type.name(), String.format("%.1f", coveredPercent));
            }
            coverageTrend.add(Collections.unmodifiableMap(coverageData));
        }
        Collections.reverse(coverageTrend);
        this.coverageTrend = Collections.unmodifiableList(coverageTrend);
    }

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
