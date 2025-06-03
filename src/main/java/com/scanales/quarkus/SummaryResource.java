package com.scanales.quarkus;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/summary")
public class SummaryResource {

    @Inject
    ReportCache cache;

    public static class SummaryPoint {
        public String reportName;
        public String reportTimestamp; // <-- Usamos este en lugar de uploadTime
        public double percentage;

        public SummaryPoint(String reportName, String reportTimestamp, double percentage) {
            this.reportName = reportName;
            this.reportTimestamp = reportTimestamp;
            this.percentage = percentage;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SummaryPoint> allSummaries() {
        Map<String, Report> reports = cache.listReports();
        return reports.entrySet()
            .stream()
            .map(e -> new SummaryPoint(
                e.getKey(),
                // Usamos getReportTimestamp().toString() para el ISO exacto del CSV
                e.getValue().getReportTimestamp().toString(),
                e.getValue().getSummaryPercentage()
            ))
            .collect(Collectors.toList());
    }
}
