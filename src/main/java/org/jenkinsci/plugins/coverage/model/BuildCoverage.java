package org.jenkinsci.plugins.coverage.model;

import org.jenkinsci.plugins.coverage.Messages;

import javax.annotation.Nonnull;
import java.util.List;

public class BuildCoverage implements Coverage {

    private final List<ToolCoverage> toolCoverages;

    public BuildCoverage(@Nonnull List<ToolCoverage> toolCoverages) {
        this.toolCoverages = toolCoverages;
    }

    @Override
    @Nonnull
    public String getName() {
        return "";
    }

    @Nonnull
    @Override
    public CoverageCounter getCoverage(CoverageType coverageType) {
        int covered = 0;
        int missed = 0;
        for (Coverage toolCoverage : this.toolCoverages) {
            CoverageCounter counter = toolCoverage.getCoverage(coverageType);
            covered += counter.getCovered();
            missed += counter.getMissed();
        }
        return new CoverageCounter(covered, missed);
    }

    @Nonnull
    @Override
    public List<ToolCoverage> getChildren() {
        return toolCoverages;
    }

    @Nonnull
    @Override
    public String getChildrenType() {
        return "Tools";
    }

    @Override
    public boolean isChildrenRefed() {
        return true;
    }
}
