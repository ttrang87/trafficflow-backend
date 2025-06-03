package com.julie.store.road;

import com.julie.store.vehicle.Vehicle;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CenterArea {
    private final List<Vehicle> centerArea = new ArrayList<>();

    public void addVehicle(Vehicle vehicle) {
        centerArea.add(vehicle);
    }

    public List<Vehicle> getCenterArea() {
        return centerArea;
    }

    public void operate() {
        while(true) {
            try {
                for (Vehicle vehicle: centerArea) {
                    vehicle.moveOut();
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        };
    }
}
