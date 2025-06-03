package com.scanales.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.*;

@ApplicationScoped
public class ScriptService {

    private static final Logger LOG = Logger.getLogger(ScriptService.class);

    void onStart(@Observes StartupEvent ev) {
        try {
            Path scriptsDir = Paths.get("scripts");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(scriptsDir)) {
                for (Path file : stream) {
                    // process scripts...
                }
            }
        } catch (IOException e) {
            StackTraceElement origen = e.getStackTrace().length > 0 ? e.getStackTrace()[0] : null;
            LOG.info("Failed scanning scripts: " + e.toString() +
                     (origen != null ? "\n\tat " + origen.toString() : ""));
            if (LOG.isDebugEnabled()) {
                LOG.debug("Stack completo al escanear scripts:", e);
            }
        }
    }
}
