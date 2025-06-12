package com.julie.store.road;

import com.julie.store.SimulationLauncher;
import com.julie.store.TrafficLight;
import com.julie.store.lane.BaseLane;
import com.julie.store.vehicle.CarBrand;
import com.julie.store.vehicle.Vehicle;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


//handle logic and object creation
@Service
public class RoadService {
    private final Road north;
    private final Road east;
    private final Road south;
    private final Road west;
    private final SimulationLauncher simulationLauncher;
    private final Random random = new Random();
    private final CenterArea centerArea;
    private final ScheduledExecutorService executorService;

    private String level = "Normal";
    private String density = "Normal";

    public RoadService(CenterArea centerArea, SimulationLauncher simulationLauncher) {
        this.centerArea = centerArea;
        this.north = new Road(RoadSize.NorthSize, new TrafficLight("GREEN", 7, 0), centerArea);
        this.east = new Road(RoadSize.EastSize, new TrafficLight("RED", 30, 20), centerArea);
        this.south = new Road(RoadSize.SouthSize, new TrafficLight("RED", 30, 10), centerArea);
        this.west = new Road(RoadSize.WestSize, new TrafficLight("RED", 30, 0), centerArea);
        this.simulationLauncher = simulationLauncher;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void randomAddVehicle() {
        //Random prob of adding with weighted
        ArrayList<Boolean> weightedProb = new ArrayList<Boolean>();
        for (int i = 0; i < getWeightedDensity(); i++) {
            weightedProb.add(false);
        }
        weightedProb.add(true);

        boolean add = weightedProb.get(random.nextInt(weightedProb.size()));
        if (!add) {
            return;
        }

        //Random Goal and Position
        List<Road> allRoads = List.of(north, east, south, west);
        Road sourceRoad = allRoads.get(random.nextInt(allRoads.size()));

        Road goalRoad;
        do {
            goalRoad = allRoads.get(random.nextInt(allRoads.size()));
        } while (goalRoad == sourceRoad);

        RoadSize size = sourceRoad.getRoadSize();
        int newX, newY;
        int indexRoad = size.ordinal();
        int relationship = (indexRoad - goalRoad.getRoadSize().ordinal() + 4) % 4;

        //Random Car type with weighted
        ArrayList<Integer> weightedNumbers = new ArrayList<Integer>();

        for (int value = 0; value < 6; value++) {
            for (int i = 0; i < 60; i++) {
                weightedNumbers.add(value);
            }
        }
        weightedNumbers.add(6);
        weightedNumbers.add(7);
        weightedNumbers.add(8);

        int indexCar = weightedNumbers.get(random.nextInt(weightedNumbers.size()));
        boolean isEmergency = indexCar == 6 || indexCar == 7 || indexCar == 8;

        //Put in correct lane
        if (indexRoad == 0) {
            if (isEmergency) {
                newX = size.getXLeft() + 50;
            } else if (relationship == 1) {
                newX = size.getXLeft() + 10;
            } else {
                newX = size.getXLeft() + 30;
            }
            newY = size.getYUp() + 10;
        } else if (indexRoad == 1) {
            if (isEmergency) {
                newY = size.getYUp() + 50;
            } else if (relationship == 1) {
                newY = size.getYUp() + 10;
            } else {
                newY = size.getYUp() + 30;
            }
            newX = size.getXRight() - 10;
        } else if (indexRoad == 2) {
            if (isEmergency) {
                newX = size.getXRight() - 50;
            } else if (relationship == 1) {
                newX = size.getXRight() - 10;
            } else {
                newX = size.getXRight() - 30;
            }
            newY = size.getYDown() - 10;
        } else {
            if (isEmergency) {
                newY = size.getYDown() - 50;
            } else if (relationship == 1) {
                newY = size.getYDown() - 10;
            } else {
                newY = size.getYDown() - 30;
            }
            newX = size.getXLeft() + 10;
        }

        boolean check;
        if (isEmergency) {
            check = verifyAdd(sourceRoad.getEmergencyLaneOut(), indexRoad, newX, newY);
        } else {
            if (relationship == 1) {
                check = verifyAdd(sourceRoad.getRightMost(), indexRoad, newX, newY);
            } else {
                check = verifyAdd(sourceRoad.getRightMiddle(), indexRoad, newX, newY);
            }
        }


        if (check) {
            Vehicle newVehicle = new Vehicle(newX, newY, sourceRoad, goalRoad, CarBrand.values()[indexCar]);
            modifyNewVehicleSpeed(newVehicle);  //modify speed right after creating to fit with system
            if (isEmergency) {
                sourceRoad.getEmergencyLaneOut().addVehicle(newVehicle);
                changeLightEmergency();
            } else {
                if (relationship == 1) {
                    sourceRoad.getRightMost().addVehicle(newVehicle);
                } else {
                    sourceRoad.getRightMiddle().addVehicle(newVehicle);
                }
            }
        }
    }

    public void changeLightEmergency() {
        north.getLight().emergency();
        east.getLight().emergency();
        south.getLight().emergency();
        west.getLight().emergency();
    }

    public void resumeLight() {
        if (north.getLight().getEmergency()) {
            north.getLight().resume();
        }
        if (east.getLight().getEmergency()) {
            east.getLight().resume();
        }
        if (south.getLight().getEmergency()) {
            south.getLight().resume();
        }
        if (west.getLight().getEmergency()) {
            west.getLight().resume();
        }
    }
    public boolean checkAllEmergencyVehicles() {
        boolean northEmpty = north.getEmergencyLaneOut().getLane().isEmpty() &&
                north.getEmergencyLaneIn().getLane().isEmpty();
        boolean eastEmpty = east.getEmergencyLaneOut().getLane().isEmpty() &&
                east.getEmergencyLaneIn().getLane().isEmpty();
        boolean southEmpty = south.getEmergencyLaneOut().getLane().isEmpty() &&
                south.getEmergencyLaneIn().getLane().isEmpty();
        boolean westEmpty = west.getEmergencyLaneOut().getLane().isEmpty() &&
                west.getEmergencyLaneIn().getLane().isEmpty();
        boolean centerEmpty = checkCenterArea(centerArea);

        return northEmpty && eastEmpty && southEmpty && westEmpty && centerEmpty;
    }

    public boolean checkCenterArea (CenterArea centerArea) {
        if (centerArea.getCenterArea().isEmpty()) { return true; }
        for (Vehicle vehicle : centerArea.getCenterArea()) {
            int indexCar = vehicle.getBrand().ordinal();
            if (indexCar == 6 || indexCar == 7 || indexCar == 8 ) {
                return false;
            }
        }

        return true;
    }



    public void resumeAfterEmergency() {
        if (checkAllEmergencyVehicles() ) {
            resumeLight();
        }
    }


    private static boolean verifyAdd(BaseLane lane, int indexRoad, int newX, int newY) {
        Vehicle lastVehicle = lane.getLastVehicle();
        if (lastVehicle != null) {
            int lastX = lastVehicle.getX();
            int lastY = lastVehicle.getY();
            int length = 90;
            return (indexRoad != 0 || lastY - newY >= length) &&
                    (indexRoad != 1 || newX - lastX >= length) &&
                    (indexRoad != 2 || newY - lastY >= length) &&
                    (indexRoad != 3 || lastX - newX >= length);
        }
        return true;
    }


    public Map<String, Map<String, Integer>> getRoadCoordinates() {
        return Map.of(
                "North", toMap(north.getRoadSize()),
                "East", toMap(east.getRoadSize()),
                "South", toMap(south.getRoadSize()),
                "West", toMap(west.getRoadSize())
        );
    }

    public Map<String, Map<String, Object>> getTrafficLightColors() {
        return Map.of(
                "North", Map.of(
                        "color", north.getLight().getColor(),
                        "count", north.getLight().getCount()
                ),
                "East", Map.of(
                        "color", east.getLight().getColor(),
                        "count", east.getLight().getCount()
                ),
                "South", Map.of(
                        "color", south.getLight().getColor(),
                        "count", south.getLight().getCount()
                ),
                "West", Map.of(
                        "color", west.getLight().getColor(),
                        "count", west.getLight().getCount()
                )
        );
    }

    private Map<String, Integer> toMap(RoadSize size) {
        return Map.of(
                "xLeft", size.getXLeft(),
                "xRight", size.getXRight(),
                "yUp", size.getYUp(),
                "yDown", size.getYDown()
        );
    }

    public Map<String, List<Vehicle>> getCar() {
        return Map.of(
                "North", north.getCombinedLaneVehicles(),
                "East", east.getCombinedLaneVehicles(),
                "South", south.getCombinedLaneVehicles(),
                "West", west.getCombinedLaneVehicles(),
                "CenterArea", centerArea.getCenterArea()
        );
    }

    public int getCountCar() {
        int count = 0;
        count += north.getCombinedLaneVehicles().size();
        count += east.getCombinedLaneVehicles().size();
        count += south.getCombinedLaneVehicles().size();
        count += west.getCombinedLaneVehicles().size();
        count += centerArea.getCenterArea().size();
        return count;
    }

    public double getAvgSpeed() {
        double totalSpeed = 0.0;

        totalSpeed += north.calculateLaneAverageSpeed();
        totalSpeed += east.calculateLaneAverageSpeed();
        totalSpeed += south.calculateLaneAverageSpeed();
        totalSpeed += west.calculateLaneAverageSpeed();
        totalSpeed += centerArea.calculateLaneAverageSpeed();

        double avgSpeed = totalSpeed / 5.0;

        // Force 2-digit rounding, strictly
        BigDecimal bd = new BigDecimal(avgSpeed).setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double calculateAverageWaitTime() {
        double totalWait = 0.0;
        int count = 0;

        double s;

        if ((s = north.calculateAverageWaitTime()) > 0) { totalWait += s; count++; }
        if ((s = east.calculateAverageWaitTime()) > 0) { totalWait += s; count++; }
        if ((s = south.calculateAverageWaitTime()) > 0) { totalWait += s; count++; }
        if ((s = west.calculateAverageWaitTime()) > 0) { totalWait += s; count++; }

        if (count == 0) return 0.0;


        double avgWaitTime = totalWait / count / 10;
        BigDecimal bd = new BigDecimal(avgWaitTime).setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //This part contains method for update speed

    public void setLevel(String newLevel) {
        if (!newLevel.equals(this.level)) {
            updateAllVehicleSpeeds(newLevel);
            this.level = newLevel;
        }
    }

    public void modifySpeed(Vehicle vehicle, String newLevel) {
        int curInitialSpeed = vehicle.getInitialSpeed();
        if (newLevel.equals("Slow")) {
            if (this.level.equals("Fast")) {
                vehicle.setInitialSpeed(curInitialSpeed - 4);
                System.out.println("Its new ini speed is: " + vehicle.getInitialSpeed());
            } else {
                vehicle.setInitialSpeed(curInitialSpeed - 2);
            }
        } else if (newLevel.equals("Fast")) {
            if (this.level.equals("Slow")) {
                vehicle.setInitialSpeed(curInitialSpeed + 4);
            } else {
                vehicle.setInitialSpeed(curInitialSpeed + 2);
            }
        } else {
            if (this.level.equals("Fast")) {
                vehicle.setInitialSpeed(curInitialSpeed - 2);
            } else {
                vehicle.setInitialSpeed(curInitialSpeed + 2);
            }
        }

        if (vehicle.getSpeed() == curInitialSpeed) {vehicle.setSpeed(vehicle.getInitialSpeed()); }
    }

    public void modifyNewVehicleSpeed(Vehicle vehicle) {
        int curInitialSpeed = vehicle.getInitialSpeed();
        if (this.level.equals("Fast")) {
            vehicle.setInitialSpeed(curInitialSpeed + 2);
        } else if (this.level.equals("Slow")) {
            vehicle.setInitialSpeed(curInitialSpeed - 2);
        }
    }

    public List<Vehicle> getCarsForChangeSpeed() {
        List<Vehicle> allVehicles = new ArrayList<>();
        allVehicles.addAll(north.getCombinedLaneVehicles());
        allVehicles.addAll(east.getCombinedLaneVehicles());
        allVehicles.addAll(south.getCombinedLaneVehicles());
        allVehicles.addAll(west.getCombinedLaneVehicles());
        allVehicles.addAll(centerArea.getCenterArea());

        return allVehicles;
    }

    public void updateAllVehicleSpeeds(String newLevel) {
        for (Vehicle vehicle : getCarsForChangeSpeed()) {
            modifySpeed(vehicle, newLevel);
        }
    }

    //This part contains code for updating density
    public void setDensity(String newDensity) {
        this.density = newDensity;
    }

    public int getWeightedDensity() {
        return switch (this.density) {
            case "Normal" -> 4;
            case "Sparse" -> 12;
            default -> 1;
        };
    }

    public void startSimulation() {

        executorService.scheduleAtFixedRate(this::randomAddVehicle, 0, 100, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(this::resumeAfterEmergency, 0, 300, TimeUnit.MILLISECONDS);
        simulationLauncher.startCountdown(north.getLight());
        simulationLauncher.startCountdown(east.getLight());
        simulationLauncher.startCountdown(south.getLight());
        simulationLauncher.startCountdown(west.getLight());
        simulationLauncher.startCenter(centerArea);

        // Start each lane of each road separately
        simulationLauncher.startLane(north.getRightMost());
        simulationLauncher.startLane(north.getRightMiddle());

        simulationLauncher.startLane(east.getRightMost());
        simulationLauncher.startLane(east.getRightMiddle());

        simulationLauncher.startLane(south.getRightMost());
        simulationLauncher.startLane(south.getRightMiddle());

        simulationLauncher.startLane(west.getRightMost());
        simulationLauncher.startLane(west.getRightMiddle());

        simulationLauncher.startEmergencyLane(north.getEmergencyLaneOut());
        simulationLauncher.startEmergencyLane(east.getEmergencyLaneOut());
        simulationLauncher.startEmergencyLane(south.getEmergencyLaneOut());
        simulationLauncher.startEmergencyLane(west.getEmergencyLaneOut());

        simulationLauncher.startEmergencyLane(north.getEmergencyLaneIn());
        simulationLauncher.startEmergencyLane(east.getEmergencyLaneIn());
        simulationLauncher.startEmergencyLane(south.getEmergencyLaneIn());
        simulationLauncher.startEmergencyLane(west.getEmergencyLaneIn());

        // Start each inbound lane of each road separately
        simulationLauncher.startInboundLane(north.getLane1());
        simulationLauncher.startInboundLane(north.getLane2());

        simulationLauncher.startInboundLane(east.getLane1());
        simulationLauncher.startInboundLane(east.getLane2());

        simulationLauncher.startInboundLane(south.getLane1());
        simulationLauncher.startInboundLane(south.getLane2());

        simulationLauncher.startInboundLane(west.getLane1());
        simulationLauncher.startInboundLane(west.getLane2());

//        executorService.scheduleAtFixedRate(() -> {
//            centerArea.printAllVehiclesInfo();
//            north.getLane1().printAllVehiclesInfo();
//            north.getLane2().printAllVehiclesInfo();
//            east.getLane1().printAllVehiclesInfo();
//            east.getLane2().printAllVehiclesInfo();
//            south.getLane1().printAllVehiclesInfo();
//            south.getLane2().printAllVehiclesInfo();
//            west.getLane1().printAllVehiclesInfo();
//            west.getLane2().printAllVehiclesInfo();
//            System.out.println("--------------------------------------");
//        }, 0, 100, TimeUnit.MILLISECONDS);

    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}