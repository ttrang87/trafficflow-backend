package com.julie.store.road;

import com.julie.store.TrafficLight;
import com.julie.store.lane.EmergencyLane;
import com.julie.store.lane.InboundLane;
import com.julie.store.lane.Lane;
import com.julie.store.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Road {
    private final RoadSize size;
    private final TrafficLight trafficLight;
    private final Lane rightMost;
    private final Lane rightMiddle;
    private final InboundLane lane1;
    private final InboundLane lane2;
    private final EmergencyLane emergencyLaneOut;
    private final EmergencyLane emergencyLaneIn;
    private final AtomicBoolean isSwitching = new AtomicBoolean(false);

    // Remove the ReentrantLock - no longer needed
    // private final ReentrantLock laneSwitchLock = new ReentrantLock();

    public boolean tryStartSwitching() {
        // Atomic compare-and-set: only succeeds if currently false
        return isSwitching.compareAndSet(false, true);
    }

    public void finishSwitching() {
        isSwitching.set(false);
    }

    public boolean isSwitching() {
        return isSwitching.get();
    }

    public Road(RoadSize size, TrafficLight trafficLight, CenterArea centerArea) {
        this.size = size;
        this.trafficLight = trafficLight;
        this.rightMost = new Lane(size, trafficLight, centerArea);
        this.rightMiddle = new Lane(size, trafficLight, centerArea);
        this.emergencyLaneOut = new EmergencyLane(size, centerArea);
        this.emergencyLaneIn = new EmergencyLane(size, centerArea);
        this.lane1 = new InboundLane(size, centerArea);
        this.lane2 = new InboundLane(size, centerArea);

        lane1.setNeighbour(lane2, "LEFT");
        lane1.setRoad(this);
        lane2.setNeighbour(lane1, "RIGHT");
        lane2.setRoad(this);
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

    public EmergencyLane getEmergencyLaneOut() { return this.emergencyLaneOut; }

    public EmergencyLane getEmergencyLaneIn() { return this.emergencyLaneIn; }

    public InboundLane getLane1() {
        return this.lane1;
    }

    public InboundLane getLane2() {
        return this.lane2;
    }

    public void clean() {
        rightMost.clear();
        rightMiddle.clear();
        lane1.clear();
        lane2.clear();
        emergencyLaneIn.clear();
        emergencyLaneOut.clear();
    }


    public List<Vehicle> getCombinedLaneVehicles() {
        List<Vehicle> combined = new ArrayList<>();
        combined.addAll(rightMost.getLane());
        combined.addAll(rightMiddle.getLane());
        combined.addAll(lane1.getLane());
        combined.addAll(lane2.getLane());
        combined.addAll(emergencyLaneOut.getLane());
        combined.addAll(emergencyLaneIn.getLane());
        return combined;
    }

    public double calculateLaneAverageSpeed() {
        double totalSpeed = 0.0;
        int count = 0;

        double s;

        if ((s = rightMost.calculateLaneAverageSpeed()) > 0) { totalSpeed += s; count++; }
        if ((s = rightMiddle.calculateLaneAverageSpeed()) > 0) { totalSpeed += s; count++; }
        if ((s = lane1.calculateLaneAverageSpeed()) > 0) { totalSpeed += s; count++; }
        if ((s = lane2.calculateLaneAverageSpeed()) > 0) { totalSpeed += s; count++; }
        if ((s = emergencyLaneIn.calculateLaneAverageSpeed()) > 0) { totalSpeed += s; count++; }
        if ((s = emergencyLaneOut.calculateLaneAverageSpeed()) > 0) { totalSpeed += s; count++; }

        if (count == 0) return 0.0;
        return totalSpeed / count;
    }

    public double calculateAverageWaitTime() {
        double totalWait = 0.0;
        int count = 0;

        double s;

        if ((s = rightMost.calculateAverageWaitTime()) > 0) { totalWait += s; count++; }
        if ((s = rightMiddle.calculateAverageWaitTime()) > 0) { totalWait += s; count++; }
        if ((s = emergencyLaneOut.calculateAverageWaitTime()) > 0) { totalWait += s; count++; }

        if (count == 0) return 0.0;
        return totalWait / count;
    }


}
