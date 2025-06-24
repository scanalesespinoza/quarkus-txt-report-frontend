package com.scanales.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.scanales.quarkus.ReportCache;

import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class TemplateEndpointTest {

    @Inject
    ReportCache cache;

    @BeforeEach
    void setup() {
        String rawCsv = """
                    namespace,deployment,timestamp
                    kpulse,health-behavior-simulator-sim-1,2025-06-24T04:24:04Z
                    kpulse,health-behavior-simulator-sim-3,2025-06-24T04:24:04Z
                    kpulse,health-behavior-simulator-sim-4,2025-06-24T04:24:04Z
                    percent_not_ready,,50.0
                """;

        cache.addReport("not-ready-202506240424", rawCsv);
    }

    @Test
    @DisplayName("GET / debe responder 200 OK y contener HTML")
    public void testSummaryTemplateLoads() {
        RestAssured
                .given()
                .when().get("/")
                .then()
                .statusCode(200)
                .contentType("text/html;charset=UTF-8")
                .body(containsString("<!DOCTYPE html>"))
                .body(containsString("Gráfico de % Indisponibilidad")); // texto visible en la vista
    }

    @Test
    @DisplayName("GET /report-detail sin parámetros debería fallar (400 o 404)")
    public void testReportDetailFailsWithoutParam() {
        RestAssured
                .given()
                .when().get("/report-detail")
                .then()
                .statusCode(400); // o 404 dependiendo de tu controlador
    }

    @Test
    @DisplayName("GET /report-detail?name=not-ready-202506240424 debe retornar detalle HTML")
    public void testReportDetailHtmlResponse() {
        RestAssured
                .given()
                .queryParam("name", "not-ready-202506240424")
                .when()
                .get("/report-detail")
                .then()
                .statusCode(200)
                .contentType("text/html;charset=UTF-8")
                .body(containsString("Detalle del Reporte: not-ready-202506240424"))
                .body(containsString("<table>"))
                .body(containsString("RESUMEN % Indisponibilidad"))
                .body(containsString("50.0%"));
    }
}
