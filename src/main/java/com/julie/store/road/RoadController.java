package com.julie.store.road;

import com.julie.store.vehicle.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class RoadController {
    private final RoadService roadService;

    @Autowired
    public RoadController(RoadService roadService) {
        this.roadService = roadService;
    }

    @GetMapping("/get-road-coordinate")
    public Map<String, Map<String, Integer>> getRoadCoordinate() {
        return roadService.getRoadCoordinates();
    }

    @GetMapping("/get-traffic-light")
    public Map<String, String> getTrafficLightStates() {;
        return roadService.getTrafficLightColors();
    }

    @GetMapping("/get-car")
    public Map<String, List<Vehicle>> getCar() {
        return roadService.getCar();
    }
}
