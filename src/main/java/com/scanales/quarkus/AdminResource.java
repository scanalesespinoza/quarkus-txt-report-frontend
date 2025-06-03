package com.scanales.quarkus;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Path("/admin")
public class AdminResource {

    private static final Logger LOG = Logger.getLogger(AdminResource.class);

    @GET
    @Path("/restart")
    @Produces(MediaType.TEXT_PLAIN)
    public Response restart() {
        LOG.info("Received /admin/restart request. Aplicación se reiniciará en 2 segundos. Runtime data se perderá.");
        // Retornamos mensaje inmediato
        String msg = "Servidor reiniciándose. Toda información en memoria se perderá.";
        // Lanzamos un exit con retraso para que el contenedor reinicie la app
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            LOG.info("Saliendo con System.exit(0)...");
            System.exit(0);
        }, 2, TimeUnit.SECONDS);
        return Response.ok(msg).build();
    }
}
// Este recurso es para reiniciar la aplicación Quarkus desde el navegador
// Útil para pruebas rápidas, pero no recomendado en producción