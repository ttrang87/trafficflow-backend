//package com.julie.store.lane;
//
//import com.julie.store.road.CenterArea;
//import com.julie.store.road.RoadSize;
//import com.julie.store.vehicle.Vehicle;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ConcurrentLinkedDeque;
//
//public class EmergencyLane extends BaseLane {
//    private final ConcurrentLinkedDeque<Vehicle> lane = new ConcurrentLinkedDeque<>();
//    private final CenterArea centerArea;
//    public EmergencyLane(RoadSize size, CenterArea centerArea) {
//        super(size);
//        this.centerArea = centerArea;
//
//    }
//
//    public List<Vehicle> getLane() {
//        // CHANGE 2: Convert queue to list for compatibility
//        return new ArrayList<>(this.lane);
//    }
//
//    public void addVehicle(Vehicle vehicle) {
//        this.lane.offer(vehicle); // or lane.add(vehicle) - both work the same
//    }
//
//    public void updateAllDangerousDistances() {
//        // Convert to array for indexed access
//        Vehicle[] vehicles = lane.toArray(new Vehicle[0]);
//
//        for (int i = 1; i < vehicles.length; i++) {
//            Vehicle current = vehicles[i];
//            Vehicle front = vehicles[i - 1];
//            current.changeDangerousDistance(calculateSeparationDistance(current, front));
//        }
//    }
//
//    public int calculateCenterDistance(Vehicle firstVehicle) {
//        int x = firstVehicle.getX();
//        int y = firstVehicle.getY();
//        int length = firstVehicle.getBrand().getLength();
//
//        int minCenterDistance = Integer.MAX_VALUE;
//        for (Vehicle v : centerArea.getCenterArea()) {
//            int carIndex = v.getBrand().ordinal();
//            if (carIndex == 6 || carIndex == 7 || carIndex == 8) {
//
//            }
//        }
//
//        return minCenterDistance;
//    }
//
//    public void yellowFlow() {
//        if (lane.isEmpty()) return;
//
//        Vehicle first = lane.peekFirst();
//        if (first == null) return;
//
//        first.changeDangerousDistance(calculateCenterDistance(first));
//        first.changeSpeed(first.getInitialSpeed() - 1);
//        first.moveSafe();
//        removeVehicle(first);
//
//        updateAllDangerousDistances();
//
//        Vehicle[] vehicles = lane.toArray(new Vehicle[0]);
//        for (int i = 1; i < vehicles.length; i++) {
//            vehicles[i].moveSafe();
//            removeVehicle(vehicles[i]);
//        }
//    }
//
//
//
//}
