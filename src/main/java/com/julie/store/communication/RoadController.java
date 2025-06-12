package com.julie.store.communication;

import com.julie.store.road.RoadService;
import com.julie.store.vehicle.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/set-speed-level")
    public void setSpeedLevel(@RequestBody Map<String, String> request) {
        String newLevel = request.get("level");
        roadService.setLevel(newLevel);
    }

    @PostMapping("/set-density-level")
    public void setDensityLevel(@RequestBody Map<String, String> request) {
        String newDensity = request.get("density");
        roadService.setDensity(newDensity);
    }
}