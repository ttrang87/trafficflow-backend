package com.julie.store.lane;

import com.julie.store.road.RoadSize;
import com.julie.store.vehicle.CarBrand;
import com.julie.store.vehicle.Vehicle;

import java.util.List;

public abstract class BaseLane {
    protected final RoadSize size;

    public BaseLane(RoadSize size) {
        this.size = size;
    }

    // Abstract methods that must be implemented
    public abstract List<Vehicle> getLane();
    public abstract void updateAllDangerousDistances();
    public abstract void operate();

    // Common method for calculating separation distance
    protected int calculateSeparationDistance(Vehicle current, Vehicle front) {
        int dx = Math.abs(current.getX() - front.getX());
        int dy = Math.abs(current.getY() - front.getY());
        int distance = (size.ordinal() == 0 || size.ordinal() == 2) ? dy : dx;
        CarBrand curBrand = current.getBrand();
        CarBrand frontBrand = front.getBrand();
        int addition = curBrand.getLength() / 2 + frontBrand.getLength() / 2;
        return distance - addition;
    }


    public void printAllVehiclesInfo() {
        List<Vehicle> vehicles = getLane();
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            System.out.println("Brand: " + vehicle.getBrand() +
                    " Index: " + i +
                    " Direction " + vehicle.getDirection() +
                    " Dangerous: " + vehicle.getDangerousDistance() +
                    " Speed: " + vehicle.getSpeed() +
                    " x, y: " + vehicle.getX() + " " + vehicle.getY() +
                    " IsSwitch: " + vehicle.getSwitch());
        }
        System.out.println("-------------------------------");
    }
}
