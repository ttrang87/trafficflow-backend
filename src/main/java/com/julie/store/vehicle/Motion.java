package com.julie.store.vehicle;

import com.julie.store.road.Road;
import com.julie.store.road.RoadSize;

public abstract class Motion {
    private int x;
    private int y;
    private final int initialSpeed;
    private final Road position;
    private final Road goal;
    //check dangerous distance
    private final String relationship;
    private int direction;
    private int turnDistance = 0;
    private boolean outPosition = false;
    private int speed;
    private int dangerousDistance;


    public Motion(int x, int y, int initialSpeed, Road position, Road goal){
        this.x= x;
        this.y = y;
        this.initialSpeed = initialSpeed;
        this.position = position;
        this.goal = goal;
        this.relationship = getRelationship();
        this.direction = getDirection();
        this.speed = initialSpeed;
    }

    public String getRelationship () {
        RoadSize go = this.position.getRoadSize();
        RoadSize to = this.goal.getRoadSize();
        int angle = (go.ordinal() - to.ordinal()) % 4;
        return switch(angle) {
            case 1 -> "RIGHT";
            case 2 -> "OPPOSITE";
            default -> "LEFT";
        };
    }

    public int getDirection() {
        RoadSize go = this.position.getRoadSize();
        return (go.ordinal()+2)%4;
    }

    public void changeSpeed(int newSpeed) {
        this.speed = newSpeed;
    }

    public void changeDangerousDistance(int newDangerous) {
        this.dangerousDistance = newDangerous;
    }

    public int getX() { return this.x;}

    public int getY() { return this.y;}


    public void goStraight() {
        if (outPosition) {
            turnDistance += speed;
        }
        if (direction == 1) {
            x += speed;
        } else if (direction == 3) {
            x -= speed;
        } else if (direction == 0) {
            y += speed;
        } else {
            y -= speed;
        }
    }

    public void run() {
        speed = initialSpeed;
        goStraight();
    }

    public void stop() {
        speed = 0;
        goStraight();
    }

    public void slow() {
        speed -= 10;
        goStraight();
    }

    //for behind vehicles
    public void moveSafe() {
        if (dangerousDistance <= 10) {
            stop();
        } else if (dangerousDistance < 30) {
            slow();
        } else {
            goStraight();
        }
    }

    public void turnRight() {
        direction = (direction + 1) % 4;
    }

    public void turnLeft() {
        direction = (direction - 1) % 4;
    }

    public void changeOutPosition() {
        outPosition = !outPosition;
    }

    //for the first vehicle only
    public void moveOut() {
        speed = initialSpeed;
        if (relationship.equals("RIGHT") && turnDistance == 60) {
            turnRight();
        } else if (relationship.equals("LEFT") && turnDistance == 150){
            turnLeft();
        }
        goStraight();
    }
}
