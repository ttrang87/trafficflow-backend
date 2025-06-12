package com.julie.store.lane;

import com.julie.store.TrafficLight;
import com.julie.store.road.CenterArea;
import com.julie.store.road.RoadSize;
import com.julie.store.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Lane extends BaseLane {
    private final ConcurrentLinkedDeque<Vehicle> lane = new ConcurrentLinkedDeque<>();
    private final TrafficLight trafficLight;


    public Lane(RoadSize size, TrafficLight trafficLight, CenterArea centerArea) {
        super(size, centerArea);
        this.trafficLight = trafficLight;
    }


    public List<Vehicle> getLane() {
        return new ArrayList<>(this.lane);
    }

    public void addVehicle(Vehicle vehicle) {
        this.lane.offer(vehicle);
    }

    public Vehicle getLastVehicle() {
        return lane.peekLast();
    }

    public int calculateBoundary(Vehicle firstVehicle) {
        int x = firstVehicle.getX();
        int y = firstVehicle.getY();
        int length = firstVehicle.getBrand().getLength();
        int ind = size.ordinal();

        return switch (ind) {
            case 0 -> size.getYDown() - y - length / 2 - 5;
            case 1 -> x - size.getXLeft() - length / 2 - 5;
            case 2 -> y - size.getYUp() - length / 2 - 5;
            default -> size.getXRight() - x - length / 2 - 5;
        };
    }

    public void removeVehicle(Vehicle vehicle) {
        int x = vehicle.getX();
        int y = vehicle.getY();
        if (x > this.size.getXRight()
                || x < this.size.getXLeft()
                || y < this.size.getYUp()
                || y > this.size.getYDown()) {
            // CHANGE 3: Use poll() instead of removeFirst()
            Vehicle removed = this.lane.pollFirst(); // Removes and returns first element
            if (removed != null) { // Safety check
                this.centerArea.addVehicle(removed);
                removed.changeOutLane();
            }
        }
    }

    public void updateAllDangerousDistances() {
        // Convert to array for indexed access
        Vehicle[] vehicles = lane.toArray(new Vehicle[0]);

        for (int i = 1; i < vehicles.length; i++) {
            Vehicle current = vehicles[i];
            Vehicle front = vehicles[i - 1];
            current.changeDangerousDistance(calculateSeparationDistance(current, front));
        }
    }


    public void greenFlow() {
        if (lane.isEmpty()) return;

        Vehicle first = lane.peekFirst();
        if (first == null) return; // Additional safety check

        first.changeDangerousDistance(calculateCenterDistance(first));
        first.changeSpeed(first.getInitialSpeed());
        first.moveSafe();
        removeVehicle(first);

        updateAllDangerousDistances();

        try {
            Thread.sleep(50); // Small delay to prevent immediate following
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        Vehicle[] vehicles = lane.toArray(new Vehicle[0]);
        for (int i = 1; i < vehicles.length; i++) {
            vehicles[i].moveSafe();
            removeVehicle(vehicles[i]);
        }
    }

    public void yellowFlow() {
        if (lane.isEmpty()) return;

        Vehicle first = lane.peekFirst();
        if (first == null) return;

        first.changeDangerousDistance(calculateCenterDistance(first) - 10);
        first.changeSpeed(first.getInitialSpeed() - 1);
        first.moveSafe();
        removeVehicle(first);

        updateAllDangerousDistances();

        try {
            Thread.sleep(50); // Small delay to prevent immediate following
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        Vehicle[] vehicles = lane.toArray(new Vehicle[0]);
        for (int i = 1; i < vehicles.length; i++) {
            vehicles[i].moveSafe();
            removeVehicle(vehicles[i]);
        }
    }

    public void redFlow() {
        if (lane.isEmpty()) return;

        Vehicle first = lane.peekFirst();
        if (first == null) return;

        first.changeDangerousDistance(Math.min(calculateBoundary(first), calculateCenterDistance(first)));
        first.moveSafe();

        updateAllDangerousDistances();

        Vehicle[] vehicles = lane.toArray(new Vehicle[0]);
        for (int i = 1; i < vehicles.length; i++) {
            vehicles[i].moveSafe();
            removeVehicle(vehicles[i]);
        }
    }

    public void operate() {
        while (true) {
            try {
                while (BaseLane.isPaused()) {
                    synchronized (pauseLock) {
                        pauseLock.wait();
                    }
                }
                switch (this.trafficLight.getColor()) {
                    case "GREEN" -> this.greenFlow();
                    case "YELLOW" -> this.yellowFlow();
                    default -> this.redFlow();
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Add general exception handling to prevent thread death
                System.err.println("Error in Lane.operate(): " + e.getMessage());
                e.printStackTrace();
                // Continue running instead of crashing
            }
        }
    }

}