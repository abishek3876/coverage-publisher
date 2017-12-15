package org.jenkinsci.plugins.coverage;

import hudson.model.Api;
import hudson.model.Run;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@ExportedBean
public class ReportRenderer {

    @Exported
    public final Map<String, Object> coverageData;
    @Nonnull
    public final Run<?, ?> run;
    @Nonnull
    private final String path;

    public ReportRenderer(@Nonnull Run<?, ?> run, @Nonnull String path) {
        this.run = run;
        this.path = path;
        this.coverageData = getCoverageDataJSON();
    }

    private Map<String, Object> getCoverageDataJSON() {
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

    public String getCoverageData() {
        return new JSONObject(coverageData).toString();
    }

    public Api getApi() {
        return new Api(this);
    }

    public ReportRenderer getDynamic(String token, StaplerRequest req, StaplerResponse rsp ) throws IOException {
        return new ReportRenderer(run, path + File.separator + token);
    }
}
