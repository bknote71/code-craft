package com.bknote71.codecraft.robocode.event;


import com.bknote71.codecraft.robocode.api.Robot;
import com.bknote71.codecraft.robocode.core.RobotStatics;
import com.bknote71.codecraft.robocode.robointerface.IBasicEvent;

public class ScannedRobotEvent extends Event {

    private final static int DEFAULT_PRIORITY = 10;

    private final String name;
    private final double energy;
    private final double heading;
    private final double bearing; // 스캐닝 하는 각도
    private final double distance;
    private final double velocity;
    private final boolean isSentryRobot;

    public ScannedRobotEvent() {
        this(null, 0, 0, 0, 0, 0, false);
    }

    public ScannedRobotEvent(String name, double energy, double bearing, double distance, double heading, double velocity) {
        this(name, energy, bearing, distance, heading, velocity, false);
    }

    public ScannedRobotEvent(String name, double energy, double bearing, double distance, double heading, double velocity, boolean isSentryRobot) {
        super();
        this.name = name;
        this.energy = energy;
        this.bearing = bearing;
        this.distance = distance;
        this.heading = heading;
        this.velocity = velocity;
        this.isSentryRobot = isSentryRobot;
    }

    public double getBearing() {
        return bearing * 180.0 / Math.PI;
    }

    public double getBearingRadians() {
        return bearing;
    }

    // 스캔한 대상까지의 거리?
    public double getDistance() {
        return distance;
    }

    // 스캔한 로봇의 에너지(hp??)
    public double getEnergy() {
        return energy;
    }

    // in degrees
    public double getHeading() {
        return heading * 180.0 / Math.PI;
    }

    public double getHeadingRadians() {
        return heading;
    }

    public String getName() {
        return name;
    }

    public double getVelocity() {
        return velocity;
    }

    public int getDefaultPriority() {
        return DEFAULT_PRIORITY;
    }

    @Override
    public final void dispatch(Robot robot, RobotStatics statics) {
        IBasicEvent listener = robot.getBasicEvent();
        if (listener != null) {
            listener.onScannedRobot(this);
        }
    }
}

