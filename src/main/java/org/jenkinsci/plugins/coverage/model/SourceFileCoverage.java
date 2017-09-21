package org.jenkinsci.plugins.coverage.model;

import hudson.FilePath;

public interface SourceFileCoverage {
    FilePath getSourceFilePath();

    int getTotalBranchCountForLine(int line);
    int getCoveredBranchCountForLine(int line);
    CoverageStatus getCoverageStatusForLine(int line);

    enum CoverageStatus {
        FULLY_COVERED,
        PARTIALLY_COVERED,
        NOT_COVERED,
        NOT_SOURCE
    }
}
