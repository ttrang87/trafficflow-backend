package com.julie.store.road;
import com.julie.store.vehicle.Vehicle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue; // ADD THIS IMPORT

@Service
public class CenterArea {
    // CHANGE 1: Replace ArrayList with ConcurrentLinkedQueue
    private final ConcurrentLinkedQueue<Vehicle> centerArea = new ConcurrentLinkedQueue<>();

    public void addVehicle(Vehicle vehicle) {
        centerArea.add(vehicle); // Same method name, works exactly the same
    }

    public List<Vehicle> getCenterArea() {
        return new ArrayList<>(centerArea); // Same - converts queue to list
    }

    public void updateAllDangerousDistances() {
        // CHANGE 2: Convert to array for safe indexing (since queue doesn't have get(i))
        Vehicle[] vehicles = centerArea.toArray(new Vehicle[0]);
        int n = vehicles.length;

        for (int i = 0; i < n; i++) {
            Vehicle current = vehicles[i]; // Use array instead of centerArea.get(i)
            int curDir = current.getDirection();
            int minDangerous = Integer.MAX_VALUE;

            for (int j = 0; j < i; j++) {
                Vehicle front = vehicles[j]; // Use array instead of centerArea.get(j)
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

            current.changeDangerousDistance(minDangerous);
        }
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
        if ("RIGHT".equals(relationship)) {
            goal.getLane1().addLane(vehicle);
        } else {
            goal.getLane2().addLane(vehicle);
        }
    }

    public void printAllVehiclesInfo() {
        System.out.println("---- Vehicles in CenterArea ----");
        for (Vehicle vehicle : centerArea) { // Same - enhanced for loop works the same
            System.out.println("Brand: " + vehicle.getBrand() + " Speed: " + vehicle.getSpeed() + " Dangerous: " + vehicle.getDangerousDistance() + " x, y: " + vehicle.getX() + " " + vehicle.getY());
        }
        System.out.println("-------------------------------");
    }

    public void operate() {
        while (true) {
            try {
                updateAllDangerousDistances();

                // CHANGE 3: Iterator works the same, but now it's thread-safe!
                Iterator<Vehicle> iter = centerArea.iterator();
                while (iter.hasNext()) {
                    Vehicle vehicle = iter.next();
                    vehicle.moveOut();
                    if (isOutOfBounds(vehicle)) {
                        // iter.remove() doesn't work well with ConcurrentLinkedQueue
                        // Use direct removal instead:
                        centerArea.remove(vehicle);
                        transferToTargetInboundLane(vehicle);
                    }
                }

                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}