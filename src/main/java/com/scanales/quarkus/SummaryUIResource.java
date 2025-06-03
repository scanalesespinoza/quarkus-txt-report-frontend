package com.scanales.quarkus;

import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Path("/")                // AHORA la raíz "/" mostrará el resumen
@Produces(MediaType.TEXT_HTML)
public class SummaryUIResource {

    private static final Logger LOG = Logger.getLogger(SummaryUIResource.class);

    @Inject
    Template summary; // src/main/resources/templates/summary.html

    @GET
    public String view() {
        LOG.info("Rendering summary page con gráfico");
        return summary.render();
    }
}
