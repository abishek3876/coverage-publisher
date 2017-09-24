package org.jenkinsci.plugins.coverage.model;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean
public interface Coverage {

    @Exported
    Counter getBranchCounter();
    @Exported
    Counter getLineCounter();
    @Exported
    Counter getMethodCounter();
    @Exported
    Counter getClassCounter();

    @ExportedBean
    class Counter {
        private final int totalCount;
        private final int coveredCount;
        public Counter(int totalCount, int coveredCount) {
            this.totalCount = totalCount;
            this.coveredCount = coveredCount;
        }

        @Exported
        public int getTotalCount() {
            return this.totalCount;
        }
        @Exported
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
