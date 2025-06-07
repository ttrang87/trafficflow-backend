package com.julie.store.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

@Component
public class StompPresenceEventListener {

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        System.out.println("🟢 STOMP client connected: " + event.getMessage().getHeaders());
    }

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        System.out.println("📬 STOMP client subscribed: " + event.getMessage().getHeaders());
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        System.out.println("🔴 STOMP client disconnected: " + event.getSessionId());
    }
}
