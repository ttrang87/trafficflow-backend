package com.julie.store.lane;

import com.julie.store.road.CenterArea;
import com.julie.store.road.RoadSize;
import com.julie.store.vehicle.CarBrand;
import com.julie.store.vehicle.Vehicle;

import java.util.List;

public abstract class BaseLane {
    protected final RoadSize size;
    protected final CenterArea centerArea;

    public BaseLane(RoadSize size, CenterArea centerArea) {
        this.size = size;
        this.centerArea = centerArea;
    }

    // Abstract methods that must be implemented
    public abstract List<Vehicle> getLane();
    public abstract void updateAllDangerousDistances();
    public abstract void operate();
    public abstract Vehicle getLastVehicle();

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

    public int calculateCenterDistance(Vehicle firstVehicle) {
        int x = firstVehicle.getX();
        int y = firstVehicle.getY();
        int length = firstVehicle.getBrand().getLength();
        String curRela = firstVehicle.getRelationship();

        int minCenterDistance = Integer.MAX_VALUE;
        for (Vehicle v : centerArea.getCenterArea()) {
            int relation = (v.getDirection() - firstVehicle.getDirection() + 4) % 4;
            String frontRela = v.getRelationship();
            boolean isSameLane = curRela.equals("LEFT") && frontRela.equals("OPPOSITE") || curRela.equals(frontRela);
            if ( isSameLane && relation == 0 || relation == 1) {
                int dx = Math.abs(v.getX() - x);
                int dy = Math.abs(v.getY() - y);
                int rawDistance = (int) Math.sqrt(dx * dx + dy * dy);
                int  addition = relation == 1 ? length / 2 + v.getBrand().getWidth() / 2 : length / 2 + v.getBrand().getLength() / 2;
                minCenterDistance = Math.min(minCenterDistance, rawDistance - addition - 4);
            }
        }

        return minCenterDistance;
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
