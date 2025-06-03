package com.scanales.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class ReportCache {
    private final Map<String, String> cache = new LinkedHashMap<>();

    public void addReport(String name, String content) {
        cache.put(name, content);
    }

    public Map<String, String> listReports() {
        return cache;
    }
}
