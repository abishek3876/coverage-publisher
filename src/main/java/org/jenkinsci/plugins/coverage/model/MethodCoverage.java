package org.jenkinsci.plugins.coverage.model;

public abstract class MethodCoverage implements Coverage {
    public abstract String getMethodName();

    @Override
    public Counter getMethodCounter() {
        int totalCount = 1;
        int coveredCount = (getBranchCounter().getCoveredCount() > 0)? 1 : 0;
        return new Counter(totalCount, coveredCount);
    }

    @Override
    public final Counter getClassCounter() {
        int totalCount = 1;
        int coveredCount = (getMethodCounter().getCoveredCount() > 0)? 1 : 0;
        return new Counter(totalCount, coveredCount);
    }
}
