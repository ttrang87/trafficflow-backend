package com.julie.store.road;

import com.julie.store.TrafficLight;
import com.julie.store.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class Road {
    private final RoadSize size;
    private final List<Vehicle> rightLane = new ArrayList<>();
    private final TrafficLight trafficLight;
    private final CenterArea centerArea;

    public Road(RoadSize size, TrafficLight trafficLight, CenterArea centerArea) {
        this.size = size;
        this.trafficLight = trafficLight;
        this.centerArea = centerArea;
    }

    public RoadSize getRoadSize() {
        return this.size;
    }

    public TrafficLight getLight() {
        return this.trafficLight;
    }

    public List<Vehicle> getRightLane() {
        return this.rightLane;  // returns empty list if no vehicles
    }


    public void addVehicle(Vehicle vehicle) {
        this.rightLane.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        int x = vehicle.getX();
        int y = vehicle.getY();
        if (x > this.size.getXRight()
                || x < this.size.getXLeft()
                || y < this.size.getYUp()
                || y > this.size.getYDown()) {
            this.rightLane.removeFirst();
            this.centerArea.addVehicle(vehicle);
            vehicle.changeOutPosition();
        }
    }

    public void updateAllDangerousDistances() {
        for (int i = 1; i < rightLane.size(); i++) {
            Vehicle current = rightLane.get(i);
            Vehicle front = rightLane.get(i - 1);

            int dx = Math.abs(current.getX() - front.getX());
            int dy = Math.abs(current.getY() - front.getY());
            int distance = (size.ordinal() == 0 || size.ordinal() == 2) ? dy : dx;

            current.changeDangerousDistance(distance);
        }
    }

    public void greenFlow() {
        updateAllDangerousDistances();

        if (rightLane.isEmpty()) return;

        Vehicle first = rightLane.getFirst();
        first.run();  // obey traffic light
        removeVehicle(first);

        for (int i = 1; i < rightLane.size(); i++) {
            rightLane.get(i).moveSafe();  // follow behavior only
            removeVehicle(rightLane.get(i));
        }
    }

    public void yellowFlow() {
        updateAllDangerousDistances();

        if (rightLane.isEmpty()) return;

        Vehicle first = rightLane.getFirst();
        first.slow();  // obey traffic light
        removeVehicle(first);

        for (int i = 1; i < rightLane.size(); i++) {
            rightLane.get(i).moveSafe();  // follow behavior only
            removeVehicle(rightLane.get(i));
        }
    }

    public void redFlow() {
        updateAllDangerousDistances();

        if (rightLane.isEmpty()) return;

        Vehicle first = rightLane.getFirst();
        first.stop();  // obey traffic light
        removeVehicle(first);

        for (int i = 1; i < rightLane.size(); i++) {
            rightLane.get(i).moveSafe();  // follow behavior only
            removeVehicle(rightLane.get(i));
        }
    }

    public void operate() {
        while (true) {
            try {
                switch (this.trafficLight.getColor()) {
                    case "GREEN" -> this.greenFlow();
                    case "YELLOW" -> this.yellowFlow();
                    default -> this.redFlow();
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
