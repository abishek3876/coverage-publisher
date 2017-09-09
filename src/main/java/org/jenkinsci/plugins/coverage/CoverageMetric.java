package org.jenkinsci.plugins.coverage;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean
public final class CoverageMetric {
	
	@Exported
	public final int coveredCount;
	@Exported
	public final int totalCount;
	@Exported
	public final CoverageType coverageType;
	
	public CoverageMetric(CoverageType coverageType, int totalCount, int coveredCount) {
		this.coverageType = coverageType;
		this.totalCount = totalCount;
		this.coveredCount = coveredCount;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !o.getClass().equals(getClass())) {
			return false;
		}
		
		CoverageMetric other = (CoverageMetric) o;
		
		return (other.coverageType == coverageType) && (other.totalCount == totalCount) && (other.coveredCount == coveredCount);
	}

	@Override
	public int hashCode() {
		int hashCode;
		hashCode = coverageType.hashCode();
		hashCode = hashCode * 31 + totalCount;
		hashCode = hashCode * 31 + coveredCount;
		
		return hashCode;
	}
}
