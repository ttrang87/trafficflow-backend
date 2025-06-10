package com.julie.store;

import com.julie.store.lane.EmergencyLane;
import com.julie.store.lane.InboundLane;
import com.julie.store.lane.Lane;
import com.julie.store.road.CenterArea;
import com.julie.store.road.RoadService;
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

    @Async("taskExecutor")
    public void startAddingVehicles(RoadService roadService) {
        while (true) {
            try {
                roadService.randomAddVehicle();
                Thread.sleep(100); // every 100ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                System.err.println("Error in adding vehicles: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
