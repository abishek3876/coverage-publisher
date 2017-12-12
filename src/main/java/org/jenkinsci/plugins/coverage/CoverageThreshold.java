package org.jenkinsci.plugins.coverage;

import hudson.EnvVars;
import org.jenkinsci.plugins.coverage.model.BuildCoverage;
import org.jenkinsci.plugins.coverage.model.CoverageCounter;
import org.jenkinsci.plugins.coverage.model.CoverageType;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This holds the coverage targets for marking build results and health.
 */
/*package*/ class CoverageThreshold implements Serializable {
    private static final float THRESHOLD_DEFAULT = 0;

    private static class ThresholdValues {
        private final float minThreshold;
        private final float maxThreshold;

        private ThresholdValues(float minThreshold, float maxThreshold) {
            this.minThreshold = getValidThreshold(0, minThreshold);
            this.maxThreshold = getValidThreshold(minThreshold, maxThreshold);
        }

        private float getValidThreshold(float lowerBound, float actual) {
            if (actual < lowerBound) {
                return lowerBound;
            } else if (actual > 100) {
                return 100;
            } else {
                return actual;
            }
        }

        @Override
        public String toString() {
            return String.format("(%.2f%% - %.2f%%]", minThreshold, maxThreshold);
        }
    }

    private static final ThresholdValues DEFAULT_THRESHOLD_VALUES = new ThresholdValues(THRESHOLD_DEFAULT, THRESHOLD_DEFAULT);

    private final EnvVars envVars;
    private final Map<CoverageType, ThresholdValues> thresholdValuesMap = new HashMap<>();

    public CoverageThreshold(@Nonnull EnvVars envVars,
                             String minInstructionThreshold, String maxInstructionThreshold,
                             String minBranchThreshold, String maxBranchThreshold,
                             String minLineThreshold, String maxLineThreshold,
                             String minMethodThreshold, String maxMethodThreshold,
                             String minClassThreshold, String maxClassThreshold) {

        this.envVars = envVars;
        thresholdValuesMap.put(CoverageType.INSTRUCTION, getThresholdValues(minInstructionThreshold, maxInstructionThreshold));
        thresholdValuesMap.put(CoverageType.BRANCH, getThresholdValues(minBranchThreshold, maxBranchThreshold));
        thresholdValuesMap.put(CoverageType.LINE, getThresholdValues(minLineThreshold, maxLineThreshold));
        thresholdValuesMap.put(CoverageType.METHOD, getThresholdValues(minMethodThreshold, maxMethodThreshold));
        thresholdValuesMap.put(CoverageType.CLASS, getThresholdValues(minClassThreshold, maxClassThreshold));
    }

    private ThresholdValues getThresholdValues(String minThreshold, String maxThreshold) {
        return new ThresholdValues(getResolvedThreshold(envVars, minThreshold), getResolvedThreshold(envVars, maxThreshold));
    }

    /*package*/ static float getResolvedThreshold(EnvVars envVars, String input) {
        try {
            String expandedInput = envVars.expand(input);
            return Float.parseFloat(expandedInput);
        } catch (Exception e) {
            return THRESHOLD_DEFAULT;
        }
    }

    private @Nonnull ThresholdValues getThreshold(CoverageType coverageType) {
        ThresholdValues thresholdValues = thresholdValuesMap.get(coverageType);
        return (thresholdValues == null)? DEFAULT_THRESHOLD_VALUES : thresholdValues;
    }

    @Override
    public String toString() {
        return thresholdValuesMap.toString();
    }

    /*package*/ int getScoreForCoverage(BuildCoverage buildCoverage) {
        int score = 100;

        for (CoverageType type : CoverageType.values()) {
            CoverageCounter counter = buildCoverage.getCoverage(type);
            score = computeScore(score, getThreshold(type), getCoveredPercent(counter));
        }

        return score;
    }

    /*package*/ static float getCoveredPercent(CoverageCounter counter) {
        try {
            float result = ((float) counter.getCovered() / (counter.getCovered() + counter.getMissed())) * 100;
            return Float.isNaN(result)? 0 : result;
        } catch (Exception e) {
            return 0;
        }
    }

    private int computeScore(int originalScore, ThresholdValues thresholdValues, float actualValue) {
        if (actualValue >= thresholdValues.maxThreshold) {
            return originalScore;
        } else if (actualValue < thresholdValues.minThreshold) {
            return 0;
        } else {
            double currentScore = Math.floor(100 * (actualValue - thresholdValues.minThreshold)/(thresholdValues.maxThreshold - thresholdValues.minThreshold));

            int actualScore = (int) currentScore + 1;
            actualScore = (actualScore < originalScore)? actualScore : originalScore;

            return actualScore;
        }
    }
}
