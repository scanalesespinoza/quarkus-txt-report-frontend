package com.scanales.quarkus;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Report {
    private final String name;
    private final String rawCsv;
    private final Instant uploadTime;
    private final List<ReportEntry> entries;
    private final double summaryPercentage;
    private final Instant reportTimestamp; // <-- Nuevo campo

    public Report(String name, String rawCsv, Instant uploadTime) {
        this.name = name;
        this.rawCsv = rawCsv;
        this.uploadTime = uploadTime;
        this.entries = parseEntries(rawCsv);
        this.summaryPercentage = parseSummary(rawCsv);
        this.reportTimestamp = computeReportTimestamp(this.entries);
    }

    private List<ReportEntry> parseEntries(String csv) {
        List<ReportEntry> list = new ArrayList<>();
        String[] lines = csv.split("\\r?\\n");
        if (lines.length < 2) {
            return list;
        }
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("percent_not_ready")) {
                break;
            }
            String[] cols = line.split(",", -1);
            if (cols.length >= 3) {
                list.add(new ReportEntry(cols[0], cols[1], cols[2]));
            }
        }
        return list;
    }

    private double parseSummary(String csv) {
        String[] lines = csv.split("\\r?\\n");
        // Encuentra la última línea no vacía
        String lastLine = null;
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i].trim();
            if (!line.isEmpty()) {
                lastLine = line;
                break;
            }
        }
        if (lastLine != null && lastLine.startsWith("percent_not_ready")) {
            // Formato: percent_not_ready,,<valor>
            String[] parts = lastLine.split(",");
            if (parts.length >= 3) {
                String valuePart = parts[parts.length - 1].replace("%", "").trim();
                try {
                    return Double.parseDouble(valuePart);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid percentage format in summary: " + valuePart, e);
                }
            }
        }
        return 0.0;
    }

    private Instant computeReportTimestamp(List<ReportEntry> entries) {
        Instant max = Instant.EPOCH;
        for (ReportEntry e : entries) {
            try {
                Instant i = Instant.parse(e.getTimestamp());
                if (i.isAfter(max)) {
                    max = i;
                }
            } catch (DateTimeParseException ex) {
                // Si alguna línea tiene formato erróneo, la ignoramos
            }
        }
        return max.equals(Instant.EPOCH) ? uploadTime : max;
    }

    public String getName() {
        return name;
    }

    public String getRawCsv() {
        return rawCsv;
    }

    public Instant getUploadTime() {
        return uploadTime;
    }

    public Instant getReportTimestamp() {
        return reportTimestamp;
    }

    public List<ReportEntry> getEntries() {
        return entries;
    }

    public double getSummaryPercentage() {
        return summaryPercentage;
    }
}