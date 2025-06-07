package com.julie.store.road;

import org.springframework.stereotype.Component;

public enum RoadSize {

    NorthSize(420, 600, 40, 400),
    EastSize(600, 960, 400, 580),
    SouthSize(420, 600, 580, 940),
    WestSize(60, 420, 400, 580);


    private final int xLeft;
    private final int xRight;
    private final int yUp;
    private final int yDown;

    RoadSize(int xLeft, int xRight, int yUp, int yDown) {
        this.xLeft = xLeft;
        this.xRight = xRight;
        this.yUp = yUp;
        this.yDown = yDown;
    }

    public int getXLeft() { return xLeft; }
    public int getXRight() { return xRight; }
    public int getYUp() { return yUp; }
    public int getYDown() { return yDown; }
}
