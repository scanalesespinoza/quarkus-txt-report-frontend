package com.scanales.quarkus;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.jboss.logging.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Path("/service-status")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceStatusResource {

    private static final Logger LOG = Logger.getLogger(ServiceStatusResource.class);

    @Inject
    ReportCache cache;

    // Un contador expuesto en métricas Micrometer (por ejemplo)
    @Metric(name = "reports.cached", description = "Número de reportes en cache")
    Counter reportsCached;

    @GET
    public Map<String, Object> status() {
        LOG.info("GET /service-status invoked");
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());

        // Cache
        int cacheSize = cache.listReports().size();
        map.put("reportsInCache", cacheSize);
        reportsCached.inc(cacheSize - reportsCached.getCount());

        // JVM Memory
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        map.put("heapMemoryUsed", memBean.getHeapMemoryUsage().getUsed());
        map.put("heapMemoryMax", memBean.getHeapMemoryUsage().getMax());
        map.put("nonHeapMemoryUsed", memBean.getNonHeapMemoryUsage().getUsed());

        // Sistema (CPU load)
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        // Muchos impl. no exponen getProcessCpuLoad; usamos genérico
        double load = os.getSystemLoadAverage();
        map.put("systemLoadAverage", load);

        // Uptime
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        map.put("jvmUptimeMillis", uptime);

        return map;
    }
}