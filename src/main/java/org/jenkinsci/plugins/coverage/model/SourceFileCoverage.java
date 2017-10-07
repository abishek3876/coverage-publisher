package org.jenkinsci.plugins.coverage.model;

import javax.annotation.Nonnull;
import java.util.List;

public class SourceFileCoverage {
    private final String filePath;
    private final List<Line> lines;

    public SourceFileCoverage(@Nonnull String filePath, @Nonnull List<Line> lines) {
        this.filePath = filePath;
        this.lines = lines;
    }

    @Nonnull
    public String getFilePath() {
        return this.filePath;
    }

    @Nonnull
    public List<Line> getLines() {
        return this.lines;
    }

    public enum CoverageStatus {
        FULLY_COVERED (1),
        PARTIALLY_COVERED (2),
        NOT_COVERED (0),
        NOT_SOURCE (-1);

        public final int value;
        CoverageStatus(int value) {
            this.value = value;
        }
    }

    public static class Line {
        private final CoverageStatus coverageStatus;
        private final CoverageCounter branchCounter;

        public Line(@Nonnull CoverageStatus coverageStatus, @Nonnull CoverageCounter branchCounter) {
            this.coverageStatus = coverageStatus;
            this.branchCounter = branchCounter;
        }

        @Nonnull
        public CoverageStatus getCoverageStatus() {
            return this.coverageStatus;
        }

        @Nonnull
        public CoverageCounter getBranchCounter() {
            return this.branchCounter;
        }
    }
}
