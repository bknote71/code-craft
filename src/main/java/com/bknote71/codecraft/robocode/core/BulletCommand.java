package com.bknote71.codecraft.robocode.core;

public class BulletCommand {

    private final double power;
    private final boolean fireAssistValid;
    private final double fireAssistAngle;
    private final int bulletId;

    public BulletCommand(double power, boolean fireAssistValid, double fireAssistAngle, int bulletId) {
        this.fireAssistValid = fireAssistValid;
        this.fireAssistAngle = fireAssistAngle;
        this.bulletId = bulletId;
        this.power = power;
    }

    public boolean isFireAssistValid() {
        return fireAssistValid;
    }

    public int getBulletId() {
        return bulletId;
    }

    public double getPower() {
        return power;
    }

    public double getFireAssistAngle() {
        return fireAssistAngle;
    }

}
