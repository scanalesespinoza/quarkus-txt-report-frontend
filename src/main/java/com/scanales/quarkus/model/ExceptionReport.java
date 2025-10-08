package com.scanales.quarkus.model;

import java.time.LocalDateTime;

public class ExceptionReport {
    private String id;
    private String message;
    private String stacktrace;
    private LocalDateTime timestamp;

    public ExceptionReport() {}

    public ExceptionReport(String id, String message, String stacktrace, LocalDateTime timestamp) {
        this.id = id;
        this.message = message;
        this.stacktrace = stacktrace;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
