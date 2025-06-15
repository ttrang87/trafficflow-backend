package com.julie.store.communication;

import com.julie.store.road.RoadService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final RoadService roadService;

    // Control flag for scheduling
    private volatile boolean simulationActive = false;

    public WebSocketService(SimpMessagingTemplate messagingTemplate, RoadService roadService) {
        this.messagingTemplate = messagingTemplate;
        this.roadService = roadService;
    }

    public void startSimulation() {
        if (!simulationActive) {
            System.out.println("🚦 Starting traffic simulation via REST API...");
            roadService.startSimulation();
            simulationActive = true;
            System.out.println("✅ Simulation started, WebSocket scheduling enabled");
        }
    }


    // All scheduled methods now check the flag first
    @Scheduled(fixedRate = 100)
    public void sendCarUpdates() {
        if (!simulationActive) return;
        messagingTemplate.convertAndSend("/topic/vehicles", roadService.getCar());
    }

    @Scheduled(fixedRate = 1000)
    public void sendTrafficLightUpdates() {
        if (!simulationActive) return;
        messagingTemplate.convertAndSend("/topic/traffic-light", roadService.getTrafficLightColors());
    }

    @Scheduled(fixedRate = 100)
    public void sendLiveVehicles() {
        if (!simulationActive) return;
        messagingTemplate.convertAndSend("/topic/number-of-vehicles", roadService.getCountCar());
    }

    @Scheduled(fixedRate = 100)
    public void sendTotalVehicles() {
        if (!simulationActive) return;
        messagingTemplate.convertAndSend("/topic/total-number-of-vehicles", roadService.getTotalVehicle());
    }

    @Scheduled(fixedRate = 1000)
    public void sendAvgSpeed() {
        if (!simulationActive) return;
        messagingTemplate.convertAndSend("/topic/avg-speed", roadService.getAvgSpeed());
    }

    @Scheduled(fixedRate = 500)
    public void sendAvgWaitTime() {
        if (!simulationActive) return;
        messagingTemplate.convertAndSend("/topic/avg-wait", roadService.calculateAverageWaitTime());
    }
}