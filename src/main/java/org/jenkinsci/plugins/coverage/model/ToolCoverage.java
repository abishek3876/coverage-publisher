package org.jenkinsci.plugins.coverage.model;

import javax.annotation.Nonnull;
import java.util.List;

public class ToolCoverage implements Coverage {

    private final String coverageName;
    private final List<PackageCoverage> packageCoverages;

    public ToolCoverage(@Nonnull String coverageName, @Nonnull List<PackageCoverage> packageCoverages) {
        this.coverageName = coverageName;
        this.packageCoverages = packageCoverages;
    }

    @Nonnull
    @Override
    public String getName() {
        return this.coverageName;
    }

    @Nonnull
    @Override
    public CoverageCounter getCoverage(CoverageType coverageType) {
        int covered = 0;
        int missed = 0;
        for (Coverage packageCoverage : this.packageCoverages) {
            CoverageCounter counter = packageCoverage.getCoverage(coverageType);
            covered += counter.getCovered();
            missed += counter.getMissed();
        }
        return new CoverageCounter(covered, missed);
    }

    @Nonnull
    @Override
    public List<PackageCoverage> getChildren() {
        return this.packageCoverages;
    }

    @Nonnull
    @Override
    public String getChildrenType() {
        return "Packages";
    }

    @Override
    public boolean isChildrenRefed() {
        return true;
    }
}
