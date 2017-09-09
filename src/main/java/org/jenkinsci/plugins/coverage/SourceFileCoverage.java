package org.jenkinsci.plugins.coverage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jenkinsci.plugins.coverage.tools.SourceCoverageInfo;
import org.springframework.web.util.HtmlUtils;

public class SourceFileCoverage {
	
	private final String sourceName;
	private final File sourceFile;
	private final CoverageTool coverageTool;
	private final SourceCoverageInfo sourceCoverageInfo;
	
	private String sourceCoverageHtml;
	
	private static final String TEMPLATE_TABLE_ROW = "<tr><td>%d</td><td>%s</td><td %s>%s</td></tr>";
	
	public SourceFileCoverage(String sourceName, File sourceFile, CoverageTool coverageTool) {
		this.sourceName = sourceName;
		this.sourceFile = sourceFile;
		this.coverageTool = coverageTool;
		this.sourceCoverageInfo = null;//coverageTool.getCoverageInfoForSource(sourceName, sourceFile);
	}
	
	private void parseSourceFile() throws IOException {
		try(BufferedReader sourceReader = new BufferedReader(new FileReader(sourceFile))) {
			StringBuilder sourceHtmlBuilder = new StringBuilder("<table style=\"white-space:pre;\">");
			String sourceLine;
			int i = 1;
			while((sourceLine = sourceReader.readLine()) != null) {
				String cssStyle = sourceCoverageInfo.getCoverageStatusForLine(i).cssStyle;
				String sourceHtml = HtmlUtils.htmlEscape(sourceLine);
				int coveredBranchCount = sourceCoverageInfo.getCoveredBranchCountForLine(i);
				int totalBranchCount = sourceCoverageInfo.getTotalBranchCountForLine(i);
				
				String branchRatio = "";
				if (totalBranchCount > 0) {
					branchRatio = coveredBranchCount + "/" + totalBranchCount;
				}
				String line = String.format(TEMPLATE_TABLE_ROW, i, branchRatio, cssStyle, sourceHtml);
				sourceHtmlBuilder.append(line);
				
				i++;
			}
			sourceHtmlBuilder.append("</table>");
			sourceCoverageHtml = sourceHtmlBuilder.toString();
		}
	}
	
	
}
