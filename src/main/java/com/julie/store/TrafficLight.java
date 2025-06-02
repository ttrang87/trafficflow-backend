package com.julie.store;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public class TrafficLight {
    private int second;
    private String color;
    private int count;

    public TrafficLight(String color, int second, int count) {
        this.color = color;
        this.second = second;
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    public void countDown() {
        while (true) {
            this.count += 1;
            if (count == this.second) {
                switch (this.color) {
                    case "GREEN" -> this.changeGreen();
                    case "YELLOW" -> this.changeYellow();
                    default -> this.changeRed();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

    }

    public String getColor(){
        return this.color;
    }

    public void changeGreen() {
        this.color = "YELLOW";
        this.second = 3;
        this.count = 0;
    }

    public void changeYellow() {
        this.color = "RED";
        this.second = 30;
        this.count = 0;
    }

    public void changeRed() {
        this.color = "GREEN";
        this.second = 7;
        this.count = 0;
    }

}
