package com.julie.store.road;

import com.julie.store.SimulationLauncher;
import com.julie.store.TrafficLight;
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

        if (indexRoad == 0) {
            newX = size.getXLeft() + 30;
            newY = size.getYUp() + 30;
        } else if (indexRoad == 1) {
            newX = size.getXRight() - 30;
            newY = size.getYUp() + 30;
        } else if (indexRoad == 2) {
            newX = size.getXRight() - 30;
            newY = size.getYDown() - 30;
        } else {
            newX = size.getXLeft() + 30;
            newY = size.getYDown() - 30;
        }

        if (!sourceRoad.getRightLane().isEmpty()) {
            Vehicle lastVehicle = sourceRoad.getRightLane().getLast();
            int lastX = lastVehicle.getX();
            int lastY = lastVehicle.getY();
            int length = 80;
            if (lastVehicle.getBrand() == CarBrand.FireTruck || lastVehicle.getBrand() == CarBrand.MiniVan || lastVehicle.getBrand() == CarBrand.Police) {
                length = 100;
            }
            if (
                    (indexRoad == 0 && lastY - newY < length) ||
                            (indexRoad == 1 && newX - lastX < length) ||
                            (indexRoad == 2 && newY - lastY < length) ||
                            (indexRoad == 3 && lastX - newX < length)
            ) {
                return;
            }

        }

        int indexCar = random.nextInt(0, 7);
        Vehicle newVehicle = new Vehicle(newX, newY, sourceRoad, goalRoad, CarBrand.values()[indexCar]);

        sourceRoad.addVehicle(newVehicle);
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
                "North", north.getRightLane(),
                "East", east.getRightLane(),
                "South", south.getRightLane(),
                "West", west.getRightLane(),
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
        simulationLauncher.startOperate(west);
        simulationLauncher.startOperate(north);
        simulationLauncher.startOperate(east);
        simulationLauncher.startOperate(south);
        simulationLauncher.startCenter(centerArea);

        executorService.scheduleAtFixedRate(() -> {
            System.out.println("---- Vehicles on each road ----");
            System.out.println("North: " + north.getRightLaneCoordinates());
            System.out.println("East: " + east.getRightLaneCoordinates());
            System.out.println("South: " + south.getRightLaneCoordinates());
            System.out.println("West: " + west.getRightLaneCoordinates());
            System.out.println("--------------------------------------");
        }, 0, 1, TimeUnit.SECONDS);

        // Optional: Block the main thread for non-web environments
        try {
            Thread.sleep(Long.MAX_VALUE); // Keeps the application alive
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
            System.out.println("Simulation interrupted");
        }
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