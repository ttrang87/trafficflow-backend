package com.julie.store;

import com.julie.store.road.RoadService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@RestController
public class StoreApplication {
	private final RoadService roadService;

    public StoreApplication(RoadService roadService) {
        this.roadService = roadService;
    }

    public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
	}


	@PostConstruct
	public void initSimulationOnStartup() {
		System.out.println("🚦 Auto-starting traffic simulation...");
		roadService.startSimulation();
	}
}



