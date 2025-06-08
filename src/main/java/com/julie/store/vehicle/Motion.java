package com.julie.store.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.julie.store.road.Road;
import com.julie.store.road.RoadSize;

public abstract class Motion {
    private int x;
    private int y;
    private final int initialSpeed;
    @JsonIgnore
    private final Road position;
    @JsonIgnore
    private final Road goal;
    private final String relationship;
    private int direction;
    private int turnDistance = 0;
    private boolean isTurn = false;
    private boolean outLane = false;
    private int speed;
    private int dangerousDistance;
    private boolean switchingLane = false;
    private int switchProgress = 0;
    private String changeLane;



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

    public int getInitialSpeed() { return this.initialSpeed; }

    public int getDangerousDistance() { return this.dangerousDistance; }

    public boolean getSwitch() {return this.switchingLane; };


    public void goStraight() {
        if (switchingLane) {
            switchProgress += speed;

            int shiftAmount = 25; // lane width
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
                goal.getLane1().changeIsSwitching(false);
                goal.getLane2().changeIsSwitching(false);
            }
        }


        if (outLane) {
            turnDistance += speed;
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
        outLane = !outLane;
        speed = initialSpeed;
    }

    public void changeSpeed(int speed) {
        this.speed = speed;
    }

    public void moveOut() {
        if (relationship.equals("RIGHT") && turnDistance >= 15 && !isTurn) {
            isTurn = true;
            turnRight();
        } else if (relationship.equals("LEFT") && turnDistance >= 135 && !isTurn){
            isTurn = true;
            turnLeft();
        }
        moveSafe();
    }

    public void switchLane(String changeLane) {
        switchingLane = true;
        this.changeLane = changeLane;
        switchProgress = 0;
    }
}
