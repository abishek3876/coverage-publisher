package org.jenkinsci.plugins.coverage.model;

import java.util.List;

public final class ClassCoverage implements Coverage {

    private final String className;
    private final Counter branchCounter;
    private final Counter lineCounter;
    private final Counter methodCounter;
    private final Counter classCounter;
    private final List<MethodCoverage> methodCoverages;

    public ClassCoverage(String className, List<MethodCoverage> methodCoverages) {
        this.className = className;
        this.methodCoverages = methodCoverages;
        this.branchCounter = computeBranchCounter();
        this.lineCounter = computeLineCounter();
        this.methodCounter = computeMethodCounter();
        this.classCounter = computeClassCounter();
    }

    private Counter computeBranchCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (MethodCoverage methodCoverage : methodCoverages) {
            totalCount += methodCoverage.getBranchCounter().getTotalCount();
            coveredCount += methodCoverage.getBranchCounter().getCoveredCount();
        }

        return new Counter(totalCount, coveredCount);
    }

    private Counter computeLineCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (MethodCoverage methodCoverage : methodCoverages) {
            totalCount += methodCoverage.getLineCounter().getTotalCount();
            coveredCount += methodCoverage.getLineCounter().getCoveredCount();
        }

        return new Counter(totalCount, coveredCount);
    }

    private Counter computeMethodCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (MethodCoverage methodCoverage : methodCoverages) {
            totalCount++;
            if (methodCoverage.getMethodCounter().getCoveredCount() > 0) {
                coveredCount++;
            }
        }

        return new Counter(totalCount, coveredCount);
    }

    private Counter computeClassCounter() {
        int totalCount = 1;
        int coveredCount = (getMethodCounter().getCoveredCount() > 0)? 1 : 0;
        return new Counter(totalCount, coveredCount);
    }

    @Override
    public String getName() {
        return className;
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
}
