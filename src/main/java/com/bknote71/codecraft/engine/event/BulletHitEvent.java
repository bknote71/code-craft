package com.bknote71.codecraft.engine.event;

import com.bknote71.codecraft.engine.api.Robot;
import com.bknote71.codecraft.engine.core.BulletPeer;
import com.bknote71.codecraft.engine.core.RobotStatics;
import com.bknote71.codecraft.engine.robointerface.IBasicEvent;

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
