package com.julie.store.road;

import com.julie.store.TrafficLight;
import com.julie.store.vehicle.CarBrand;
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

    public String getRightLaneCoordinates() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < rightLane.size(); i++) {
            Vehicle v = rightLane.get(i);
            sb.append("[").append(v.getX()).append(", ").append(v.getY()).append("]");
            if (i < rightLane.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
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
            CarBrand curBrand = current.getBrand();
            CarBrand frontBrand = front.getBrand();

            int length = 80;
            if (curBrand == CarBrand.FireTruck || curBrand == CarBrand.MiniVan || frontBrand == CarBrand.MiniVan || frontBrand == CarBrand.FireTruck || curBrand == CarBrand.Police || frontBrand == CarBrand.Police) {
                length = 100;
            }
            current.changeDangerousDistance(distance - length);
        }


    }

    public void greenFlow() {
        if (rightLane.isEmpty()) return;

        Vehicle first = rightLane.getFirst();
        first.run();  // obey traffic light
        removeVehicle(first);

        updateAllDangerousDistances();

        for (int i = 1; i < rightLane.size(); i++) {
            int speed = rightLane.get(i).getSpeed();
            rightLane.get(i).moveSafe(speed);  // follow behavior only
            removeVehicle(rightLane.get(i));
        }
    }

    public void yellowFlow() {
        if (rightLane.isEmpty()) return;

        Vehicle first = rightLane.getFirst();
        first.slow();  // obey traffic light
        removeVehicle(first);

        updateAllDangerousDistances();

        for (int i = 1; i < rightLane.size(); i++) {
            int speed = rightLane.get(i).getSpeed();
            rightLane.get(i).moveSafe(speed);  // follow behavior only
            removeVehicle(rightLane.get(i));
        }
    }

    public void redFlow() {
        if (rightLane.isEmpty()) return;

        //for first vehicle
        Vehicle firstVehicle = rightLane.getFirst();
        int fx = firstVehicle.getX();
        int fy = firstVehicle.getY();
        int ind = size.ordinal();
        int length = 80;
        if (firstVehicle.getBrand() == CarBrand.FireTruck) {
            length = 100;
        }
        switch (ind) {
            case 0 -> firstVehicle.changeDangerousDistance(size.getYDown() - fy - length);
            case 1 -> firstVehicle.changeDangerousDistance(fx - size.getXLeft() - length);
            case 2 -> firstVehicle.changeDangerousDistance(fy- size.getYUp() - length);
            default -> firstVehicle.changeDangerousDistance(size.getXRight() - fx - length);
        }
        int firstSpeed = firstVehicle.getSpeed();
        firstVehicle.moveSafe(firstSpeed);

        updateAllDangerousDistances();

        for (int i = 1; i < rightLane.size(); i++) {
            int speed = rightLane.get(i).getSpeed();
            rightLane.get(i).moveSafe(speed);  // follow behavior only
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
