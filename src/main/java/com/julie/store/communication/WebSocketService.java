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
        System.out.println("Sending vehicles update");
        messagingTemplate.convertAndSend("/topic/vehicles", roadService.getCar());
    }

    @Scheduled(fixedRate = 1000)
    public void sendTrafficLightUpdates() {

        messagingTemplate.convertAndSend("/topic/traffic-light", roadService.getTrafficLightColors());
    }
}
