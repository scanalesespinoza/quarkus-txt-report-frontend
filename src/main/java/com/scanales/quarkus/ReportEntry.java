package com.scanales.quarkus;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ReportEntry {
    private final String namespace;
    private final String deployment;
    private final String timestamp;

    public ReportEntry(String namespace, String deployment, String timestamp) {
        this.namespace = namespace;
        this.deployment = deployment;
        this.timestamp = timestamp;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getDeployment() {
        return deployment;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
