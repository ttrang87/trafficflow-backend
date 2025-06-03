package com.julie.store.vehicle;

import java.util.Random;

public enum CarBrand {
//    Ford(18,24),
    Audi(16,22),
    Tesla(18, 24),
    Delivery(12,16),
    MiniVan(14,18),
    Ambulance(18,24),
    FireTruck(20,24),
    Police(18, 26);


    private final int lower;
    private final int upper;

    CarBrand(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public int getStartSpeed() {
        Random random = new Random();
        return random.nextInt(lower, upper+1);
    }

}
