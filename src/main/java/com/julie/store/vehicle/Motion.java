package com.julie.store.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.julie.store.lane.InboundLane;
import com.julie.store.road.Road;
import com.julie.store.road.RoadSize;

public abstract class Motion {
    private int x;
    private int y;
    private int initialSpeed;
    @JsonIgnore
    private final Road position;
    @JsonIgnore
    private final Road goal;
    private final String relationship;
    private int direction;
    private boolean isTurn = false;
    private int speed;
    private int dangerousDistance;
    private boolean switchingLane = false;
    private int switchProgress = 0;
    private String changeLane;
    @JsonIgnore
    private InboundLane currentLane;
    private int insertIndex;

    private int totalTravelDistance = 0;
    private int totalTravelTime = 0;
    private int waitTime = 0;


    public Motion(int x, int y, int initialSpeed, Road position, Road goal){
        this.x= x;
        this.y = y;
        this.initialSpeed = initialSpeed;
        this.position = position;
        this.goal = goal;
        this.relationship = getRelationship();
        this.direction = (position.getRoadSize().ordinal()+2)%4;
        this.speed = initialSpeed;
    }

    public String getRelationship () {
        RoadSize go = this.position.getRoadSize();
        RoadSize to = this.goal.getRoadSize();
        int angle = (go.ordinal() - to.ordinal() + 4) % 4;
        return switch(angle) {
            case 1 -> "RIGHT";
            case 2 -> "OPPOSITE";
            default -> "LEFT";
        };
    }

    public int getDirection() {
        return direction;
    }

    public void changeDangerousDistance(int newDangerous) {
        this.dangerousDistance = newDangerous;
    }

    public int getX() { return this.x;}

    public int getY() { return this.y;}

    public Road getGoal() { return this.goal; }

    public int getSpeed() { return this.speed; }

    public void setSpeed(int newSpeed) { this.speed = newSpeed; }

    public int getInitialSpeed() { return this.initialSpeed; }

    public void setInitialSpeed(int newInitialSpeed) { this.initialSpeed = newInitialSpeed; }

    public int getDangerousDistance() { return this.dangerousDistance; }

    public boolean getSwitch() {return this.switchingLane; };

    public double getAvgSpeed() {
        if (totalTravelTime == 0) return 0.0;
        return (double) totalTravelDistance / totalTravelTime;
    }

    public int getWaitTime() { return waitTime; }

    public void goStraight() {
        totalTravelDistance += speed;
        totalTravelTime += 1;
        if (speed == 0) {
            waitTime ++;
        }

        if (switchingLane) {
            switchProgress += speed;

            int shiftAmount = 18; // lane width
            if (changeLane.equals("LEFT")) {
                switch (direction) {
                    case 0 -> x -= speed;
                    case 1 -> y -= speed;
                    case 2 -> x += speed;
                    case 3 -> y += speed;
                }
            } else {
                switch (direction) {
                    case 0 -> x += speed;
                    case 1 -> y += speed;
                    case 2 -> x -= speed;
                    case 3 -> y -= speed;
                }
            }

            if (switchProgress >= shiftAmount) {
                switchingLane = false;
                switchProgress = 0;

                // Complete the lane switch through the current lane
                this.currentLane.completeLaneSwitch((Vehicle) this, insertIndex);

            }
        }

        switch (direction) {
            case 0 -> y -= speed;
            case 1 -> x += speed;
            case 2 -> y += speed;
            case 3 -> x -= speed;
        }
    }

    //for behind vehicles
    public void moveSafe() {
        if (dangerousDistance <= 2) {
            this.speed = 0;
        } else if (dangerousDistance <= this.speed) {
            this.speed = dangerousDistance - 1;
        } else {
            this.speed = initialSpeed;
        }
        goStraight();
    }

    public void turnRight() {
        direction = (direction + 1) % 4;
    }

    public void turnLeft() {
        direction = (direction + 3) % 4; // Equivalent to (direction - 1 + 4) % 4
    }

    public void changeOutLane() {
        speed = initialSpeed;
    }

    public void changeSpeed(int speed) {
        this.speed = speed;
    }

    public void moveOut() {
        RoadSize roadSize = position.getRoadSize();
        int turnLeftPoint = roadSize.getTurnLeftPoint();
        int turnRightPoint = roadSize.getTurnRightPoint();

        if (relationship.equals("RIGHT") && !isTurn) {
            // Vertical direction (North or South): use y-coordinate
            if ((direction == 0 && y <= turnRightPoint) ||
                    (direction == 2 && y >= turnRightPoint) ||
                    (direction == 1 && x >= turnRightPoint) ||
                    (direction == 3 && x <= turnRightPoint)) {
                isTurn = true;
                turnRight();
            }
        }
        else if (relationship.equals("LEFT") && !isTurn) {
            if ((direction == 0 && y <= turnLeftPoint) ||
                    (direction == 2 && y >= turnLeftPoint) ||
                    (direction == 1 && x >= turnLeftPoint) ||
                    (direction == 3 && x <= turnLeftPoint)) {
                isTurn = true;
                turnLeft();
            }
        }
        moveSafe();
    }

    public void moveOutEmer() {
        RoadSize roadSize = position.getRoadSize();
        int turnLeftPoint = roadSize.getTurnLeftPointEmer();
        int turnRightPoint = roadSize.getTurnRightPointEmer();

        if (relationship.equals("RIGHT") && !isTurn) {
            // Vertical direction (North or South): use y-coordinate
            if ((direction == 0 && y <= turnRightPoint) ||
                    (direction == 2 && y >= turnRightPoint) ||
                    (direction == 1 && x >= turnRightPoint) ||
                    (direction == 3 && x <= turnRightPoint)) {
                isTurn = true;
                turnRight();
            }
        }
        else if (relationship.equals("LEFT") && !isTurn) {
            if ((direction == 0 && y <= turnLeftPoint) ||
                    (direction == 2 && y >= turnLeftPoint) ||
                    (direction == 1 && x >= turnLeftPoint) ||
                    (direction == 3 && x <= turnLeftPoint)) {
                isTurn = true;
                turnLeft();
            }
        }
        moveSafe();
    }


    public void switchLane(String changeLane, InboundLane currentLane, int insertIndex) {
        switchingLane = true;
        switchProgress = 0;
        this.changeLane = changeLane;
        this.currentLane = currentLane;
        this.insertIndex = insertIndex;
    }
}
