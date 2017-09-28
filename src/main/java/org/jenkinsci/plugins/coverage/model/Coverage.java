package org.jenkinsci.plugins.coverage.model;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;

public interface Coverage extends Serializable {

    String getName();
    Counter getBranchCounter();
    Counter getLineCounter();
    Counter getMethodCounter();
    Counter getClassCounter();

    class Counter implements Serializable {
        private final int totalCount;
        private final int coveredCount;
        public Counter(int totalCount, int coveredCount) {
            this.totalCount = totalCount;
            this.coveredCount = coveredCount;
        }

        public int getTotalCount() {
            return this.totalCount;
        }
        public int getCoveredCount() {
            return this.coveredCount;
        }

        public float getCoveredPercent() {
            if (totalCount <= 0) {
                return 0;
            } else {
                return 100f * coveredCount / totalCount;
            }
        }
    }
}
