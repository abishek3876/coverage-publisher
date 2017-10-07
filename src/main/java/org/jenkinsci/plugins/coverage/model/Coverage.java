package org.jenkinsci.plugins.coverage.model;

import javax.annotation.Nonnull;
import java.util.List;

public interface Coverage {
    @Nonnull String getName();
    @Nonnull CoverageCounter getCoverage(CoverageType coverageType);
    @Nonnull List<? extends Coverage> getChildren();
    @Nonnull String getChildrenType();
    boolean isChildrenRefed();
}
