package com.julie.store.road;

import org.springframework.stereotype.Component;

public enum RoadSize {

    NorthSize(340, 460, 20, 340),
    EastSize(460, 780, 340, 460),
    SouthSize(340, 460, 460, 780),
    WestSize(20, 340, 340, 460);


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
            case NorthSize -> getYDown() + 90;
            case EastSize -> getXLeft() - 90;
            case SouthSize -> getYUp() - 90;
            case WestSize -> getXRight() + 90;
        };
    }

    public int getTurnRightPoint() {
        return switch (this) {
            case NorthSize -> getYDown() + 10;
            case EastSize -> getXLeft() - 10;
            case SouthSize -> getYUp() - 10;
            case WestSize -> getXRight() + 10;
        };
    }

    public int getTurnLeftPointEmer() {
        return switch (this) {
            case NorthSize -> getYDown() + 70;
            case EastSize -> getXLeft() - 70;
            case SouthSize -> getYUp() - 70;
            case WestSize -> getXRight() + 70;
        };
    }

    public int getTurnRightPointEmer() {
        return switch (this) {
            case NorthSize -> getYDown() + 50;
            case EastSize -> getXLeft() - 50;
            case SouthSize -> getYUp() - 50;
            case WestSize -> getXRight() + 50;
        };
    }

}
