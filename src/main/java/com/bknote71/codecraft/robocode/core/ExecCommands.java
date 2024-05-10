package com.bknote71.codecraft.robocode.core;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class ExecCommands {
    public static final int defaultBodyColor = 0xFF29298C;
    public static final int defaultGunColor = 0xFF29298C;
    public static final int defaultRadarColor = 0xFF29298C;
    public static final int defaultScanColor = 0xFF0000FF;
    public static final int defaultBulletColor = 0xFFFFFFFF;

    private double bodyTurnRemaining;
    private double radarTurnRemaining;
    private double gunTurnRemaining;
    private double distanceRemaining;

    private boolean isAdjustGunForBodyTurn;
    private boolean isAdjustRadarForGunTurn;
    private boolean isAdjustRadarForBodyTurn;
    private boolean isAdjustRadarForBodyTurnSet;

    private int bodyColor = defaultBodyColor;
    private int gunColor = defaultGunColor;
    private int radarColor = defaultRadarColor;
    private int scanColor = defaultScanColor;
    private int bulletColor = defaultBulletColor;
    private double maxTurnRate;
    private double maxVelocity;

    private boolean moved;
    private boolean scan;
    private boolean isIORobot;
    private boolean isTryingToPaint;
    private List<BulletCommand> bullets = new ArrayList<BulletCommand>();

    public ExecCommands() {
        setMaxVelocity(Double.MAX_VALUE);
        setMaxTurnRate(Double.MAX_VALUE);
    }

    public ExecCommands(ExecCommands origin, boolean fromRobot) {
        bodyTurnRemaining = origin.bodyTurnRemaining;
        radarTurnRemaining = origin.radarTurnRemaining;
        gunTurnRemaining = origin.gunTurnRemaining;
        distanceRemaining = origin.distanceRemaining;
        isAdjustGunForBodyTurn = origin.isAdjustGunForBodyTurn;
        isAdjustRadarForGunTurn = origin.isAdjustRadarForGunTurn;
        isAdjustRadarForBodyTurn = origin.isAdjustRadarForBodyTurn;
        isAdjustRadarForBodyTurnSet = origin.isAdjustRadarForBodyTurnSet;
        maxTurnRate = origin.maxTurnRate;
        maxVelocity = origin.maxVelocity;
        copyColors(origin);
        if (fromRobot) {
            bullets = origin.bullets;
            scan = origin.scan;
            moved = origin.moved;
        }
    }

    public void copyColors(ExecCommands origin) {
        if (origin != null) {
            bodyColor = origin.bodyColor;
            gunColor = origin.gunColor;
            radarColor = origin.radarColor;
            bulletColor = origin.bulletColor;
            scanColor = origin.scanColor;
        }
    }

    public int getBodyColor() {
        return bodyColor;
    }

    public int getRadarColor() {
        return radarColor;
    }

    public int getGunColor() {
        return gunColor;
    }

    public int getBulletColor() {
        return bulletColor;
    }

    public int getScanColor() {
        return scanColor;
    }

    public void setBodyColor(int color) {
        bodyColor = color;
    }

    public void setRadarColor(int color) {
        radarColor = color;
    }

    public void setGunColor(int color) {
        gunColor = color;
    }

    public void setBulletColor(int color) {
        bulletColor = color;
    }

    public void setScanColor(int color) {
        scanColor = color;
    }

    public double getBodyTurnRemaining() {
        return bodyTurnRemaining;
    }

    public void setBodyTurnRemaining(double bodyTurnRemaining) {
        this.bodyTurnRemaining = bodyTurnRemaining;
    }

    public double getRadarTurnRemaining() {
        return radarTurnRemaining;
    }

    public void setRadarTurnRemaining(double radarTurnRemaining) {
        this.radarTurnRemaining = radarTurnRemaining;
    }

    public double getGunTurnRemaining() {
        return gunTurnRemaining;
    }

    public void setGunTurnRemaining(double gunTurnRemaining) {
        this.gunTurnRemaining = gunTurnRemaining;
    }

    public double getDistanceRemaining() {
        return distanceRemaining;
    }

    public void setDistanceRemaining(double distanceRemaining) {
        this.distanceRemaining = distanceRemaining;
    }

    public boolean isAdjustGunForBodyTurn() {
        return isAdjustGunForBodyTurn;
    }

    public void setAdjustGunForBodyTurn(boolean adjustGunForBodyTurn) {
        isAdjustGunForBodyTurn = adjustGunForBodyTurn;
    }

    public boolean isAdjustRadarForGunTurn() {
        return isAdjustRadarForGunTurn;
    }

    public void setAdjustRadarForGunTurn(boolean adjustRadarForGunTurn) {
        isAdjustRadarForGunTurn = adjustRadarForGunTurn;
    }

    public boolean isAdjustRadarForBodyTurn() {
        return isAdjustRadarForBodyTurn;
    }

    public void setAdjustRadarForBodyTurn(boolean adjustRadarForBodyTurn) {
        isAdjustRadarForBodyTurn = adjustRadarForBodyTurn;
    }

    public boolean isAdjustRadarForBodyTurnSet() {
        return isAdjustRadarForBodyTurnSet;
    }

    public void setAdjustRadarForBodyTurnSet(boolean adjustRadarForBodyTurnSet) {
        isAdjustRadarForBodyTurnSet = adjustRadarForBodyTurnSet;
    }

    public double getMaxTurnRate() {
        return maxTurnRate;
    }

    public void setMaxTurnRate(double maxTurnRate) {
        this.maxTurnRate = Math.min(abs(maxTurnRate), Rules.MAX_TURN_RATE_RADIANS);
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = Math.min(abs(maxVelocity), Rules.MAX_VELOCITY);
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public boolean isScan() {
        return scan;
    }

    public void setScan(boolean scan) {
        this.scan = scan;
    }

    public List<BulletCommand> getBullets() {
        return bullets;
    }

    public boolean isIORobot() {
        return isIORobot;
    }

    public void setIORobot() {
        isIORobot = true;
    }

    public boolean isTryingToPaint() {
        return isTryingToPaint;
    }

    public void setTryingToPaint(boolean tryingToPaint) {
        isTryingToPaint = tryingToPaint;
    }
}
