package org.jenkinsci.plugins.coverage.model;

import java.util.List;

public final class PackageCoverage implements Coverage {

    private final String packageName;
    private final Counter branchCounter;
    private final Counter lineCounter;
    private final Counter methodCounter;
    private final Counter classCounter;
    private final List<ClassCoverage> classCoverages;

    public PackageCoverage(String packageName, List<ClassCoverage> classCoverages) {
        this.packageName = packageName;
        this.classCoverages = classCoverages;
        this.branchCounter = computeBranchCounter();
        this.lineCounter = computeLineCounter();
        this.methodCounter = computeMethodCounter();
        this.classCounter = computeClassCounter();
    }

    private Counter computeBranchCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (ClassCoverage classCoverage : classCoverages) {
            totalCount += classCoverage.getBranchCounter().getTotalCount();
            coveredCount += classCoverage.getBranchCounter().getCoveredCount();
        }

        return new Counter(totalCount, coveredCount);
    }

    private Counter computeLineCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (ClassCoverage classCoverage : classCoverages) {
            totalCount += classCoverage.getLineCounter().getTotalCount();
            coveredCount += classCoverage.getLineCounter().getCoveredCount();
        }

        return new Counter(totalCount, coveredCount);
    }

    private Counter computeMethodCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (ClassCoverage classCoverage : classCoverages) {
            totalCount += classCoverage.getMethodCounter().getTotalCount();
            coveredCount += classCoverage.getMethodCounter().getCoveredCount();
        }

        return new Counter(totalCount, coveredCount);
    }

    private Counter computeClassCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (ClassCoverage classCoverage : classCoverages) {
            totalCount++;
            if (classCoverage.getClassCounter().getCoveredCount() > 0) {
                coveredCount++;
            }
        }

        return new Counter(totalCount, coveredCount);
    }

    @Override
    public String getName() {
        return packageName;
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
