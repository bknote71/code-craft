package com.bknote71.codecraft.robocode.robointerface;

import com.bknote71.codecraft.robocode.event.*;

public interface IBasicEvent {
    void onStatus(StatusEvent statusEvent);

    void onHitByBullet(HitByBulletEvent hitByBulletEvent);

    void onScannedRobot(ScannedRobotEvent scannedRobotEvent);

    void onHitRobot(HitRobotEvent hitRobotEvent);

    void onBulletHit(BulletHitEvent bulletHitEvent);
}
