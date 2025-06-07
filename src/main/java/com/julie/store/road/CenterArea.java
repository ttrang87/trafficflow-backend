package com.julie.store.road;

import com.julie.store.vehicle.CarBrand;
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

    public void updateAllDangerousDistances() {
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
                int addition = distance - 100;
                if (relationship != 2) {
                    addition = front.getBrand().getLength() / 2 + current.getBrand().getLength() /2;
                }
                int gap = distance - addition - 2;

                minDangerous = Math.min(minDangerous, gap);
            }
            current.changeDangerousDistance(minDangerous); // free to move

        }
    }


    private static int getAddition(Vehicle check, Vehicle current, int relationship) {
        CarBrand curBrand = current.getBrand();
        CarBrand checkBrand = check.getBrand();

        int addition = 0;

        //parallel
        if(relationship == 0) {
            addition = curBrand.getLength() / 2 + checkBrand.getLength() /2;
        } else if (relationship == 2) {
            addition = Integer.MIN_VALUE;
        }
        else {
            addition = curBrand.getLength() / 2 + checkBrand.getWidth() / 2;
        }
        return addition;
    }

    public String getCenterCoordinates() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < centerArea.size(); i++) {
            Vehicle v = centerArea.get(i);
            sb.append("[").append(v.getBrand()).append(", ").append(v.getX()).append(", ").append(v.getY()).append(", ").append(v.getDangerousDistance()).append(", ").append(v.getDirection()).append("]");
            if (i < centerArea.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public void operate() {
        while(true) {
            try {
                updateAllDangerousDistances();
                for (Vehicle vehicle: centerArea) {
                    vehicle.moveOut();
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        };
    }
}
