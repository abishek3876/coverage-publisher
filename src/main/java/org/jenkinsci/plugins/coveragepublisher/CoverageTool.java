package org.jenkinsci.plugins.coveragepublisher;

import java.util.List;

public interface CoverageTool {

	String getToolName();
	
	List<String> getSupportedSourceExtensions();
	
	
}
