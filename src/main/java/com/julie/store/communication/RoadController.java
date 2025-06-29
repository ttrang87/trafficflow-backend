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

    @PostMapping("/monitor")
    public void setMonitor(@RequestBody Map<String, String> request) {
        String newStatus = request.get("status");
        if (newStatus.equals("Pause")) {
            roadService.pauseAllLanes();
        } else if (newStatus.equals("Resume")) {
            roadService.resumeAllLanes();
        } else {
            roadService.reset();
        }
    }

    @GetMapping("/set-light-duration")
    public Map<String, Integer> getLightDuration() { return roadService.getLightDuration(); }

    @PostMapping("/set-light-duration")
    public void setLightDuration(@RequestBody Map<String, Integer> request) {
        int newGreen = request.get("green");
        int newYellow = request.get("yellow");
        roadService.modifyTrafficLightDuration(newGreen, newYellow);
    }
}