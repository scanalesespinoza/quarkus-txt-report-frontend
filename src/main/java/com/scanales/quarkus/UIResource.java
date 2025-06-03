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
public class UIResource {

    private static final Logger LOG = Logger.getLogger(UIResource.class);

    @Inject
    Template index;

    @Inject
    ReportCache cache;

    @GET
    public String show() {
        LOG.info("Rendering main UI with " + cache.listReports().size() + " reports.");
        // index.data("reports", cache.listReports()) prepara los datos,
        // pero render() los transforma en HTML puro
        return index
                 .data("reports", cache.listReports())
                 .render();
    }
}
