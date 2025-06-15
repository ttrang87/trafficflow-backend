package com.julie.store.communication;

import com.julie.store.communication.WebSocketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebSocketController {

    private final WebSocketService webSocketService;

    public WebSocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @PostMapping("/connect")
    public ResponseEntity<String> startSimulation() {
        try {
            webSocketService.startSimulation();
            return ResponseEntity.ok("Simulation started successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to start simulation: " + e.getMessage());
        }
    }

}