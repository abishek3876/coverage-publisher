package org.jenkinsci.plugins.coverage.model;

import javax.annotation.Nonnull;
import java.util.List;

public class PackageCoverage implements Coverage {

    private final String packageName;
    private final List<ClassCoverage> classCoverages;
    private final List<SourceFileCoverage> sourceFileCoverages;

    public PackageCoverage(@Nonnull String packageName, @Nonnull List<ClassCoverage> classCoverages, @Nonnull List<SourceFileCoverage> sourceFileCoverages) {
        this.packageName = packageName;
        this.classCoverages = classCoverages;
        this.sourceFileCoverages = sourceFileCoverages;
    }

    @Override
    @Nonnull
    public String getName() {
        return packageName;
    }

    @Override
    @Nonnull
    public CoverageCounter getCoverage(CoverageType coverageType) {
        int covered = 0;
        int missed = 0;
        for (Coverage classCoverage : this.classCoverages) {
            CoverageCounter counter = classCoverage.getCoverage(coverageType);
            covered += counter.getCovered();
            missed += counter.getMissed();
        }
        return new CoverageCounter(covered, missed);
    }

    @Override
    @Nonnull
    public List<ClassCoverage> getChildren() {
        return this.classCoverages;
    }

    @Nonnull
    @Override
    public String getChildrenType() {
        return "Classes";
    }

    @Override
    public boolean isChildrenRefed() {
        return true;
    }

    @Nonnull
    public List<SourceFileCoverage> getSourceFiles() {
        return this.sourceFileCoverages;
    }
}
