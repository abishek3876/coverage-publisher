package org.jenkinsci.plugins.coverage.model;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.List;
import java.util.Map;

@ExportedBean
public abstract class BuildCoverage implements Coverage {
    @Exported
    public abstract Map<String, List<PackageCoverage>> getCoverage();

    @Override
    public Counter getBranchCounter() {
        int totalCount = 0;
        int coveredCount = 0;
        for (List<PackageCoverage> packages : getCoverage().values()) {
            for (PackageCoverage packageCoverage : packages) {
                totalCount += packageCoverage.getBranchCounter().getTotalCount();
                coveredCount += packageCoverage.getBranchCounter().getCoveredCount();
            }
        }

        return new Counter(totalCount, coveredCount);
    }

    @Override
    public Counter getLineCounter() {
        int totalCount = 0;
        int coveredCount = 0;
        for (List<PackageCoverage> packages : getCoverage().values()) {
            for (PackageCoverage packageCoverage : packages) {
                totalCount += packageCoverage.getLineCounter().getTotalCount();
                coveredCount += packageCoverage.getLineCounter().getCoveredCount();
            }
        }

        return new Counter(totalCount, coveredCount);
    }

    @Override
    public Counter getMethodCounter() {
        int totalCount = 0;
        int coveredCount = 0;
        for (List<PackageCoverage> packages : getCoverage().values()) {
            for (PackageCoverage packageCoverage : packages) {
                totalCount += packageCoverage.getMethodCounter().getTotalCount();
                coveredCount += packageCoverage.getMethodCounter().getCoveredCount();
            }
        }

        return new Counter(totalCount, coveredCount);
    }

    @Override
    public Counter getClassCounter() {
        int totalCount = 0;
        int coveredCount = 0;
        for (List<PackageCoverage> packages : getCoverage().values()) {
            for (PackageCoverage packageCoverage : packages) {
                totalCount += packageCoverage.getClassCounter().getTotalCount();
                coveredCount += packageCoverage.getClassCounter().getCoveredCount();
            }
        }

        return new Counter(totalCount, coveredCount);
    }
}
