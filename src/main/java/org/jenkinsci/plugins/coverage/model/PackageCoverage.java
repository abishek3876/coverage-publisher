package org.jenkinsci.plugins.coverage.model;

import java.util.List;

public abstract class PackageCoverage implements Coverage {
    public abstract String getPackageName();
    public abstract List<ClassCoverage> getClasses();
    public abstract List<SourceFileCoverage> getSourceFiles();

    @Override
    public Counter getBranchCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (ClassCoverage classCoverage : getClasses()) {
            totalCount += classCoverage.getBranchCounter().getTotalCount();
            coveredCount += classCoverage.getBranchCounter().getCoveredCount();
        }

        return new Counter(totalCount, coveredCount);
    }

    @Override
    public Counter getLineCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (ClassCoverage classCoverage : getClasses()) {
            totalCount += classCoverage.getLineCounter().getTotalCount();
            coveredCount += classCoverage.getLineCounter().getCoveredCount();
        }

        return new Counter(totalCount, coveredCount);
    }

    @Override
    public Counter getMethodCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (ClassCoverage classCoverage : getClasses()) {
            totalCount += classCoverage.getMethodCounter().getTotalCount();
            coveredCount += classCoverage.getMethodCounter().getCoveredCount();
        }

        return new Counter(totalCount, coveredCount);
    }

    @Override
    public Counter getClassCounter() {
        int totalCount = 0;
        int coveredCount = 0;

        for (ClassCoverage classCoverage : getClasses()) {
            totalCount++;
            if (classCoverage.getClassCounter().getCoveredCount() > 0) {
                coveredCount++;
            }
        }

        return new Counter(totalCount, coveredCount);
    }
}
