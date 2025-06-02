package com.julie.store.vehicle;

import com.julie.store.road.Road;

public class Vehicle extends Motion {
    private final CarBrand brand;
    public Vehicle(int x, int y, Road position, Road goal, CarBrand brand) {
        super(x, y, brand.getStartSpeed(), position, goal);
        this.brand = brand;
    }

    public CarBrand getBrand() {
        return this.brand;
    }

}
