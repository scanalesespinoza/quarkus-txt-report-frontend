package com.scanales.quarkus;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class ReportCache {

    // Map<reportName, Report>, LinkedHashMap para mantener orden de inserción
    private Map<String, Report> cache;

    // Límite de reportes: lo calculamos en base a 1.6GB / 100KB ≈ 16000
    // De todas formas, podemos parametrizarlo
    @ConfigProperty(name = "app.max-reports")
    int maxReports;

    @PostConstruct
    void init() {
        cache = new LinkedHashMap<>(maxReports + 1, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Report> eldest) {
                return size() > maxReports;
            }
        };
    }

    public synchronized void addReport(String name, String rawCsv) {
        // Si existe, reemplaza; si no, inserta. 
        // El LinkedHashMap eliminará los más viejos automáticamente si supera maxReports.
        Report r = new Report(name, rawCsv, Instant.now());
        cache.put(name, r);
    }

    public synchronized Map<String, Report> listReports() {
        return cache;
    }
}
