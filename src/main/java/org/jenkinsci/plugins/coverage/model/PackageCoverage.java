package org.jenkinsci.plugins.coverage.model;

import java.util.List;

public interface PackageCoverage {
    String getPackageName();
    List<ClassCoverage> getClasses();
    List<SourceFileCoverage> getSourceFiles();
}
