package com.scanales.quarkus;

import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Path("/exceptions")
@Produces(MediaType.TEXT_HTML)
public class ExceptionsUIResource {

    private static final Logger LOG = Logger.getLogger(ExceptionsUIResource.class);

    @Inject
    ReportCache reportCache;

    @Inject
    Template exceptions;

    @GET
    public String viewExceptions() {
        LOG.infof("Rendering exceptions dashboard with %d entries", reportCache.listExceptions().size());
        return exceptions.data("exceptions", reportCache.listExceptions().values()).render();
    }
}
