package com.bknote71.codecraft.engine.robointerface;

import com.bknote71.codecraft.engine.event.*;

public interface IBasicEvent {
    void onStatus(StatusEvent statusEvent);

    void onHitByBullet(HitByBulletEvent hitByBulletEvent);

    void onScannedRobot(ScannedRobotEvent scannedRobotEvent);

    void onHitRobot(HitRobotEvent hitRobotEvent);

    void onBulletHit(BulletHitEvent bulletHitEvent);
}
