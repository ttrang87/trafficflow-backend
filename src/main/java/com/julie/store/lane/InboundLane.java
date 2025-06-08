package com.julie.store.lane;

import com.julie.store.road.RoadSize;
import com.julie.store.vehicle.CarBrand;
import com.julie.store.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InboundLane {
    private final List<Vehicle> lane = new ArrayList<>();
    private final RoadSize size;
    private InboundLane targetLane;
    private String changeLane;
    private boolean isSwitchingNow = false;

    public InboundLane(RoadSize size) {
        this.size = size;
    }

    public void setNeighbour(InboundLane lane, String changeLane) {
        this.targetLane = lane;
        this.changeLane = changeLane;
    }

    public List<Vehicle> getLane() {
        return this.lane;
    }

    public void addLane(Vehicle vehicle) {
        lane.add(vehicle);
    }



    public void changeIsSwitching(boolean newValue) {
        this.isSwitchingNow = newValue;
    }

    public int binarySearch(int target) {
        if (targetLane.getLane().isEmpty()) {
            return -1;  // No vehicles in target lane, can't find position
        }
        int direction = size.ordinal();
        int l = 0;
        int r = targetLane.getLane().size() - 1;
        int value = 0;
        while (l < r) {
            int mid = (l + r) / 2;
            Vehicle vehicle = targetLane.getLane().get(mid);
            value = (direction == 1 || direction == 3)
                    ? vehicle.getX()
                    : vehicle.getY();
            if (direction == 1 || direction == 2) {
                if (value > target) {
                    l = mid;
                } else {
                    r = mid - 1; // Go right
                }
            } else {
                if (value < target) {
                    l = mid;
                } else {
                    r = mid - 1; // Go right
                }
            }

        }

        if (l == 0) {
            Vehicle compare = targetLane.getLane().get(l);
            int secValue = (direction == 1 || direction == 3)
                    ? compare.getX()
                    : compare.getY();
            if ((secValue < target && ( direction == 1 || direction == 2)) || (secValue > target && (direction == 0 || direction == 3))){
                return -1;
            }

        }
        return l;
    }

    public void attemptChange() {
        for (int i = this.lane.size() - 1; i > 0; i--) {
            if (isSwitchingNow) return;

            Vehicle current = this.lane.get(i);
            System.out.println("Trying switch: i=" + i + ", brand=" + current.getBrand());

            int direction = size.ordinal();
            int target = (direction == 1 || direction == 3)
                    ? current.getX()
                    : current.getY();
            int largeIndex = binarySearch(target);
            int newDangerous = Integer.MAX_VALUE;
            if (largeIndex != -1) {
                Vehicle newFront = targetLane.getLane().get(largeIndex);
                CarBrand curBrand = current.getBrand();
                CarBrand frontBrand = newFront.getBrand();

                int addition = curBrand.getLength() / 2 + frontBrand.getLength() / 2;
                newDangerous = (direction == 1 || direction == 3)
                        ? Math.abs(current.getX() - newFront.getX())  - addition
                        : Math.abs(current.getY() - newFront.getY()) - addition;
            }

            if (newDangerous - current.getDangerousDistance() >= 5) {
                this.lane.remove(i);
                changeIsSwitching(true);
                targetLane.changeIsSwitching(true);
                current.switchLane(changeLane);
                targetLane.getLane().add(largeIndex + 1, current);
            }
        }
    }


    public void updateAllDangerousDistances() {
        if (lane.isEmpty()) return;
        lane.getFirst().changeDangerousDistance(Integer.MAX_VALUE); // or any large safe value

        for (int i = 1; i < lane.size(); i++) {
            Vehicle current = lane.get(i);
            Vehicle front = lane.get(i - 1);

            int dx = Math.abs(current.getX() - front.getX());
            int dy = Math.abs(current.getY() - front.getY());
            int distance = (size.ordinal() == 0 || size.ordinal() == 2) ? dy : dx;
            CarBrand curBrand = current.getBrand();
            CarBrand frontBrand = front.getBrand();

            int addition = curBrand.getLength() / 2 + frontBrand.getLength() / 2;
            current.changeDangerousDistance(distance - addition);
        }


    }

    public void printAllVehiclesInfo() {
        for (int i = 0; i < lane.size(); i++) {
            Vehicle vehicle = lane.get(i);
            System.out.println("Brand: " + vehicle.getBrand() + " Index: " + i +  " Dangerous: " + vehicle.getDangerousDistance() + " Speed: " + vehicle.getSpeed() + " x, y: " + vehicle.getX() + " " + vehicle.getY() + " IsSwitch: " + vehicle.getSwitch());
        }
        System.out.println("-------------------------------");
    }


    public void flow() {
        Iterator<Vehicle> iterator = lane.iterator();
        while (iterator.hasNext()) {
            Vehicle vehicle = iterator.next();
            vehicle.moveSafe();

            int x = vehicle.getX();
            int y = vehicle.getY();
            if (x < RoadSize.WestSize.getXLeft() ||
                    x > RoadSize.EastSize.getXRight() ||
                    y < RoadSize.NorthSize.getYUp() ||
                    y > RoadSize.SouthSize.getYDown()) {

                iterator.remove(); // ✅ safe
            }
        }
    }



    public void operate() {
        while (true) {
            try {
                updateAllDangerousDistances();
                this.flow();
                attemptChange();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
