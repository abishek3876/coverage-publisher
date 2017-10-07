package org.jenkinsci.plugins.coverage.model;

public final class CoverageCounter {
    public static final CoverageCounter UNKNOWN_COUNTER = new CoverageCounter(0, 0);

    private final int covered;
    private final int missed;

    public CoverageCounter(int covered, int missed) {
        this.covered = covered;
        this.missed = missed;
    }

    public int getCovered() {
        return this.covered;
    }
    public int getMissed() {
        return this.missed;
    }
}
