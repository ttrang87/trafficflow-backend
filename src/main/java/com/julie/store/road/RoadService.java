package com.julie.store.road;

import com.julie.store.SimulationLauncher;
import com.julie.store.TrafficLight;
import com.julie.store.vehicle.CarBrand;
import com.julie.store.vehicle.Vehicle;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
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

    public RoadService(CenterArea centerArea, SimulationLauncher simulationLauncher) {
        this.north = new Road(RoadSize.NorthSize, new TrafficLight("GREEN", 7, 0), centerArea);
        this.east = new Road(RoadSize.EastSize, new TrafficLight("RED", 30, 20), centerArea);
        this.south = new Road(RoadSize.SouthSize, new TrafficLight("RED", 30, 10), centerArea);
        this.west = new Road(RoadSize.WestSize, new TrafficLight("RED", 30, 0), centerArea);
        this.simulationLauncher = simulationLauncher;
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
            newX = size.getXLeft() + 15;
            newY = size.getYUp();
        } else if (indexRoad == 1) {
            newX = size.getXRight();
            newY = size.getYUp() - 15;
        } else if (indexRoad == 2) {
            newX = size.getXRight() - 15;
            newY = size.getYDown();
        } else {
            newX = size.getXLeft();
            newY = size.getYDown() + 15;
        }

        int indexCar = random.nextInt(0,9);
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

    public void startSimulation() {
        System.out.println("Hi this is start simulation");

        simulationLauncher.startAddingVehicles(this);

        simulationLauncher.startCountdown(north.getLight());
        simulationLauncher.startCountdown(east.getLight());
        simulationLauncher.startCountdown(south.getLight());
        simulationLauncher.startCountdown(west.getLight());

        simulationLauncher.startOperate(north);
        simulationLauncher.startOperate(east);
        simulationLauncher.startOperate(south);
        simulationLauncher.startOperate(west);



        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            System.out.println("---- Current Traffic Light Colors ----");
            System.out.println("North: " + north.getLight().getColor() + " - Second: " + north.getLight().getCount());
            System.out.println("East: " + east.getLight().getColor() + " - Second: " + east.getLight().getCount());
            System.out.println("South: " + south.getLight().getColor() + " - Second: " + south.getLight().getCount());
            System.out.println("West: " + west.getLight().getColor() + " - Second: " + west.getLight().getCount());
            System.out.println("---- Vehicles on each road ----");
            System.out.println("North: " + north.getRightLane());
            System.out.println("East: " + east.getRightLane());
            System.out.println("South: " + south.getRightLane());
            System.out.println("West: " + west.getRightLane());

            System.out.println("--------------------------------------");
        }, 0, 1, TimeUnit.SECONDS);
    }

}

