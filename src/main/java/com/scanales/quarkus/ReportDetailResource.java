package com.scanales.quarkus;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/report-detail")
@Produces(MediaType.TEXT_HTML)
public class ReportDetailResource {

    private static final Logger LOG = Logger.getLogger(ReportDetailResource.class);

    @Inject
    ReportCache cache;

    @Inject
    Template detail;  // src/main/resources/templates/detail.html

    @GET
    public Response view(@QueryParam("name") String name) {
        if (name == null || name.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("El parámetro 'name' es obligatorio.")
                           .build();
        }
        Report rpt = cache.listReports().get(name);
        if (rpt == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("No se encontró el reporte con nombre: " + name)
                           .build();
        }

        // Inyectamos dos datos en la plantilla: el objeto Report entero
        TemplateInstance ti = detail
            .data("reportName", rpt.getName())
            .data("uploadTime", rpt.getUploadTime().toString())
            .data("entries", rpt.getEntries())
            .data("summary", rpt.getSummaryPercentage());

        LOG.info("Rendering detail page para reporte: " + name);
        return Response.ok(ti.render()).build();
    }
}
