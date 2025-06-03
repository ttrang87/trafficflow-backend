// SimulationLauncher.java
package com.julie.store;

import com.julie.store.TrafficLight;
import com.julie.store.road.CenterArea;
import com.julie.store.road.Road;
import com.julie.store.road.RoadService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SimulationLauncher {
    @Async
    public void startOperate(Road road) {
        road.operate();
    }


    @Async
    public void startCountdown(TrafficLight light) {
        light.countDown();
    }

    @Async
    public void startCenter(CenterArea centerArea) {
        centerArea.operate();
    }

    @Async
    public void startAddingVehicles(RoadService roadService) {
        while (true) {
            try {
                roadService.randomAddVehicle();
                Thread.sleep(1000); // run every 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
