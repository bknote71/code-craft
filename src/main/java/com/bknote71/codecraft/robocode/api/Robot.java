package com.bknote71.codecraft.robocode.api;

import com.bknote71.codecraft.robocode.core.BulletPeer;
import com.bknote71.codecraft.robocode.event.*;
import com.bknote71.codecraft.robocode.robointerface.IBasicEvent;
import com.bknote71.codecraft.robocode.robointerface.IBasicRobotPeer;

public abstract class Robot implements IBasicEvent, Runnable {
    private static final int
            WIDTH = 36,
            HEIGHT = 36;

    // 프록시 피어
    private IBasicRobotPeer peer;

    public IBasicEvent getBasicEvent() {
        return this;
    }

    public void setPeer(IBasicRobotPeer iRobotPeer) {
        peer = iRobotPeer;
    }

    public abstract void run();

    public Runnable getRobotRunnable() {
        return this;
    }

    // my commands
    public final void ahead(double distance) {
        if (peer != null) {
            peer.move(distance);
        } else {
            uninitializedException();
        }
    }

    public final void back(double distance) {
        if (peer != null) {
            peer.move(-distance);
        } else {
            uninitializedException();
        }
    }

    public final void fire(double power) {
        if (peer != null) {
            peer.setFire(power);
            peer.execute();
        } else {
            uninitializedException();
        }
    }

    public Bullet fireBullet(double power) {
        if (peer != null) {
            return peer.fire(power);
        }
        uninitializedException();
        return null;
    }

    public void scan() {
        if (peer != null) {
            peer.rescan();
        } else {
            uninitializedException();
        }
    }

    public final void turnLeft(double degrees) {
        if (peer != null) {
            peer.turnBody(-Math.toRadians(degrees));
        } else {
            uninitializedException();
        }
    }

    public final void turnRight(double degrees) {
        if (peer != null) {
            peer.turnBody(Math.toRadians(degrees));
        } else {
            uninitializedException();
        }
    }

    public void turnGunLeft(double degrees) {
        if (peer != null) {
            peer.turnGun(-Math.toRadians(degrees));
        } else {
            uninitializedException();
        }
    }

    public void turnGunRight(double degrees) {
        if (peer != null) {
            peer.turnGun(Math.toRadians(degrees));
        } else {
            uninitializedException();
        }
    }

    public void robot_image(String robotImageName) {
        if (peer != null) {
            peer.setRobotImage(robotImageName);
        } else {
            uninitializedException();
        }
    }

    // 사용 안할 커맨드

    public double getBattleFieldWidth() {
        if (peer != null) {
            return peer.getBattleFieldWidth();
        }
        uninitializedException();
        return 0; // never called
    }

    public double getBattleFieldHeight() {
        if (peer != null) {
            return peer.getBattleFieldHeight();
        }
        uninitializedException();
        return 0; // never called
    }

    public double getHeading() {
        if (peer != null) { // direction, in degrees
            double rv = 180.0 * peer.getBodyHeading() / Math.PI;

            while (rv < 0) {
                rv += 360;
            }
            while (rv >= 360) {
                rv -= 360;
            }
            return rv;
        }
        uninitializedException();
        return 0; // never called
    }

    public double getHeight() {
        if (peer == null) {
            uninitializedException();
        }
        return HEIGHT;
    }

    public double getWidth() {
        if (peer == null) {
            uninitializedException();
        }
        return WIDTH;
    }


    public String getName() {
        if (peer != null) {
            return peer.getName();
        }
        uninitializedException();
        return null; // never called
    }

    public double getX() {
        if (peer != null) {
            return peer.getX();
        }
        uninitializedException();
        return 0; // never called
    }

    public double getY() {
        if (peer != null) {
            return peer.getY();
        }
        uninitializedException();
        return 0; // never called
    }

    public void doNothing() {
        if (peer != null) {
            peer.execute();
        } else {
            uninitializedException();
        }
    }

    public double getGunCoolingRate() {
        if (peer != null) {
            return peer.getGunCoolingRate();
        }
        uninitializedException();
        return 0; // never called
    }

    public double getGunHeading() {
        if (peer != null) {
            return peer.getGunHeading() * 180.0 / Math.PI;
        }
        uninitializedException();
        return 0; // never called
    }

    public double getGunHeat() {
        if (peer != null) {
            return peer.getGunHeat();
        }
        uninitializedException();
        return 0; // never called
    }

    public int getOthers() {
        if (peer != null) {
            return peer.getOthers();
        }
        uninitializedException();
        return 0; // never called
    }

    public double getRadarHeading() {
        if (peer != null) {
            return peer.getRadarHeading() * 180.0 / Math.PI;
        }
        uninitializedException();
        return 0; // never called
    }

    // 이벤트
    public void onDeath(DeathEvent event) {
    }

    public void onHitRobot(HitRobotEvent event) {
    }

    @Override
    public void onStatus(StatusEvent statusEvent) {

    }

    @Override
    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
    }

    @Override
    public void onBulletHit(BulletHitEvent bulletHitEvent) {

    }

    public void onWin(WinEvent event) {
    }

    private void uninitializedException() {
        throw new RuntimeException("you cannot call");
    }
}
