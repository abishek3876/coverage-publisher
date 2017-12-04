package org.jenkinsci.plugins.coverage;

import hudson.model.Api;
import hudson.model.Run;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@ExportedBean
public class ReportRenderer {
    private static final String PARAMETER_PATH = "path";
    private static final String PARAMETER_NAME = "name";

    public final Run<?, ?> run;

    @Exported
    public final Map<String, Object> coverageDataJSON;

    public ReportRenderer(Run<?, ?> run) {
        this.run = run;
        this.coverageDataJSON = getCoverageDataJSON();
    }

    private Map<String, Object> getCoverageDataJSON() {
        String path = Stapler.getCurrentRequest().getParameter(PARAMETER_PATH);
        String name = Stapler.getCurrentRequest().getParameter(PARAMETER_NAME);

        StringBuilder coveragePath = new StringBuilder(CoveragePublisher.COVERAGE_PATH);
        if (path != null && !path.isEmpty()) {
            String[] folders = path.split(" >> ");
            for (int i = 1; i < folders.length; i++) { // Starting with 1 because we don't want the root folder.
                coveragePath.append(File.separator).append(Utils.normalizeForFileName(folders[i]));
            }
        }
        if (name != null && !name.isEmpty()) {
            coveragePath.append(File.separator).append(Utils.normalizeForFileName(name));
        }
        coveragePath.append(File.separator).append(CoveragePublisher.SUMMARY_FILE);
        File coverageFile = new File(run.getRootDir(), coveragePath.toString());
        try {
            return getCoverageData(new File(run.getRootDir(), CoveragePublisher.COVERAGE_PATH), coverageFile).toMap();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject getCoverageData(File coverageRootDir, File coverageFile) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(coverageFile))) {
            JSONObject coverageJSON = new JSONObject(new JSONTokener(reader));
            if (coverageJSON.has("sourceFilePath")) {
                String sourcePath = (String) coverageJSON.get("sourceFilePath");
                File sourceFile = new File(coverageRootDir, sourcePath);
                if (sourceFile.isFile()) {
                    try (BufferedReader sourceReader = new BufferedReader(new FileReader(sourceFile))) {
                        JSONArray sourceJSON = new JSONArray(new JSONTokener(sourceReader));
                        coverageJSON.put("sourceFile", sourceJSON);
                    }
                }
            }
            return coverageJSON;
        }
    }

    public Api getApi() {
        return new Api(this);
    }
}
