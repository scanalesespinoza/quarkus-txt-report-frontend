package com.scanales.quarkus;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

@Path("/reports")
@Produces(MediaType.TEXT_HTML)
public class ReportResource {

    private static final Logger LOG = Logger.getLogger(ReportResource.class);

    @Inject
    ReportCache cache;

    @GET
    public String list() {
        return "<script>window.location='/'</script>";
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response uploadReport(InputStream bodyStream, @QueryParam("name") String name) {
        if (name == null || name.isBlank()) {
            LOG.info("Upload failed: missing 'name' parameter for report upload request.");
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Parameter 'name' is required to upload a report.")
                           .build();
        }

        String content;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(bodyStream, StandardCharsets.UTF_8))) {
            content = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            StackTraceElement origen = e.getStackTrace().length > 0 ? e.getStackTrace()[0] : null;
            LOG.info("Failed scanning scripts: " + e.toString() +
                     (origen != null ? "\n\tat " + origen.toString() : ""));
            if (LOG.isDebugEnabled()) {
                LOG.debug("Stack completo al escanear scripts:", e);
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error processing report upload. Please try again later.")
                           .build();
        }

        try {
            cache.addReport(name, content);
            LOG.info("Report '" + name + "' uploaded successfully.");
            return Response.ok("Report '" + name + "' uploaded successfully.").build();
        } catch (Exception e) {
            StackTraceElement origen = e.getStackTrace().length > 0 ? e.getStackTrace()[0] : null;
            LOG.info("Failed storing report: " + e.toString() +
                     (origen != null ? "\n\tat " + origen.toString() : ""));
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unexpected error while adding report to cache: " + name, e);
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error storing report. Please try again later.")
                           .build();
        }
    }
}
