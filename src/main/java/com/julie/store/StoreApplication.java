package com.julie.store;

import com.julie.store.road.RoadService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAsync
@RestController
public class StoreApplication {
	private final RoadService roadService;

    public StoreApplication(RoadService roadService) {
        this.roadService = roadService;
    }

    public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
	}

	@RequestMapping("/hi")
	public String sayHello() {
		return "index.html";
	}

	@GetMapping("/start-simulation")
	public String startSimulation() {
		roadService.startSimulation();
		return "Simulation started";
	}
}



