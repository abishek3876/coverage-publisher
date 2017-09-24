package org.jenkinsci.plugins.coverage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import hudson.EnvVars;
import org.jenkinsci.plugins.coverage.model.BuildCoverage;
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
                             String minBranchThreshold, String maxBranchThreshold,
                             String minLineThreshold, String maxLineThreshold,
                             String minMethodThreshold, String maxMethodThreshold,
                             String minClassThreshold, String maxClassThreshold) {

        this.envVars = envVars;
        thresholdValuesMap.put(CoverageType.BRANCH, getThresholdValues(minBranchThreshold, maxBranchThreshold));
        thresholdValuesMap.put(CoverageType.LINE, getThresholdValues(minLineThreshold, maxLineThreshold));
        thresholdValuesMap.put(CoverageType.METHOD, getThresholdValues(minMethodThreshold, maxMethodThreshold));
        thresholdValuesMap.put(CoverageType.CLASS, getThresholdValues(minClassThreshold, maxClassThreshold));
    }

    private ThresholdValues getThresholdValues(String minThreshold, String maxThreshold) {
        return new ThresholdValues(getResolvedThreshold(envVars, minThreshold), getResolvedThreshold(envVars, maxThreshold));
    }

    public static float getResolvedThreshold(EnvVars envVars, String input) {
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

    public int isTargetMet(BuildCoverage buildCoverage) {
        int i = 1;
        float branchPercent = buildCoverage.getBranchCounter().getCoveredPercent();
        float linePercent = buildCoverage.getLineCounter().getCoveredPercent();
        float methodPercent = buildCoverage.getMethodCounter().getCoveredPercent();
        float classPercent = buildCoverage.getClassCounter().getCoveredPercent();

        ThresholdValues values = getThreshold(CoverageType.BRANCH);
        if (branchPercent < values.minThreshold) {
            return -1;
        } else if (branchPercent < values.maxThreshold) {
            i = 0;
        }

        values = getThreshold(CoverageType.LINE);
        if (linePercent < values.minThreshold) {
            return -1;
        } else if (linePercent < values.maxThreshold) {
            i = 0;
        }

        values = getThreshold(CoverageType.METHOD);
        if (methodPercent < values.minThreshold) {
            return -1;
        } else if (methodPercent < values.maxThreshold) {
            i = 0;
        }

        values = getThreshold(CoverageType.CLASS);
        if (classPercent < values.minThreshold) {
            return -1;
        } else if (classPercent < values.maxThreshold) {
            i = 0;
        }

        return i;
    }
}
