package com.bknote71.codecraft.robocode.event;

import com.bknote71.codecraft.robocode.api.Robot;
import com.bknote71.codecraft.robocode.core.BulletPeer;
import com.bknote71.codecraft.robocode.core.RobotStatics;
import com.bknote71.codecraft.robocode.robointerface.IBasicEvent;

public class BulletHitEvent extends Event {
    private final static int DEFAULT_PRIORITY = 50;

    private final String name;
    private final double energy;
    private final BulletPeer bullet;

    public BulletHitEvent(String name, double energy, BulletPeer bullet) {
        super();
        this.name = name;
        this.energy = energy;
        this.bullet = bullet;
    }

    @Override
    public void dispatch(Robot robot, RobotStatics statics) {
        IBasicEvent basicEvent = robot.getBasicEvent();
        if (basicEvent != null) {
            basicEvent.onBulletHit(this);
        }
    }
}
