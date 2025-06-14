package com.julie.store.communication;

import com.julie.store.road.RoadService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final RoadService roadService;

    public WebSocketService(SimpMessagingTemplate messagingTemplate, RoadService roadService) {
        this.messagingTemplate = messagingTemplate;
        this.roadService = roadService;
    }

    @Scheduled(fixedRate = 100)
    public void sendCarUpdates() {
        messagingTemplate.convertAndSend("/topic/vehicles", roadService.getCar());
    }

    @Scheduled(fixedRate = 1000)
    public void sendTrafficLightUpdates() {
        messagingTemplate.convertAndSend("/topic/traffic-light", roadService.getTrafficLightColors());
    }

    @Scheduled(fixedRate = 100)
    public void sendLiveVehicles() {
        messagingTemplate.convertAndSend("/topic/number-of-vehicles", roadService.getCountCar());
    }

    @Scheduled(fixedRate = 100)
    public void sendTotalVehicles() {
        messagingTemplate.convertAndSend("/topic/total-number-of-vehicles", roadService.getTotalVehicle());
    }

    @Scheduled(fixedRate = 1000)
    public void sendAvgSpeed() {
        messagingTemplate.convertAndSend("/topic/avg-speed", roadService.getAvgSpeed());
    }

    @Scheduled(fixedRate = 500)
    public void sendAvgWaitTime() {
        messagingTemplate.convertAndSend("/topic/avg-wait", roadService.calculateAverageWaitTime());
    }
}
