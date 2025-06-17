package com.scanales.quarkus;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/summary")
public class SummaryResource {

    @Inject
    ReportCache cache;

    @Schema(description = "Punto de resumen para el gráfico de disponibilidad")
    public static class SummaryPoint {
        @Schema(description = "Nombre del reporte", example = "report-202506171200")
        public String reportName;
        @Schema(description = "Timestamp original del reporte", example = "2025-06-17T12:00:00Z")
        public String reportTimestamp; // <-- Usamos este en lugar de uploadTime
        @Schema(description = "Porcentaje de recursos no disponibles", example = "4.5")
        public double percentage;

        public SummaryPoint() {
        }

        public SummaryPoint(String reportName, String reportTimestamp, double percentage) {
            this.reportName = reportName;
            this.reportTimestamp = reportTimestamp;
            this.percentage = percentage;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "All Summaries", description = "Devuelve un listado de reportes con su % de indisponibilidad")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SummaryPoint.class)))
    public List<SummaryPoint> getSummaries() {
        Map<String, Report> reports = cache.listReports();
        return reports.entrySet()
                .stream()
                .map(e -> new SummaryPoint(
                        e.getKey(),
                        // Usamos getReportTimestamp().toString() para el ISO exacto del CSV
                        e.getValue().getReportTimestamp().toString(),
                        e.getValue().getSummaryPercentage()))
                .collect(Collectors.toList());
    }
}
