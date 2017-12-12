package org.jenkinsci.plugins.coverage;

import hudson.model.Api;
import hudson.model.Run;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@ExportedBean
public class ReportRenderer {
    public final Run<?, ?> run;

    @Exported
    public final Map<String, Object> coverageData;

    public ReportRenderer(Run<?, ?> run) {
        this.run = run;
        this.coverageData = getCoverageDataJSON();
    }

    private Map<String, Object> getCoverageDataJSON() {
        String path = getProperPathOrNull(Stapler.getCurrentRequest().getRestOfPath());

        if (path == null) {
            return Collections.emptyMap();
        }

        File coverageFile = new File(run.getRootDir(), CoveragePublisher.COVERAGE_PATH + File.separator + path + CoveragePublisher.COVERAGE_FILE_SUFFIX);
        try {
            return getCoverageDataJSON(coverageFile).toMap();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject getCoverageDataJSON(File coverageFile) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(coverageFile))) {
            JSONObject coverageJSON = new JSONObject(new JSONTokener(reader));
            if (coverageJSON.has("sourceFilePath")) {
                String sourcePath = (String) coverageJSON.get("sourceFilePath");
                File sourceFile = new File(coverageFile.getParentFile(), sourcePath);
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

    public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp ) throws IOException {
        if (token.equals("api")) {
            return getApi();
        } else {
            return this;
        }
    }

    private String getProperPathOrNull(String path) {
        if (path.endsWith("/api/json")) {
            return path.substring(0, path.length() - 9);
        } else if (path.endsWith("/api/xml")) {
            return path.substring(0, path.length() - 8);
        } else {
            return null;
        }
    }
}
