package com.julie.store.road;
import com.julie.store.lane.BaseLane;
import com.julie.store.vehicle.Vehicle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class CenterArea {
    // CHANGE: Use Collections.synchronizedList with LinkedList
    private final List<Vehicle> centerArea = Collections.synchronizedList(new LinkedList<>());
    protected final Object pauseLock = new Object();
    protected volatile boolean paused = false;
    protected volatile boolean running = true;


    public void addVehicle(Vehicle vehicle) {
        centerArea.add(vehicle); // Adds to end - O(1) for LinkedList
    }

    public List<Vehicle> getCenterArea() {
        synchronized (centerArea) {
            return new ArrayList<>(centerArea);
        }
    }

    public void clear() { centerArea.clear(); }

    private void updateAllDangerousDistances() {
        synchronized (centerArea) {
            int n = centerArea.size();

            for (int i = 0; i < n; i++) {
                Vehicle current = centerArea.get(i);
                int curDir = current.getDirection();
                int minDangerous = Integer.MAX_VALUE;

                for (int j = 0; j < i; j++) {
                    Vehicle front = centerArea.get(j);
                    int relationship = (curDir - front.getDirection() + 4) % 4;
                    int dx = Math.abs(current.getX() - front.getX());
                    int dy = Math.abs(current.getY() - front.getY());
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    int addition = (relationship != 2)
                            ? front.getBrand().getLength() / 2 + current.getBrand().getLength() / 2
                            : distance - 100;
                    int gap = distance - addition - 2;

                    minDangerous = Math.min(minDangerous, gap);
                }

                if (curDir == current.getGoal().getRoadSize().ordinal()) {
                    minDangerous = Math.min(minDangerous, calculateDistanceWithGoal(current));
                }

                current.changeDangerousDistance(minDangerous);
            }
        }
    }

    private int calculateDistanceWithGoal(Vehicle vehicle) {
        Vehicle lastVehicle = getGoalVehicle(vehicle);

        if (lastVehicle == null) {
            return Integer.MAX_VALUE; // No vehicles in target lane
        }

        int dx = Math.abs(vehicle.getX() - lastVehicle.getX());
        int dy = Math.abs(vehicle.getY() - lastVehicle.getY());
        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        int addition = vehicle.getBrand().getLength() / 2 +
                lastVehicle.getBrand().getLength() / 2 - 5;

        return distance - addition;

    }


    private static Vehicle getGoalVehicle(Vehicle vehicle) {
        BaseLane goalLane;
        Road goal = vehicle.getGoal();
        String relationship = vehicle.getRelationship();
        int indexCar = vehicle.getBrand().ordinal();
        boolean isEmergency = indexCar == 6 || indexCar == 7 || indexCar == 8;
        if (isEmergency) {
            goalLane = goal.getEmergencyLaneIn();
        } else {
            if ("RIGHT".equals(relationship)) {
                goalLane = goal.getLane1();
            } else {
                goalLane = goal.getLane2();
            }
        }

        if (goalLane.getLane().isEmpty()) { return null; }

        return goalLane.getLastVehicle();
    }

    private boolean isOutOfBounds(Vehicle vehicle) {
        int x = vehicle.getX();
        int y = vehicle.getY();

        return x < RoadSize.NorthSize.getXLeft()
                || x > RoadSize.NorthSize.getXRight()
                || y < RoadSize.NorthSize.getYDown()
                || y > RoadSize.SouthSize.getYUp();
    }


    private void transferToTargetInboundLane(Vehicle vehicle) {
        vehicle.changeOutLane();
        Road goal = vehicle.getGoal();
        String relationship = vehicle.getRelationship();
        int indexCar = vehicle.getBrand().ordinal();
        boolean isEmergency = indexCar == 6 || indexCar == 7 || indexCar == 8;
        if (isEmergency) {
            goal.getEmergencyLaneIn().addVehicle(vehicle);
        } else {
            if ("RIGHT".equals(relationship)) {
                goal.getLane1().addLane(vehicle);
            } else {
                goal.getLane2().addLane(vehicle);
            }
        }
    }

    public double calculateLaneAverageSpeed() {
        if (centerArea.isEmpty()) return 0.0;

        double totalSpeed = 0.0;
        for (Vehicle v : centerArea) {
            totalSpeed += v.getAvgSpeed();
        }

        return totalSpeed / centerArea.size();
    }

    public void printAllVehiclesInfo() {
        System.out.println("---- Vehicles in CenterArea ----");
        synchronized (centerArea) {
            for (Vehicle vehicle : centerArea) {
                System.out.println("Brand: " + vehicle.getBrand() +
                        " Speed: " + vehicle.getSpeed() +
                        " Dangerous: " + vehicle.getDangerousDistance() +
                        " x, y: " + vehicle.getX() + " " + vehicle.getY());
            }
        }
        System.out.println("-------------------------------");
    }

    public void setPaused(boolean paused) {
        synchronized (pauseLock) {
            this.paused = paused;
            if (!paused) {
                pauseLock.notifyAll(); // Wake up waiting thread(s)
            }
        }
    }

    public void setRunning(boolean isRunning) {
        running = isRunning;
    }

    public boolean isRunning() {
        return running;
    }


    public void operate() {
        while (running) {
            try {
                synchronized (pauseLock) {
                    while (paused && running) {
                        pauseLock.wait(); // 🚧 Block until resumed
                    }
                }

                if (!running) { break; }
                updateAllDangerousDistances();

                // FIXED: Proper iterator removal with synchronized list
                synchronized (centerArea) {
                    Iterator<Vehicle> iter = centerArea.iterator();
                    while (iter.hasNext()) {
                        Vehicle vehicle = iter.next();
                        int carIndex = vehicle.getBrand().ordinal();
                        if (carIndex == 6 || carIndex == 7 || carIndex == 8) {
                            vehicle.moveOutEmer();
                        } else {
                            vehicle.moveOut();
                        }
                        if (isOutOfBounds(vehicle)) {
                            iter.remove(); // Safe removal during iteration
                            transferToTargetInboundLane(vehicle);
                        }
                    }
                }

                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error in CenterArea.operate(): " + e.getMessage());
                e.printStackTrace();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}