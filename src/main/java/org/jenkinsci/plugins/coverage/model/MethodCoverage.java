package org.jenkinsci.plugins.coverage.model;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class MethodCoverage implements Coverage {

    private final String methodName;

    public MethodCoverage(@Nonnull String methodName) {
        this.methodName = methodName;
    }

    @Override
    @Nonnull
    public String getName() {
        return methodName;
    }

    @Override
    @Nonnull
    public CoverageCounter getCoverage(CoverageType coverageType) {
        switch (coverageType) {
            case METHOD:
                CoverageCounter branchCounter = getCoverage(CoverageType.BRANCH);
                CoverageCounter lineCounter = getCoverage(CoverageType.LINE);
                int covered = (branchCounter.getCovered() > 0 || lineCounter.getCovered() > 0)? 1 : 0;
                int missed = (covered == 0)? 1 : 0;
                return new CoverageCounter(covered, missed);
            default:
                return CoverageCounter.UNKNOWN_COUNTER;
        }
    }

    @Override
    @Nonnull
    public List<Coverage> getChildren() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public String getChildrenType() {
        return "";
    }

    @Override
    public boolean isChildrenRefed() {
        return false;
    }
}
