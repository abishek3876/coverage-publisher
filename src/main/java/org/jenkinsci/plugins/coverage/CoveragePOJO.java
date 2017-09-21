package org.jenkinsci.plugins.coverage;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CoveragePOJO {
    Map<CoverageType, CounterPOJO> buildCoverage = getInitialCounters();
    Map<String, ToolPOJO> tools = new TreeMap<>();

    public static class ToolPOJO {
        Map<CoverageType, CounterPOJO> toolCoverage = getInitialCounters();
        Map<String, PackagePOJO> packages = new TreeMap<>();
    }

    public static class PackagePOJO {
        Map<CoverageType, CounterPOJO> packageCoverage = getInitialCounters();
        Map<String, ClassPOJO> classes = new TreeMap<>();
    }

    public static class ClassPOJO {
        Map<CoverageType, CounterPOJO> classCoverage = getInitialCounters();
        Map<String, MethodPOJO> methods = new TreeMap<>();
        String sourceFile;
    }

    public static class MethodPOJO {
        Map<CoverageType, CounterPOJO> methodCoverage = getInitialCounters();
    }

    private static Map<CoverageType, CounterPOJO> getInitialCounters() {
        Map<CoverageType, CounterPOJO> initialCounter = new HashMap<>();
        for (CoverageType type : CoverageType.values()) {
            initialCounter.put(type, new CounterPOJO());
        }
        return initialCounter;
    }

    public static void addCounters(Map<CoverageType, CounterPOJO> to, Map<CoverageType, CounterPOJO> from) {
        for (CoverageType type : CoverageType.values()) {
            CounterPOJO toCounter = to.get(type);
            if (toCounter == null) {
                toCounter = new CounterPOJO();
                to.put(type, toCounter);
            }

            CounterPOJO fromCounter = from.get(type);
            if (fromCounter != null) {
                toCounter.addTotalCount(fromCounter.totalCount);
                toCounter.addCoveredCount(fromCounter.coveredCount);
            }
        }
    }

    public static class CounterPOJO {
        private int totalCount = 0;
        private int coveredCount = 0;

        public void addTotalCount(int count) {
            this.totalCount += count;
        }
        public void addCoveredCount(int count) {
            this.coveredCount += count;
        }
    }
}
