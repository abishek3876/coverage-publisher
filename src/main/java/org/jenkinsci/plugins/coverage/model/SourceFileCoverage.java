package org.jenkinsci.plugins.coverage.model;

import hudson.FilePath;

public abstract class SourceFileCoverage {
    public abstract FilePath getSourceFilePath();

    enum CoverageStatus {
        FULLY_COVERED,
        PARTIALLY_COVERED,
        NOT_COVERED,
        NOT_SOURCE
    }
}
