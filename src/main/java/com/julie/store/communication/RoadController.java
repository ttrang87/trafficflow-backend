package com.julie.store.communication;

import com.julie.store.road.RoadService;
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
    }}
