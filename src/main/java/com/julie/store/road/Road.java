package com.julie.store.road;

import com.julie.store.TrafficLight;
import com.julie.store.lane.InboundLane;
import com.julie.store.lane.Lane;
import com.julie.store.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class Road {
    private final RoadSize size;
    private final TrafficLight trafficLight;
    private final Lane rightMost;
    private final Lane rightMiddle;
    private final InboundLane lane1;
    private final InboundLane lane2;

    public Road(RoadSize size, TrafficLight trafficLight, CenterArea centerArea) {
        this.size = size;
        this.trafficLight = trafficLight;
        this.rightMost = new Lane(size, trafficLight, centerArea);
        this.rightMiddle = new Lane(size, trafficLight, centerArea);
        this.lane1 = new InboundLane(size);
        this.lane2 = new InboundLane(size);

        lane1.setNeighbour(lane2, "LEFT");
        lane2.setNeighbour(lane1, "RIGHT");
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

    public InboundLane getLane1() {
        return this.lane1;
    }

    public InboundLane getLane2() {
        return this.lane2;
    }


    public List<Vehicle> getCombinedLaneVehicles() {
        List<Vehicle> combined = new ArrayList<>();
        combined.addAll(rightMost.getLane());
        combined.addAll(rightMiddle.getLane());
        combined.addAll(lane1.getLane());
        combined.addAll(lane2.getLane());
        return combined;
    }

}
