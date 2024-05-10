package com.bknote71.codecraft.robocode.api;

import com.bknote71.codecraft.robocode.core.Rules;

public class Bullet {
    private final double headingRadians;
    private double x;
    private double y;
    private final double power;
    private final String ownerName;
    private String victimName;
    private boolean isActive;
    private final int bulletId;

    public Bullet(double heading, double x, double y, double power, String ownerName, String victimName, boolean isActive, int bulletId) {
        this.headingRadians = heading;
        this.x = x;
        this.y = y;
        this.power = power;
        this.ownerName = ownerName;
        this.victimName = victimName;
        this.isActive = isActive;
        this.bulletId = bulletId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // bulletId is unique to single round and robot owner
        return bulletId == ((Bullet) obj).bulletId;
    }

    @Override
    public int hashCode() {
        return bulletId;
    }

    public double getHeading() {
        return Math.toDegrees(headingRadians);
    }

    public double getHeadingRadians() {
        return headingRadians;
    }

    public String getName() {
        return ownerName;
    }

    public double getPower() {
        return power;
    }

    public double getVelocity() {
        return Rules.getBulletSpeed(power);
    }

    public String getVictim() {
        return victimName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isActive() {
        return isActive;
    }

    public void update(double x, double y, String victimName, boolean isActive) {
        this.x = x;
        this.y = y;
        this.victimName = victimName;
        this.isActive = isActive;
    }

    int getBulletId() {
        return bulletId;
    }


}
