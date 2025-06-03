package com.scanales.quarkus.health;

import com.scanales.quarkus.ReportCache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;   // O @Readiness si prefieres readiness
import java.util.Optional;

@ApplicationScoped
@Liveness
public class ReportCacheReadyCheck implements HealthCheck {

    @Inject
    ReportCache cache;

    @Override
    public HealthCheckResponse call() {
        // Leemos app.max-reports configurado en application.properties
        int maxReports = Integer.parseInt(
            Optional.ofNullable(System.getProperty("app.max-reports")).orElse("16000")
        );
        int currentSize = cache.listReports().size();
        boolean up = currentSize < maxReports;

        // Construimos el HealthCheckResponse paso a paso
        HealthCheckResponseBuilder builder = HealthCheckResponse.builder()
            .name("report-cache-free-space");

        if (up) {
            builder.up();
        } else {
            builder.down();
        }
        return builder.withData("currentSize", currentSize).build();
    }
}
