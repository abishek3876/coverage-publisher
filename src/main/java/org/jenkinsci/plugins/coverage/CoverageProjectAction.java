package org.jenkinsci.plugins.coverage;

import hudson.model.Action;
import hudson.model.Api;
import hudson.model.Job;
import hudson.model.Run;
import org.jenkinsci.plugins.coverage.model.CoverageType;
import org.json.JSONArray;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExportedBean
public class CoverageProjectAction implements Action {
    public final Job<?,?> job;

    @SuppressWarnings("WeakerAccess")
    @Exported
    public final List<Map<String, Serializable>> coverageTrend;

    /*package*/ CoverageProjectAction(Job<?,?> job) {
        this.job = job;
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
                float coveredPercent = CoverageThreshold.getCoveredPercent(runAction.coverageSummary.get(type));
                coverageData.put(type.name(), Float.valueOf(String.format("%.1f", coveredPercent)));
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

    public Api getApi() {
        return new Api(this);
    }

    public String getCoverageTrend() {
        return new JSONArray(coverageTrend).toString();
    }
}
