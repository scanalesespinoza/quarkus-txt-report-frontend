package com.scanales.quarkus;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.stream.Collectors;

@Path("/reports")
@Produces(MediaType.TEXT_PLAIN)
public class ReportResource {

    private static final Logger LOG = Logger.getLogger(ReportResource.class);

    @Inject
    ReportCache cache;

    @GET
    public Response listRedirect() {
        LOG.debug("Redirigiendo /reports a /");
        return Response.seeOther(java.net.URI.create("/")).build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll() {
        LOG.debugf("GET /reports/all invoked. Current cache size: %d", cache.listReports().size());
        return Response.ok(cache.listReports().keySet()).build();
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response simpleHealth() {
        LOG.info("GET /reports/health invoked");
        return Response.ok("{\"status\":\"OK\",\"timestamp\":\"" + Instant.now().toString() + "\"}").build();
    }

    @GET
    @Path("/upload")
    public Response uploadForm() {
        LOG.info("GET /reports/upload invoked: debería usar POST con body");
        return Response.status(Response.Status.METHOD_NOT_ALLOWED)
            .entity("Use POST /reports/upload con contenido text/plain y parámetro ?name=").build();
    }

    @GET
    @Path("/upload-example")
    public Response uploadExample() {
        String example = "Ejemplo de curl:\n" +
            "curl -X POST -H \"Content-Type: text/plain\" --data-binary @reporte.csv \"http://<host>:<port>/reports/upload?name=miReporte\"\n";
        return Response.ok(example).build();
    }

    @jakarta.ws.rs.POST
    @Path("/upload")
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadReport(InputStream bodyStream, @QueryParam("name") String name) {
        if (name == null || name.isBlank()) {
            LOG.warn("Attempted upload without 'name' parameter");
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Parámetro 'name' es obligatorio.").build();
        }
        String rawCsv;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(bodyStream, StandardCharsets.UTF_8))) {
            rawCsv = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            LOG.error("Error al leer el cuerpo del reporte: "+ e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error leyendo el reporte.").build();
        }
        try {
            cache.addReport(name, rawCsv);
            LOG.infof("Reporte '%s' almacenado. Cache size: %d", name, cache.listReports().size());
            return Response.ok("Reporte '" + name + "' subido con éxito.").build();
        } catch (Exception e) {
            LOG.error("Error al almacenar el reporte: " + e.getMessage(), e);
            // Recomendación breve: validar que el CSV esté bien formado
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error guardando el reporte. Verifica el formato CSV.").build();
        }
    }
}
// Este recurso maneja la carga de reportes en formato CSV
// y permite listar los reportes almacenados en memoria.
// La carga se hace con POST y el nombre del reporte se pasa como parámetro.
// El recurso también incluye un endpoint para ver un ejemplo de cómo cargar un reporte con curl.
// Además, tiene un endpoint de salud simple para verificar que el servicio está activo.
// Los reportes se almacenan en un caché que mantiene un límite de tamaño configurable.