package com.bknote71.codecraft.robocode.event;

import com.bknote71.codecraft.robocode.api.Robot;
import com.bknote71.codecraft.robocode.core.RobotStatics;

public abstract class Event implements Comparable<Event> {

    private static final int DEFAULT_PRIORITY = 80;

    private long time;
    private int priority;

    private boolean addedToQueue;

    @Override
    public int compareTo(Event event) {
        int timeDiff = (int) (time - event.time);

        if (timeDiff != 0) {
            // Time differ
            return timeDiff;
        }

        int priorityDiff = event.getPriority() - getPriority();
        return priorityDiff;
    }

    public abstract void dispatch(Robot robot, RobotStatics statics);

    public long getTime() {
        return time;
    }

    public void setTime(long newTime) {
        if (addedToQueue) {
            System.out.println((
                    "SYSTEM: The time of an event cannot be changed after it has been added the event queue."));
        } else {
            time = newTime;
        }
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        if (priority < 0)
            priority = 0;
        else if (priority > 99)
            priority = 99;
        this.priority = priority;
    }

    public void setDefaultPriority(Event e) {
        setPriority(e.getDefaultPriority());
    }

    private int getDefaultPriority() {
        return DEFAULT_PRIORITY;
    }
}
