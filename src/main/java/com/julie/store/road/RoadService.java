package com.julie.store.road;

import com.julie.store.SimulationLauncher;
import com.julie.store.TrafficLight;
import com.julie.store.lane.Lane;
import com.julie.store.vehicle.CarBrand;
import com.julie.store.vehicle.Vehicle;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
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
        boolean add = random.nextBoolean();
        if (!add) {
            return;
        }

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
        int[] weightedNumbers = {
                0, 0, 0,
                1, 1, 1,
                2, 2, 2,
                3, 3, 3,
                4, 4, 4,
                5, 5, 5,
//                6,  // fewer times
//                7,
//                8
        };
        int indexCar = weightedNumbers[random.nextInt(weightedNumbers.length)];

        if (indexRoad == 0) {
//            if (indexCar == 6 || indexCar == 7 || indexCar == 8) {
//                newX = size.getXLeft() + 75;
//            } else
            if (relationship == 1) {
                newX = size.getXLeft() + 15;
            } else {
                newX = size.getXLeft() + 45;
            }
            newY = size.getYUp() + 35;
        } else if (indexRoad == 1) {
//            if (indexCar == 6 || indexCar == 7 || indexCar == 8) {
//                newY = size.getYUp() + 75;
//            } else
                if (relationship == 1) {
                newY = size.getYUp() + 15;
            } else {
                newY = size.getYUp() + 45;
            }
            newX = size.getXRight() - 35;
        } else if (indexRoad == 2) {
//            if (indexCar == 6 || indexCar == 7 || indexCar == 8) {
//                newX = size.getXRight() - 75;
//            } else
                if (relationship == 1) {
                newX = size.getXRight() - 15;
            } else {
                newX = size.getXRight() - 45;
            }
            newY = size.getYDown() - 35;
        } else {
//            if (indexCar == 6 || indexCar == 7 || indexCar == 8) {
//                newY = size.getYDown() - 75;
//            } else
                if (relationship == 1) {
                newY = size.getYDown() - 15;
            } else {
                newY = size.getYDown() - 45;
            }
            newX = size.getXLeft() + 35;
        }

        boolean check;
        if (relationship == 1) {
            check = verifyAdd(sourceRoad.getRightMost(), indexRoad, newX, newY);
        } else {
            check = verifyAdd(sourceRoad.getRightMiddle(), indexRoad, newX, newY);
        }

        if (check) {
            Vehicle newVehicle = new Vehicle(newX, newY, sourceRoad, goalRoad, CarBrand.values()[indexCar]);

            if (relationship == 1) {
                sourceRoad.getRightMost().addVehicle(newVehicle);
            } else {
                sourceRoad.getRightMiddle().addVehicle(newVehicle);
            }
        }
    }

    private static boolean verifyAdd(Lane lane, int indexRoad, int newX, int newY) {
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

    public Map<String, String> getTrafficLightColors() {
        return Map.of(
                "North", north.getLight().getColor(),
                "East", east.getLight().getColor(),
                "South", south.getLight().getColor(),
                "West", west.getLight().getColor()
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

    public void startSimulation() {
        System.out.println("Starting simulation");

        simulationLauncher.startAddingVehicles(this);
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

        // Start each inbound lane of each road separately
        simulationLauncher.startInboundLane(north.getLane1());
        simulationLauncher.startInboundLane(north.getLane2());

        simulationLauncher.startInboundLane(east.getLane1());
        simulationLauncher.startInboundLane(east.getLane2());

        simulationLauncher.startInboundLane(south.getLane1());
        simulationLauncher.startInboundLane(south.getLane2());

        simulationLauncher.startInboundLane(west.getLane1());
        simulationLauncher.startInboundLane(west.getLane2());

        executorService.scheduleAtFixedRate(() -> {
            centerArea.printAllVehiclesInfo();
            north.getLane1().printAllVehiclesInfo();
            north.getLane2().printAllVehiclesInfo();
            east.getLane1().printAllVehiclesInfo();
            east.getLane2().printAllVehiclesInfo();
            south.getLane1().printAllVehiclesInfo();
            south.getLane2().printAllVehiclesInfo();
            west.getLane1().printAllVehiclesInfo();
            west.getLane2().printAllVehiclesInfo();
            System.out.println("--------------------------------------");
        }, 0, 100, TimeUnit.MILLISECONDS);

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