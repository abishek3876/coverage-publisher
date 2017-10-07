package org.jenkinsci.plugins.coverage.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ClassCoverage implements Coverage {

    private final String className;
    private final List<MethodCoverage> methodCoverages;
    private final String sourceFileName;

    public ClassCoverage(@Nonnull String className, @Nonnull List<MethodCoverage> methodCoverages, @Nullable String sourceFileName) {
        this.className = className;
        this.methodCoverages = methodCoverages;
        this.sourceFileName = sourceFileName;
    }

    @Override
    @Nonnull
    public String getName() {
        return className;
    }

    @Override
    @Nonnull
    public CoverageCounter getCoverage(CoverageType coverageType) {
        switch (coverageType) {
            case CLASS: {
                int covered = 0;
                for (Coverage methodCoverage : this.methodCoverages) {
                    if (methodCoverage.getCoverage(CoverageType.METHOD).getCovered() > 0) {
                        covered = 1;
                        break;
                    }
                }
                int missed = (covered == 0) ? 1 : 0;
                return new CoverageCounter(covered, missed);
            }
            default: {
                int covered = 0;
                int missed = 0;
                for (Coverage methodCoverage : this.methodCoverages) {
                    CoverageCounter counter = methodCoverage.getCoverage(coverageType);
                    covered += counter.getCovered();
                    missed += counter.getMissed();
                }
                return new CoverageCounter(covered, missed);
            }
        }
    }

    @Override
    @Nonnull
    public List<MethodCoverage> getChildren() {
        return this.methodCoverages;
    }

    @Nonnull
    @Override
    public String getChildrenType() {
        return "Methods";
    }

    @Override
    public boolean isChildrenRefed() {
        return false;
    }

    @Nullable
    public String getSourceFilePath() {
        return this.sourceFileName;
    }
}
