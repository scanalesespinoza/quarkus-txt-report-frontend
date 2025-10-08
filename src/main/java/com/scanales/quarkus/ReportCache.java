package com.scanales.quarkus;

import com.scanales.quarkus.model.ExceptionReport;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ReportCache {
    private static final Logger LOG = Logger.getLogger(ReportCache.class);

    // Mapa de inserción-orden para poder eliminar el más antiguo fácilmente
    private LinkedHashMap<String, Report> cache;

    /**
     * Umbral de uso de heap (%) a partir del cual expulsar el elemento más antiguo.
     * p.ej. 90.0 para expulsar cuando el 90% del heap esté en uso.
     */
    @ConfigProperty(name = "app.eviction.threshold", defaultValue = "90.0")
    double evictionThreshold;

    private final Map<String, ExceptionReport> exceptionReports = new ConcurrentHashMap<>();

    public Map<String, ExceptionReport> listExceptions() {
        return exceptionReports;
    }

    public void addExceptionReport(ExceptionReport report) {
        exceptionReports.put(report.getId(), report);
    }

    @PostConstruct
    void init() {
        // inicializa mapa sin límite de tamaño
        cache = new LinkedHashMap<>();
    }

    public synchronized void addReport(String name, String rawCsv) {
        // Medimos uso de heap
        Runtime rt = Runtime.getRuntime();
        long maxHeap = rt.maxMemory();
        long usedHeap = rt.totalMemory() - rt.freeMemory();
        double usedPct = usedHeap * 100.0 / maxHeap;

        if (usedPct >= evictionThreshold && !cache.isEmpty()) {
            // expulsamos el más antiguo (primera clave en el LinkedHashMap)
            Iterator<String> it = cache.keySet().iterator();
            String eldest = it.next();
            it.remove();
            // Log de la expulsión del reporte más antiguo
            LOG.debugf("Evicted report '%s' (heap uso=%.1f%%)", eldest, usedPct);
        }

        // Insertamos/reemplazamos el reporte
        Report r = new Report(name, rawCsv, Instant.now());
        cache.put(name, r);
        logMemory();
    }

    public synchronized Map<String, Report> listReports() {
        return cache;
    }

    /** Muestra en DEBUG el heap usado y libre */
    private void logMemory() {
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        LOG.debugf("Memoria JVM (bytes): max=%d, total=%d, free=%d, used=%d",
                max, total, free, (total - free));
    }

}
