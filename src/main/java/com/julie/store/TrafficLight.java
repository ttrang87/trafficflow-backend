package com.julie.store;

import com.julie.store.lane.BaseLane;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public class TrafficLight {
    private int second;
    private String color;
    private int count;
    private String keepColor;
    private int keepCount;
    private volatile boolean isEmergency = false;
    protected static final Object pauseLock = new Object();
    protected static volatile boolean paused = false;
    protected static volatile boolean running = true;

    public static int redDuration = 30;
    public static int yellowDuration = 3;
    public static int greenDuration = 7;

    public TrafficLight(String color, int second, int count) {
        this.color = color;
        this.second = second;
        this.count = count;
        this.keepColor = color;
    }

    public void emergency() {
        if (!isEmergency) {
            this.isEmergency = true;
            this.keepColor = this.color;
            this.keepCount = this.count;
            this.color = "RED";
        }
    }

    public void resume() {
        this.isEmergency = false;
        this.color = this.keepColor;
        this.count = keepCount;
    }

    public String getColor(){
        return this.color;
    }

    public int getCount() {
        return this.count;
    }

    public boolean getEmergency(){
        return this.isEmergency;
    }

    //This part contains methods for changing duration


    public static int getRedDuration() {
        return redDuration;
    }

    public static int getYellowDuration() {
        return yellowDuration;
    }

    public static int getGreenDuration() {
        return greenDuration;
    }

    public static void setRedDuration(int newRedDuration) {
        redDuration = newRedDuration;
    }

    public static void setYellowDuration(int newYellowDuration) {
        yellowDuration = newYellowDuration;
    }

    public static void setGreenDuration(int newGreenDuration) {
        greenDuration = newGreenDuration;
    }

    public void changeGreen() {
        this.color = "YELLOW";
        this.second = yellowDuration;
        this.count = 0;
    }

    public void changeYellow() {
        this.color = "RED";
        this.second = redDuration;
        this.count = 0;
    }

    public void changeRed() {
        this.color = "GREEN";
        this.second = greenDuration;
        this.count = 0;
    }

    public void reset(String color, int second, int count) {
        this.color = color;
        this.second = second;
        this.count = count;
        this.isEmergency = false; // Reset emergency state
    }


    public static void setPaused(boolean paused) {
        synchronized (pauseLock) {
            TrafficLight.paused = paused;
            if (!paused) {
                pauseLock.notifyAll(); // Wake up waiting thread(s)
            }
        }
    }

    public static boolean isPaused() {
        return paused;
    }

    public static void setRunning(boolean isRunning) {
        running = isRunning;
    }

    public static boolean isRunning() {
        return running;
    }

    public void countDown() {
        while (running) {
            try {
                while (TrafficLight.isPaused() && TrafficLight.isRunning())  {
                    synchronized (pauseLock) {
                        pauseLock.wait();
                    }
                }

                if (!TrafficLight.isRunning()) {
                    break;
                }
                if (!isEmergency) {
                    this.count += 1;
                    if (count == this.second) {
                        switch (this.color) {
                            case "GREEN" -> this.changeGreen();
                            case "YELLOW" -> this.changeYellow();
                            default -> this.changeRed();
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

    }


}
