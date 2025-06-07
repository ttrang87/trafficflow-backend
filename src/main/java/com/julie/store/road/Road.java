package com.julie.store.road;

import com.julie.store.TrafficLight;
import com.julie.store.lane.Lane;
import com.julie.store.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class Road {
    private final RoadSize size;
    private final TrafficLight trafficLight;
    private final Lane rightMost;
    private final Lane rightMiddle;

    public Road(RoadSize size, TrafficLight trafficLight, CenterArea centerArea) {
        this.size = size;
        this.trafficLight = trafficLight;
        this.rightMost = new Lane(size, trafficLight, centerArea);
        this.rightMiddle = new Lane(size, trafficLight, centerArea);
    }

    public RoadSize getRoadSize() {
        return this.size;
    }

    public TrafficLight getLight() {
        return this.trafficLight;
    }

    public Lane getRightMost() {
        return this.rightMost;
    }

    public Lane getRightMiddle() {
        return this.rightMiddle;
    }

    public List<Vehicle> getCombinedLaneVehicles() {
        List<Vehicle> combined = new ArrayList<>();
        combined.addAll(rightMost.getLane());
        combined.addAll(rightMiddle.getLane());
        return combined;
    }

}
