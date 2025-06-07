//package com.julie.store.lane;
//
//import com.julie.store.TrafficLight;
//import com.julie.store.road.CenterArea;
//import com.julie.store.road.RoadSize;
//import com.julie.store.vehicle.CarBrand;
//import com.julie.store.vehicle.Vehicle;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class EmergencyLane {
//    private final RoadSize size;
//    private final List<Vehicle> lane = new ArrayList<>();
//    private final CenterArea centerArea;
//
//    public EmergencyLane(RoadSize size, TrafficLight trafficLight, CenterArea centerArea) {
//        this.size = size;
//        this.centerArea = centerArea;
//    }
//
//    public List<Vehicle> getLane() {
//        return this.lane;  // returns empty list if no vehicles
//    }
//
//    public void addVehicle(Vehicle vehicle) {
//        this.lane.add(vehicle);
//    }
//
//    public int calculateCenterDistance(Vehicle firstVehicle) {
//        int x = firstVehicle.getX();
//        int y = firstVehicle.getY();
//        int length = firstVehicle.getBrand().getLength();
//
//        // 2. Distance to vehicles in center area
//        int minCenterDistance = Integer.MAX_VALUE;
//        for (Vehicle v : centerArea.getCenterArea()) {
//            int relation = firstVehicle.getDirection() - v.getDirection();
//            if ((v.getRelationship().equals(firstVehicle.getRelationship()) && relation == 0)  || relation == 1) {
//                int dx = Math.abs(v.getX() - x);
//                int dy = Math.abs(v.getY() - y);
//                int rawDistance = (int) Math.sqrt(dx * dx + dy * dy);
//                int addition;
//                if (relation == 1) {
//                    addition = length / 2 + v.getBrand().getWidth() / 2;
//                } else {
//                    addition = length / 2 + v.getBrand().getLength() / 2;
//                }
//                minCenterDistance = Math.min(minCenterDistance, rawDistance - addition - 4);
//            }
//        }
//
//        // Return the more urgent one (smallest distance)
//        return minCenterDistance;
//    }
//
//    public void removeVehicle(Vehicle vehicle) {
//        int x = vehicle.getX();
//        int y = vehicle.getY();
//        if (x > this.size.getXRight()
//                || x < this.size.getXLeft()
//                || y < this.size.getYUp()
//                || y > this.size.getYDown()) {
//            this.lane.removeFirst();
//            this.centerArea.addVehicle(vehicle);
//            vehicle.changeOutPosition();
//        }
//    }
//
//    public void updateAllDangerousDistances() {
//        for (int i = 1; i < lane.size(); i++) {
//            Vehicle current = lane.get(i);
//            Vehicle front = lane.get(i - 1);
//
//            int dx = Math.abs(current.getX() - front.getX());
//            int dy = Math.abs(current.getY() - front.getY());
//            int distance = (size.ordinal() == 0 || size.ordinal() == 2) ? dy : dx;
//            CarBrand curBrand = current.getBrand();
//            CarBrand frontBrand = front.getBrand();
//
//            int addition = curBrand.getLength() / 2 + frontBrand.getLength() / 2;
//            current.changeDangerousDistance(distance - addition);
//        }
//
//    }
//
//    public void greenFlow() {
//        if (lane.isEmpty()) return;
//
//        Vehicle first = lane.getFirst();
//        first.changeDangerousDistance(calculateCenterDistance(first));
//        first.moveSafe(first.getInitialSpeed());  // obey traffic light
//        removeVehicle(first);
//
//        updateAllDangerousDistances();
//
//        for (int i = 1; i < lane.size(); i++) {
//            int speed = lane.get(i).getSpeed();
//            lane.get(i).moveSafe(speed);  // follow behavior only
//            removeVehicle(lane.get(i));
//        }
//    }
//
//    public void yellowFlow() {
//        if (lane.isEmpty()) return;
//
//        Vehicle first = lane.getFirst();
//        first.changeDangerousDistance(calculateCenterDistance(first));
//        first.moveSafe(first.getInitialSpeed());  // obey traffic light
//        removeVehicle(first);
//
//        updateAllDangerousDistances();
//
//        for (int i = 1; i < lane.size(); i++) {
//            int speed = lane.get(i).getSpeed();
//            lane.get(i).moveSafe(speed);  // follow behavior only
//            removeVehicle(lane.get(i));
//        }
//    }
//
//    public void redFlow() {
//        if (lane.isEmpty()) return;
//
//        Vehicle first = lane.getFirst();
//        first.changeDangerousDistance(Math.min(calculateBoundary(first), calculateCenterDistance(first)));
//        first.moveSafe(first.getInitialSpeed());  // obey traffic light
//
//        updateAllDangerousDistances();
//
//        for (int i = 1; i < lane.size(); i++) {
//            int speed = lane.get(i).getSpeed();
//            lane.get(i).moveSafe(speed);  // follow behavior only
//            removeVehicle(lane.get(i));
//        }
//    }
//
//    public void operate() {
//        while (true) {
//            try {
//                switch (this.trafficLight.getColor()) {
//                    case "GREEN" -> this.greenFlow();
//                    case "YELLOW" -> this.yellowFlow();
//                    default -> this.redFlow();
//                }
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }
//    }
//}
