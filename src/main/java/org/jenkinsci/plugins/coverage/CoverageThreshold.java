package org.jenkinsci.plugins.coverage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import hudson.EnvVars;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * This holds the coverage targets for marking build results and health.
 */
public class CoverageThreshold implements Serializable {
    private static final float THRESHOLD_DEFAULT = 0;

    private static class ThresholdValues {
        private final float minThreshold;
        private final float maxThreshold;

        private ThresholdValues(float minThreshold, float maxThreshold) {
            this.minThreshold = getValidThreshold(0, minThreshold, 100);
            this.maxThreshold = getValidThreshold(minThreshold, maxThreshold, 100);
        }

        private float getValidThreshold(float lowerBound, float actual, float upperBound) {
            if (actual < lowerBound) {
                return lowerBound;
            } else if (actual > upperBound) {
                return upperBound;
            } else {
                return actual;
            }
        }

        @Override
        public String toString() {
            return String.format("(%.2f%% - %.2f%%]", minThreshold, maxThreshold);
        }
    }

    @Nonnull
    private final EnvVars envVars;

    @Nonnull
    private final Map<CoverageType, ThresholdValues> thresholdValuesMap = new HashMap<>();

    private static final ThresholdValues DEFAULT_THRESHOLD_VALUES = new ThresholdValues(THRESHOLD_DEFAULT, THRESHOLD_DEFAULT);

    public CoverageThreshold(EnvVars envVars,
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
        return new ThresholdValues(getResolvedThreshold(minThreshold), getResolvedThreshold(maxThreshold));
    }

    private float getResolvedThreshold(String input) {
        try {
            String expandedInput = envVars.expand(input);
            return Float.parseFloat(expandedInput);
        } catch (NumberFormatException e) {
            return THRESHOLD_DEFAULT;
        }
    }

    public @Nonnull ThresholdValues getThreshold(CoverageType coverageType) {
        ThresholdValues thresholdValues = thresholdValuesMap.get(coverageType);
        return (thresholdValues == null)? DEFAULT_THRESHOLD_VALUES : thresholdValues;
    }

    @Override
    public String toString() {
        return thresholdValuesMap.toString();
    }
}
