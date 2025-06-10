package com.julie.store;

import com.julie.store.lane.EmergencyLane;
import com.julie.store.lane.InboundLane;
import com.julie.store.lane.Lane;
import com.julie.store.road.CenterArea;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SimulationLauncher {

    @Async("taskExecutor")
    public void startLane(Lane lane) {
        lane.operate();
    }

    @Async("taskExecutor")
    public void startEmergencyLane(EmergencyLane lane) {
        lane.operate();
    }

    @Async("taskExecutor")
    public void startInboundLane(InboundLane lane) {
        lane.operate();
    }

    @Async("taskExecutor")
    public void startCountdown(TrafficLight light) {
        light.countDown();
    }

    @Async("taskExecutor")
    public void startCenter(CenterArea centerArea) {
        centerArea.operate();
    }


}
