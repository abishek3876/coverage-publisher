package org.jenkinsci.plugins.coverage;

import hudson.FilePath;
import hudson.model.Run;
import org.jenkinsci.plugins.coverage.model.PackageCoverage;
import org.jenkinsci.plugins.coverage.model.SourceFileCoverage;

import java.io.*;
import java.util.List;
import java.util.Map;

public class SourceAnnotator {
	private static final String DIR_COVERAGE_SOURCES = "coverage-sources";
	private static final String TEMPLATE_TABLE_ROW = "<tr><td style=\"color:rgba(0,0,0,0.5);\">%d</td>" +
            "<td style=\"color:#4C9BD5;\">%d/%d</td><td %s>%s</td></tr>";
	private static final String TEMPLATE_TABLE_START = "<table style=\"white-space:pre;" +
            "font-family:\"LatoLatinWeb\", \"Lato\", \"Helvetica Neue\", Helvetica, Arial, sans-serif;\">";
	private static final String TEMPLATE_TABLE_END = "</table>";
	private static final String TEMPLATE_STYLE_FULL = "style=\"background-color:#78B037;color:#FFFFFF;\"";
	private static final String TEMPLATE_STYLE_EMPTY = "style=\"background-color:#D54C53;color:#FFFFFF;\"";
    private static final String TEMPLATE_STYLE_PARTIAL = "style=\"background-color:#F5A623;color:#FFFFFF;\"";
    private static final String TEMPLATE_STYLE_DEFAULT = "";

	private final File sourcesRootDir;
	private final Map<String, List<PackageCoverage>> buildCoverage;
	private final PrintStream logger;
	
	public SourceAnnotator(Run run, PrintStream logger, Map<String, List<PackageCoverage>> buildCoverage) {
		this.sourcesRootDir = new File(run.getRootDir(), DIR_COVERAGE_SOURCES);
		this.buildCoverage = buildCoverage;
		this.logger = logger;
	}

	public void process() throws IOException, InterruptedException {
	    for (List<PackageCoverage> packageCoverages : buildCoverage.values()) {
	        if (packageCoverages != null) {
	            for (PackageCoverage packageCoverage : packageCoverages) {
                    File packageDir = new File(sourcesRootDir, normalizePackageName(packageCoverage.getPackageName()));

                    if (!packageDir.exists() && packageDir.mkdirs()) {
                        throw new IOException("Unable to create source directory");
                    }

                    processPackageCoverage(packageDir, packageCoverage);
                }
            }
        }
    }

    private void processPackageCoverage(File packageDir, PackageCoverage packageCoverage) throws InterruptedException {
	    for (SourceFileCoverage sourceFileCoverage : packageCoverage.getSourceFiles()) {
	        FilePath sourceFilePath = sourceFileCoverage.getSourceFilePath();
	        File file = new File (packageDir, sourceFilePath.getName());
	        try {
                if (!sourceFilePath.exists() || sourceFilePath.isDirectory()) {
                    logger.println("[CodeCoverage] Source file not present: " + sourceFilePath.getName());
                    continue;
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                sourceFilePath.copyTo(outputStream);
                String[] lines = outputStream.toString().split("\\r?\\n", -1);
                StringBuilder sourceHtml = new StringBuilder(TEMPLATE_TABLE_START);
                for (int i = 0; i < lines.length; i++) {
                    sourceHtml.append(String.format(TEMPLATE_TABLE_ROW, i,
                            sourceFileCoverage.getCoveredBranchCountForLine(i),
                            sourceFileCoverage.getTotalBranchCountForLine(i),
                            getStyleForCoverageStatus(sourceFileCoverage.getCoverageStatusForLine(i)), lines[i]));
                }
                sourceHtml.append(TEMPLATE_TABLE_END);
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(sourceHtml.toString());
                }
            } catch (IOException e) {
	            logger.println("[CodeCoverage] Exception copying source file: " + sourceFilePath.getName());
	            e.printStackTrace(logger);
            }
        }
    }

    private static String getStyleForCoverageStatus(SourceFileCoverage.CoverageStatus coverageStatus) {
	    switch (coverageStatus) {
            case FULLY_COVERED:
                return TEMPLATE_STYLE_FULL;
            case NOT_COVERED:
                return TEMPLATE_STYLE_EMPTY;
            case PARTIALLY_COVERED:
                return TEMPLATE_STYLE_PARTIAL;
            default:
                return TEMPLATE_STYLE_DEFAULT;
        }
    }

    private static String normalizePackageName(String packageName) {
        if (packageName == null) {
            return "default";
        }
        packageName = packageName.replaceAll("\\s", "");
        packageName = packageName.replaceAll("[/\\\\]", ".");
        if (packageName.startsWith(".")) {
            packageName = packageName.substring(1);
        }
        if (packageName.endsWith(".")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }
        return packageName.length() == 0? "default" : packageName;
    }
}
