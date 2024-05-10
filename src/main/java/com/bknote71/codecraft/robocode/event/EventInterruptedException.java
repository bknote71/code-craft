package com.bknote71.codecraft.robocode.event;

public class EventInterruptedException extends Error { // Must be an Error!
    private int priority = Integer.MIN_VALUE;

    public EventInterruptedException(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
