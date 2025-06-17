package com.scanales.quarkus;

import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class SummaryUIResource {

    private static final Logger LOG = Logger.getLogger(SummaryUIResource.class);

    @Inject
    ReportCache cache;

    @Inject
    Template summary; // src/main/resources/templates/summary.html

    @GET
    public String view() {
        LOG.info("Rendering summary page with embedded data");
        LOG.infof("Rendering summary page, %d reports en cache", cache.listReports().size());
        // Inject the list of summary DTOs into the template
        return summary
                .data("summaries", cache.listReports().values()).render();
    }
}