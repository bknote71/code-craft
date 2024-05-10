package com.bknote71.codecraft.robocode.core;

public class BulletStatus {
    public int bulletId;
    public double x;
    public double y;
    public String victimName;
    public boolean isActive;

    public BulletStatus(int bulletId, double x, double y, String victimName, boolean isActive) {
        this.bulletId = bulletId;
        this.x = x;
        this.y = y;
        this.isActive = isActive;
        this.victimName = victimName;
    }

}
