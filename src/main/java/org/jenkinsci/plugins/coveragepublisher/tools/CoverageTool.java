package org.jenkinsci.plugins.coveragepublisher.tools;

import java.io.File;
import java.util.List;

public interface CoverageTool {

	String getToolName();
	
	List<String> getSupportedSourceExtensions();
	
	SourceCoverageInfo getCoverageInfoForSource(String sourceName, File sourceFile);
}
