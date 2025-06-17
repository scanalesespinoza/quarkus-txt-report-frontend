package com.scanales.quarkus;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class ReportCache {
    private static final Logger LOG = Logger.getLogger(ReportCache.class);

    private Map<String, Report> cache;

    @ConfigProperty(name = "app.max-reports")
    int maxReports;

    @PostConstruct
    void init() {
        LOG.infof("Inicializando ReportCache con maxReports=%d", maxReports);
        cache = new LinkedHashMap<>(maxReports + 1, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Report> eldest) {
                boolean evict = size() > maxReports;
                if (evict) {
                    LOG.warnf("Evicting oldest report '%s'; cacheSize before eviction=%d", 
                        eldest.getKey(), size());
                }
                return evict;
            }
        };
    }

    public synchronized void addReport(String name, String rawCsv) {
        Report r = new Report(name, rawCsv, Instant.now());
        cache.put(name, r);
        LOG.debugf("addReport('%s') → cacheSize=%d", name, cache.size());
        logMemory();
    }

    public synchronized Map<String, Report> listReports() {
        return cache;
    }

    /** Muestra en DEBUG el heap usado y libre */
    private void logMemory() {
        long max   = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free  = Runtime.getRuntime().freeMemory();
        LOG.debugf("Memoria JVM (bytes): max=%d, total=%d, free=%d, used=%d",
            max, total, free, (total - free));
    }
}
