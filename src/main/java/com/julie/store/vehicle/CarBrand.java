package com.julie.store.vehicle;

import java.util.Random;

public enum CarBrand {
    Toyota(32,50),
    Ford(34,60),
    Audi(40,66),
    Tesla(42, 64),
    UPS(24,34),
    MiniVan(32,44),
    Ambulance(38,60),
    FireTruck(40,62),
    Police(40, 66);


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
