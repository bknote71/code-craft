package com.bknote71.codecraft.robocode.robointerface;

import com.bknote71.codecraft.robocode.api.Bullet;

public interface IBasicRobotPeer {
    void move(double distance);

    double getBattleFieldWidth();

    double getBattleFieldHeight();

    double getBodyHeading();

    String getName();

    double getX();

    double getY();

    void turnBody(double v);

    void execute();

    Bullet setFire(double power);

    Bullet fire(double power);

    double getGunCoolingRate();

    double getGunHeading();

    void rescan();

    void turnGun(double v);

    double getRadarHeading();

    int getOthers();

    double getGunHeat();

    void setRobotImage(String robotImageName);
}
