package com.julie.store.lane;

import com.julie.store.road.CenterArea;
import com.julie.store.road.RoadSize;
import com.julie.store.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class EmergencyLane extends BaseLane {
    private final ConcurrentLinkedDeque<Vehicle> lane = new ConcurrentLinkedDeque<>();
    public EmergencyLane(RoadSize size, CenterArea centerArea) {
        super(size, centerArea);
    }

    public List<Vehicle> getLane() {
        return new ArrayList<>(this.lane);
    }

    public void addVehicle(Vehicle vehicle) {
        this.lane.offer(vehicle); // or lane.add(vehicle) - both work the same
    }

    public Vehicle getLastVehicle() {
        return lane.peekLast();
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

    public void removeVehicle(Vehicle vehicle) {
        int x = vehicle.getX();
        int y = vehicle.getY();
        if (x < RoadSize.WestSize.getXLeft() ||
                x > RoadSize.EastSize.getXRight() ||
                y < RoadSize.NorthSize.getYUp() ||
                y > RoadSize.SouthSize.getYDown()) {
            this.lane.pollFirst(); // Removes and returns first element
        } else if (x > this.size.getXRight()
                || x < this.size.getXLeft()
                || y < this.size.getYUp()
                || y > this.size.getYDown()) {
            Vehicle removed = this.lane.pollFirst(); // Removes and returns first element
            if (removed != null) { // Safety check
                this.centerArea.addVehicle(removed);
                removed.changeOutLane();
            }
        }
    }


    public void flow() {
        if (lane.isEmpty()) return;

        Vehicle first = lane.peekFirst();
        if (first == null) return;

        first.changeDangerousDistance(calculateCenterDistance(first));
        first.changeSpeed(first.getInitialSpeed());
        first.moveSafe();
        removeVehicle(first);

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
                flow();
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
