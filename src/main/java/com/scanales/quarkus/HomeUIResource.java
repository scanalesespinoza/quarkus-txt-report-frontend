package com.scanales.quarkus;

import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Path("/home")
@Produces(MediaType.TEXT_HTML)
public class HomeUIResource {

    private static final Logger LOG = Logger.getLogger(HomeUIResource.class);

    @Inject
    ReportCache cache;

    @Inject
    Template home; // src/main/resources/templates/home.qute.html

    @GET
    public String index() {
        boolean hasSummaryReports = !cache.listReports().isEmpty();
        boolean hasExceptionReports = !cache.listExceptions().isEmpty(); // si ya lo agregaste
        LOG.infof("Rendering dashboard: summary=%s, exceptions=%s", hasSummaryReports, hasExceptionReports);

        return home
                .data("hasSummaryReports", hasSummaryReports)
                .data("hasExceptionReports", hasExceptionReports)
                .render();
    }
}
