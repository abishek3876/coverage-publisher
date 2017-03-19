package org.jenkinsci.plugins.coveragepublisher.tools;

public interface SourceCoverageInfo {
	
	public CoverageStatus getCoverageStatusForLine(int line);

	public int getCoveredBranchCountForLine(int line);

	public int getTotalBranchCountForLine(int line);
	
	public int getTotalBranchCount();
	
	public int getCoveredBranchCount();

	public static enum CoverageStatus {
		FULLY_COVERED ("style=\"BACKGROUND-COLOR:#4A9900\""),
		PARTIALLY_COVERED ("style=\"BACKGROUND-COLOR:#F5A623\""),
		NOT_COVERED ("style=\"BACKGROUND-COLOR:#C4000A\""),
		NOT_SOURCE ("");
		
		public final String cssStyle;
		
		private CoverageStatus(String cssStyle) {
			this.cssStyle = cssStyle;
		}
	}

}
