package org.jenkinsci.plugins.coverage;

import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.Interceptor;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;

public class ReportRenderer implements HttpResponse {
    private final File buildDir;

    public ReportRenderer(File buildDir) {
        this.buildDir = buildDir;
    }

    @Override
    public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
        rsp.getOutputStream().println("HELLOW THIS IS THE RESPONSE");
    }
}
