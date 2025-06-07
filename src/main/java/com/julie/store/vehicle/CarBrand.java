package com.julie.store.vehicle;

import java.util.Random;

public enum CarBrand {
    Ford(2,4, 40, 20),
    Audi(3,5, 46, 20),
    Honda(3,4, 40, 20),
    Toyota(3, 5, 40, 16),
    Delivery(2,3, 86, 30),
    MiniVan(2,4, 40, 20),
    Ambulance(2, 6, 40, 16),
    FireTruck(3,6, 50, 20),
    Police(3, 6, 42, 18);


    private final int lower;
    private final int upper;
    private final int length;
    private final int width;

    CarBrand(int lower, int upper, int length, int width) {
        this.lower = lower;
        this.upper = upper;
        this.length = length;
        this.width = width;
    }

    public int getStartSpeed() {
        Random random = new Random();
        return random.nextInt(lower, upper+1);
    }

    public int getLength() { return this.length; }

    public int getWidth() { return this.width; }

}
