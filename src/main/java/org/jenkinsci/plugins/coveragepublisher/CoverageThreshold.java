package org.jenkinsci.plugins.coveragepublisher;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * This holds the coverage targets for marking build results and health.
 * @author Abishek_Manoharan
 * @since 1.0
 */
public class CoverageThreshold implements Serializable {
	
	private class ThresholdValues {
		private final int minThreshold;
		private final int maxThreshold;
		
		private ThresholdValues(int minThreshold, int maxThreshold) {
			this.minThreshold = getValidThreshold(0, minThreshold, 100);
			this.maxThreshold = getValidThreshold(minThreshold, maxThreshold, 100);
		}
		
		private int getValidThreshold(int lowerBound, int actual, int upperBound) {
			if (actual < lowerBound) {
				return lowerBound;
			} else if (actual > upperBound) {
				return upperBound;
			} else {
				return actual;
			}
		}
	}

	@Nullable
	private ThresholdValues classThreshold;
	@Nullable
	private ThresholdValues methodThreshold;
	@Nullable
	private ThresholdValues lineThreshold;
	@Nullable
	private ThresholdValues branchThreshold;
	@Nullable
	private ThresholdValues instructionThreshold;
	@Nullable
	private ThresholdValues complexityThreshold;

	@Nonnull
	@DataBoundSetter
	private boolean canChangeBuildStatus = true;
	@Nonnull
	@DataBoundSetter
	private boolean canChangeBuildHealth = true;
	
	@DataBoundConstructor
	public CoverageThreshold() {
	}
	
	@DataBoundSetter
	public void setClassThreshold(int minThreshold, int maxThreshold) {
		this.classThreshold = new ThresholdValues(minThreshold, maxThreshold);
	}

	@DataBoundSetter
	public void setMethodThreshold(int minThreshold, int maxThreshold) {
		this.classThreshold = new ThresholdValues(minThreshold, maxThreshold);
	}

	@DataBoundSetter
	public void setLineThreshold(int minThreshold, int maxThreshold) {
		this.classThreshold = new ThresholdValues(minThreshold, maxThreshold);
	}

	@DataBoundSetter
	public void setBranchThreshold(int minThreshold, int maxThreshold) {
		this.classThreshold = new ThresholdValues(minThreshold, maxThreshold);
	}

	@DataBoundSetter
	public void setInstructionThreshold(int minThreshold, int maxThreshold) {
		this.classThreshold = new ThresholdValues(minThreshold, maxThreshold);
	}

	@DataBoundSetter
	public void setComplexityThreshold(int minThreshold, int maxThreshold) {
		this.classThreshold = new ThresholdValues(minThreshold, maxThreshold);
	}

}
