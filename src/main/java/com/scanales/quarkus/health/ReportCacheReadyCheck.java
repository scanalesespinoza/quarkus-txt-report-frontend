package com.scanales.quarkus.health;

import com.scanales.quarkus.ReportCache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

@ApplicationScoped
@Liveness
public class ReportCacheReadyCheck implements HealthCheck {

    @Inject
    ReportCache cache;

    /**
     * Umbral mínimo de memoria libre en bytes (por defecto 50 MiB).
     * Se puede sobreescribir con la propiedad 'app.min-free-memory'.
     */
    @ConfigProperty(name = "app.min-free-memory", defaultValue = "52428800")
    long minFreeMemory;

    @Override
    public HealthCheckResponse call() {
        Runtime rt = Runtime.getRuntime();
        long maxMemory    = rt.maxMemory();
        long totalMemory  = rt.totalMemory();
        long freeInAlloc  = rt.freeMemory();
        long usedMemory   = totalMemory - freeInAlloc;
        long freeMemory   = maxMemory - usedMemory;

        boolean up = freeMemory >= minFreeMemory;

        HealthCheckResponseBuilder builder = HealthCheckResponse
            .named("report-cache-memory")
            .withData("maxMemory",    maxMemory)
            .withData("allocated",    totalMemory)
            .withData("usedMemory",   usedMemory)
            .withData("freeMemory",   freeMemory)
            .withData("minFreeMemory",minFreeMemory);

        if (up) {
            builder.up();
        } else {
            builder.down();
        }
        return builder.build();
    }
}
