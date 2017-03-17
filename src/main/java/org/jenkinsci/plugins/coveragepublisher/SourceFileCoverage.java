package org.jenkinsci.plugins.coveragepublisher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.web.util.HtmlUtils;

public class SourceFileCoverage {
	
	private final String sourceName;
	private final File sourceFile;
	private final CoverageTool coverageTool;
	
	public SourceFileCoverage(String sourceName, File sourceFile, CoverageTool coverageTool) {
		this.sourceName = sourceName;
		this.sourceFile = sourceFile;
		this.coverageTool = coverageTool;
	}
	
	private void parseSourceFile() throws IOException {
		try(BufferedReader sourceReader = new BufferedReader(new FileReader(sourceFile))) {
			StringBuilder sourceHtmlBuilder = new StringBuilder("<code style=\"white-space:pre;\">");
			String sourceLine;
			int i = 1;
			while((sourceLine = sourceReader.readLine()) != null) {
				
				sourceHtmlBuilder.append(HtmlUtils.htmlEscape(sourceLine));
			}
			sourceHtmlBuilder.append("</code>");
		}
	}

}
