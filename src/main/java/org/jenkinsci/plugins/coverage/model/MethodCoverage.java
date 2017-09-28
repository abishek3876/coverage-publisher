package org.jenkinsci.plugins.coverage.model;

public final class MethodCoverage implements Coverage {

    private final String methodName;
    private final Counter branchCounter;
    private final Counter lineCounter;
    private final Counter methodCounter;
    private final Counter classCounter;

    public MethodCoverage(String methodName, int branchTotalCount, int branchCoveredCount, int lineTotalCount, int lineCoveredCount) {
        this.methodName = methodName;
        this.branchCounter = new Counter(branchTotalCount, branchCoveredCount);
        this.lineCounter = new Counter(lineTotalCount, lineCoveredCount);
        this.methodCounter = computeMethodCounter();
        this.classCounter = computeClassCounter();
    }

    @Override
    public String getName() {
        return methodName;
    }

    @Override
    public Counter getBranchCounter() {
        return branchCounter;
    }

    @Override
    public Counter getLineCounter() {
        return lineCounter;
    }

    @Override
    public Counter getMethodCounter() {
        return methodCounter;
    }

    @Override
    public Counter getClassCounter() {
        return classCounter;
    }

    private Counter computeMethodCounter() {
        int totalCount = 1;
        int coveredCount = (branchCounter.getCoveredCount() > 0 || lineCounter.getCoveredCount() > 0)? 1 : 0;
        return new Counter(totalCount, coveredCount);
    }

    private Counter computeClassCounter() {
        int totalCount = 1;
        int coveredCount = (getMethodCounter().getCoveredCount() > 0)? 1 : 0;
        return new Counter(totalCount, coveredCount);
    }
}
