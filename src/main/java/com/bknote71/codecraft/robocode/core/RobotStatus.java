package com.bknote71.codecraft.robocode.core;

public class RobotStatus {

    private final double energy;
    private final double x;
    private final double y;
    private final double bodyHeading;
    private final double gunHeading;
    private final double radarHeading;
    private final double velocity;
    private final double bodyTurnRemaining;
    private final double radarTurnRemaining;
    private final double gunTurnRemaining;
    private final double distanceRemaining;
    private final double gunHeat;
    private final long time;
    private int others;

    public RobotStatus(double energy, double x, double y, double bodyHeading, double gunHeading, double radarHeading,
                        double velocity, double bodyTurnRemaining, double radarTurnRemaining, double gunTurnRemaining,
                        double distanceRemaining, double gunHeat, long time) {
        this.energy = energy;
        this.x = x;
        this.y = y;
        this.bodyHeading = bodyHeading;
        this.gunHeading = gunHeading;
        this.radarHeading = radarHeading;
        this.bodyTurnRemaining = bodyTurnRemaining;
        this.velocity = velocity;
        this.radarTurnRemaining = radarTurnRemaining;
        this.gunTurnRemaining = gunTurnRemaining;
        this.distanceRemaining = distanceRemaining;
        this.gunHeat = gunHeat;
        this.time = time;
    }

    public double getEnergy() {
        return energy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getBodyTurnRemaining() {
        return bodyTurnRemaining;
    }

    public double getRadarTurnRemaining() {
        return radarTurnRemaining;
    }

    public double getGunTurnRemaining() {
        return gunTurnRemaining;
    }

    public double getDistanceRemaining() {
        return distanceRemaining;
    }

    public double getGunHeat() {
        return gunHeat;
    }

    public int getOthers() {
        return others;
    }

    public void setOthers(int others) {
        this.others = others;
    }

    public long getTime() {
        return time;
    }

    public double getHeadingRadians() {
        return bodyHeading;
    }

    public double getHeading() {
        return Math.toDegrees(bodyHeading);
    }


    public double getGunHeadingRadians() {
        return gunHeading;
    }

    public double getGunHeading() {
        return Math.toDegrees(gunHeading);
    }

    public double getRadarHeadingRadians() {
        return radarHeading;
    }

    public double getRadarHeading() {
        return Math.toDegrees(radarHeading);
    }
}
