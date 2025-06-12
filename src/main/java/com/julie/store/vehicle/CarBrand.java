package com.julie.store.vehicle;

import java.util.Random;

public enum CarBrand {
    Ford(4,5, 26, 14),
    Audi(4,5, 26, 14),
    Honda(4,5, 26, 14),
    Toyota(4, 5, 26, 16),
    Delivery(3,4, 36, 16),
    MiniVan(3,4, 26, 16),
    Ambulance(5, 6, 28, 16),
    FireTruck(5,6, 36, 20),
    Police(5, 6, 26, 16);


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
