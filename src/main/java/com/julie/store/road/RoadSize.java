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

    public int getTurnLeftPoint() {
        return switch (this) {
            case NorthSize -> getYDown() + 135;
            case EastSize -> getXLeft() - 135;
            case SouthSize -> getYUp() - 135;
            case WestSize -> getXRight() + 135;
        };
    }

    public int getTurnRightPoint() {
        return switch (this) {
            case NorthSize -> getYDown() + 15;
            case EastSize -> getXLeft() - 15;
            case SouthSize -> getYUp() - 15;
            case WestSize -> getXRight() + 15;
        };
    }

    public int getTurnLeftPointEmer() {
        return switch (this) {
            case NorthSize -> getYDown() + 105;
            case EastSize -> getXLeft() - 105;
            case SouthSize -> getYUp() - 105;
            case WestSize -> getXRight() + 105;
        };
    }

    public int getTurnRightPointEmer() {
        return switch (this) {
            case NorthSize -> getYDown() + 75;
            case EastSize -> getXLeft() - 75;
            case SouthSize -> getYUp() - 75;
            case WestSize -> getXRight() + 75;
        };
    }

}
