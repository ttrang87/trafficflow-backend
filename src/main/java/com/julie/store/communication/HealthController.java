package com.julie.store.communication;// Add this to your Spring Boot application
// HealthController.java

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/actuator/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(status);
    }
}

// Or if you're using Spring Boot Actuator, just add this to application.properties:
// management.endpoints.web.exposure.include=health
// management.endpoint.health.show-details=always