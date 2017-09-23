package org.jenkinsci.plugins.coverage.model;

import java.util.List;

public abstract class ClassCoverage implements Coverage {
    public abstract String getClassName();
    public abstract String getSourceFileName();
    public abstract List<MethodCoverage> getMethods();

    @Override
    public Counter getBranchCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (MethodCoverage methodCoverage : getMethods()) {
            totalCount += methodCoverage.getBranchCounter().getTotalCount();
            coveredCount += methodCoverage.getBranchCounter().getCoveredCount();
        }

        return new Counter(totalCount, coveredCount);
    }

    @Override
    public Counter getLineCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (MethodCoverage methodCoverage : getMethods()) {
            totalCount += methodCoverage.getLineCounter().getTotalCount();
            coveredCount += methodCoverage.getLineCounter().getCoveredCount();
        }

        return new Counter(totalCount, coveredCount);
    }

    @Override
    public Counter getMethodCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (MethodCoverage methodCoverage : getMethods()) {
            totalCount++;
            if (methodCoverage.getMethodCounter().getCoveredCount() > 0) {
                coveredCount++;
            }
        }

        return new Counter(totalCount, coveredCount);
    }

    @Override
    public Counter getClassCounter() {
        int totalCount = 1;
        int coveredCount = (getMethodCounter().getCoveredCount() > 0)? 1 : 0;
        return new Counter(totalCount, coveredCount);
    }
}
