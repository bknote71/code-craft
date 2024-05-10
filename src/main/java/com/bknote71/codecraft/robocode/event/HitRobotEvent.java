package com.bknote71.codecraft.robocode.event;

import com.bknote71.codecraft.robocode.api.Robot;
import com.bknote71.codecraft.robocode.core.RobotStatics;
import com.bknote71.codecraft.robocode.robointerface.IBasicEvent;

public class HitRobotEvent extends Event {

    private final static int DEFAULT_PRIORITY = 40;

    private final String robotName;
    private final double bearing;
    private final double energy;
    private final boolean atFault;

    public HitRobotEvent(String name, double bearing, double energy, boolean atFault) {
        this.robotName = name;
        this.bearing = bearing;
        this.energy = energy;
        this.atFault = atFault;
    }

    public String getRobotName() {
        return robotName;
    }

    public double getBearing() {
        return bearing;
    }

    public double getEnergy() {
        return energy;
    }

    public boolean isMyFault() {
        return atFault;
    }

    @Override
    public final int compareTo(Event event) {
        final int res = super.compareTo(event);

        if (res != 0) {
            return res;
        }

        // Compare the isMyFault, if the events are HitRobotEvents
        // The isMyFault has higher priority when it is set compared to when it is not set
        if (event instanceof HitRobotEvent) {
            int compare1 = (this).isMyFault() ? -1 : 0;
            int compare2 = ((HitRobotEvent) event).isMyFault() ? -1 : 0;

            return compare1 - compare2;
        }

        // No difference found
        return 0;
    }


    public int getDefaultPriority() {
        return DEFAULT_PRIORITY;
    }

    @Override
    public void dispatch(Robot robot, RobotStatics statics) {
        IBasicEvent basicEvent = robot.getBasicEvent();
        if (basicEvent != null) {
            basicEvent.onHitRobot(this);
        }
    }
}
