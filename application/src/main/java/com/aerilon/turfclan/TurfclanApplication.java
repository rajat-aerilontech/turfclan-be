package com.aerilon.turfclan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@SpringBootApplication
public class TurfclanApplication {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

	public static void main(String[] args) {
		SpringApplication.run(TurfclanApplication.class, args);
	}

    @EventListener(ApplicationReadyEvent.class)
    public void logSwaggerLinks() {
        String normalizedContextPath = normalizeContextPath(contextPath);

        log.info("Swagger UI available at: http://localhost:{}{}/swagger-ui.html", serverPort, normalizedContextPath);
        log.info("OpenAPI docs available at: http://localhost:{}{}/v3/api-docs", serverPort, normalizedContextPath);
    }

    private String normalizeContextPath(String path) {
        if (path == null || path.isBlank() || "/".equals(path)) {
            return "";
        }

        return path.startsWith("/") ? path : "/" + path;
    }
}
