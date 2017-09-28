package org.jenkinsci.plugins.coverage.model;

import org.jenkinsci.plugins.coverage.Messages;

import java.util.List;
import java.util.Map;

public final class BuildCoverage implements Coverage {

    private final Counter branchCounter;
    private final Counter lineCounter;
    private final Counter methodCounter;
    private final Counter classCounter;
    private final Map<String, List<PackageCoverage>> coveragesMap;

    public BuildCoverage(Map<String, List<PackageCoverage>> coveragesMap) {
        this.coveragesMap = coveragesMap;
        this.branchCounter = computeBranchCounter();
        this.lineCounter = computeLineCounter();
        this.methodCounter = computeMethodCounter();
        this.classCounter = computeClassCounter();
    }

    private Counter computeBranchCounter() {
        int totalCount = 0;
        int coveredCount = 0;
        for (List<PackageCoverage> packages : coveragesMap.values()) {
            for (PackageCoverage packageCoverage : packages) {
                totalCount += packageCoverage.getBranchCounter().getTotalCount();
                coveredCount += packageCoverage.getBranchCounter().getCoveredCount();
            }
        }

        return new Counter(totalCount, coveredCount);
    }

    private Counter computeLineCounter() {
        int totalCount = 0;
        int coveredCount = 0;
        for (List<PackageCoverage> packages : coveragesMap.values()) {
            for (PackageCoverage packageCoverage : packages) {
                totalCount += packageCoverage.getLineCounter().getTotalCount();
                coveredCount += packageCoverage.getLineCounter().getCoveredCount();
            }
        }

        return new Counter(totalCount, coveredCount);
    }

    private Counter computeMethodCounter() {
        int totalCount = 0;
        int coveredCount = 0;
        for (List<PackageCoverage> packages : coveragesMap.values()) {
            for (PackageCoverage packageCoverage : packages) {
                totalCount += packageCoverage.getMethodCounter().getTotalCount();
                coveredCount += packageCoverage.getMethodCounter().getCoveredCount();
            }
        }

        return new Counter(totalCount, coveredCount);
    }

    private Counter computeClassCounter() {
        int totalCount = 0;
        int coveredCount = 0;
        for (List<PackageCoverage> packages : coveragesMap.values()) {
            for (PackageCoverage packageCoverage : packages) {
                totalCount += packageCoverage.getClassCounter().getTotalCount();
                coveredCount += packageCoverage.getClassCounter().getCoveredCount();
            }
        }

        return new Counter(totalCount, coveredCount);
    }

    @Override
    public String getName() {
        return Messages.CoveragePublisher_CoverageReport();
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
