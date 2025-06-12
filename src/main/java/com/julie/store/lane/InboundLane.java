package com.julie.store.lane;

import com.julie.store.road.CenterArea;
import com.julie.store.road.Road;
import com.julie.store.road.RoadSize;
import com.julie.store.vehicle.CarBrand;
import com.julie.store.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class InboundLane extends BaseLane{
    // Use Collections.synchronizedList with ArrayList for optimal indexed access
    private final List<Vehicle> lane = Collections.synchronizedList(new ArrayList<Vehicle>());
    private InboundLane targetLane;
    private String changeLane;
    private Road road;
    //prevent same lane switch
    private volatile boolean isSwitchInProgress = false;

    public InboundLane(RoadSize size, CenterArea centerArea) {
        super(size, centerArea);
    }

    public void setNeighbour(InboundLane lane, String changeLane) {
        this.targetLane = lane;
        this.changeLane = changeLane;
    }

    public void setRoad(Road road) {
        this.road = road;
    }

    public List<Vehicle> getLane() {
        synchronized (lane) {
            return new ArrayList<>(this.lane);
        }
    }

    public void clear() {lane.clear();}

    public Vehicle getLastVehicle() {
        synchronized (lane) {
            if (lane.isEmpty()) return null;
            return lane.getLast();
        }
    }


    public void addLane(Vehicle vehicle) {
        synchronized (lane) {
            lane.add(vehicle);
        }
    }


    public int binarySearch(int target) {
        List<Vehicle> targetList = targetLane.getLane();
        if (targetList.isEmpty()) {
            return -1;  // No vehicles in target lane, can't find position
        }

        int direction = size.ordinal();
        int l = 0;
        int r = targetList.size() - 1;
        int value = 0;

        while (l < r) {
            int mid = (l + r) / 2;
            Vehicle vehicle = targetList.get(mid);
            value = (direction == 1 || direction == 3)
                    ? vehicle.getX()
                    : vehicle.getY();

            if (direction == 1 || direction == 2) {
                if (value > target) {
                    l = mid + 1;
                } else {
                    r = mid;
                }
            } else {
                if (value < target) {
                    l = mid + 1;
                } else {
                    r = mid;
                }
            }
        }

        if (l == 0) {
            Vehicle compare = targetList.get(l);
            int secValue = (direction == 1 || direction == 3)
                    ? compare.getX()
                    : compare.getY();
            if ((secValue < target && (direction == 1 || direction == 2)) ||
                    (secValue > target && (direction == 0 || direction == 3))) {
                return -1;
            }
        }
        return l;
    }

    public void attemptChange() {
        try {
            List<Vehicle> laneSnapshot = new ArrayList<>(lane);

            for (int i = laneSnapshot.size() - 1; i > 0; i--) {
                if (isSwitchInProgress) {
                    return;
                }

                Vehicle current = laneSnapshot.get(i);


                int direction = size.ordinal();
                int target = (direction == 1 || direction == 3) ? current.getX() : current.getY();

                int largeIndex = binarySearch(target);
                int newDangerous = Integer.MAX_VALUE;
                int space = Integer.MAX_VALUE;
                Vehicle prev = null;

                List<Vehicle> targetList = targetLane.getLane();

                if (largeIndex != -1) {
                    Vehicle newFront = targetList.get(largeIndex);

                    CarBrand curBrand = current.getBrand();
                    CarBrand frontBrand = newFront.getBrand();

                    int addition = curBrand.getLength() / 2 + frontBrand.getLength() / 2 + 18;
                    newDangerous = (direction == 1 || direction == 3)
                            ? Math.abs(current.getX() - newFront.getX()) - addition
                            : Math.abs(current.getY() - newFront.getY()) - addition;
                }

                if (largeIndex < targetList.size() - 1) {
                    prev = targetList.get(largeIndex + 1);
                    int addition = current.getBrand().getLength() / 2 + prev.getBrand().getLength() / 2 + prev.getSpeed() * 28 / current.getInitialSpeed();
                    space = (direction == 1 || direction == 3)
                            ? Math.abs(current.getX() - prev.getX()) - addition
                            : Math.abs(current.getY() - prev.getY()) - addition;
                }


                if (newDangerous - current.getDangerousDistance() >= 40 &&
                        space > 20) {

                    if (!road.tryStartSwitching()) {
                        return; // Another lane on this road is already switching
                    }

                    if (prev != null) {
                        prev.changeDangerousDistance(space - newDangerous);
                    }

                    isSwitchInProgress = true;
                    current.switchLane(changeLane, this, largeIndex);
                    return;
                }
            }
        } catch (Exception e) {
            road.finishSwitching();// Ensure road-level permission is released on error
            throw e;
        }
    }

    // Updated completeLaneSwitch
    public synchronized void completeLaneSwitch(Vehicle vehicle, int insertIndex) {
        synchronized (this.lane) {
            this.lane.remove(vehicle);
        }

        synchronized (targetLane.lane) {
            if (insertIndex == -1 || insertIndex >= targetLane.lane.size()) {
                targetLane.lane.add(vehicle);
            } else {
                targetLane.lane.add(insertIndex + 1, vehicle);
            }
        }

        isSwitchInProgress = false;  // Simple assignment (same thread)
        road.finishSwitching();

    }


    public synchronized void updateAllDangerousDistances() {
        if (lane.isEmpty()) return;

        // Work with synchronized access
        synchronized (lane) {
            if (lane.isEmpty()) return;

            lane.getFirst().changeDangerousDistance(Integer.MAX_VALUE);

            for (int i = 1; i < lane.size(); i++) {
                Vehicle current = lane.get(i);
                Vehicle front = lane.get(i - 1);
                current.changeDangerousDistance(calculateSeparationDistance(current, front));
            }
        }
    }

    public void flow() {
        // Create a copy of the list to iterate safely
        List<Vehicle> vehiclesToProcess;
        List<Vehicle> vehiclesToRemove = new ArrayList<>();

        synchronized (lane) {
            if (lane.isEmpty()) return;
            vehiclesToProcess = new ArrayList<>(lane);
        }

        // Process vehicles without holding the lock
        for (Vehicle vehicle : vehiclesToProcess) {
            vehicle.moveSafe();

            int x = vehicle.getX();
            int y = vehicle.getY();

            // Check if vehicle should be removed (out of bounds)
            if (x < RoadSize.WestSize.getXLeft() ||
                    x > RoadSize.EastSize.getXRight() ||
                    y < RoadSize.NorthSize.getYUp() ||
                    y > RoadSize.SouthSize.getYDown()) {

                vehiclesToRemove.add(vehicle);
            }
        }

        // Remove vehicles that went out of bounds
        if (!vehiclesToRemove.isEmpty()) {
            synchronized (lane) {
                lane.removeAll(vehiclesToRemove);
            }
        }
    }

    public void operate() {
        while (BaseLane.isRunning()) {
            try {
                while (BaseLane.isPaused() && BaseLane.isRunning()) {
                    synchronized (pauseLock) {
                        pauseLock.wait();
                    }
                }

                if (!BaseLane.isRunning()) {
                    break;
                }

                updateAllDangerousDistances();
                flow();
                attemptChange();
                Thread.sleep(100);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }  catch (Exception e) {
                // Add general exception handling to prevent thread death
                System.err.println("Error in Lane.operate(): " + e.getMessage());
                e.printStackTrace();
                // Continue running instead of crashing
            }
        }
    }
}