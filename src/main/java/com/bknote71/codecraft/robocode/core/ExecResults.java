package com.bknote71.codecraft.robocode.core;


import com.bknote71.codecraft.robocode.event.Event;

import java.util.List;

public final class ExecResults {
    private ExecCommands commands;
    private RobotStatus status;
    private List<Event> events;
    private List<BulletStatus> bulletUpdates;
    private boolean halt;
    private boolean shouldWait;
    private boolean paintEnabled;

    public ExecResults(ExecCommands commands, RobotStatus status, List<Event> events, List<BulletStatus> bulletUpdates, boolean halt, boolean paintEnabled) {
        this.commands = commands;
        this.status = status;
        this.events = events;
        this.bulletUpdates = bulletUpdates;
        this.halt = halt;
        // this.shouldWait = shouldWait;
        this.paintEnabled = paintEnabled;
    }

    private ExecResults() {
    }

    public ExecCommands getCommands() {
        return commands;
    }

    public RobotStatus getStatus() {
        return status;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<BulletStatus> getBulletUpdates() {
        return bulletUpdates;
    }

    public boolean isHalt() {
        return halt;
    }

    public boolean isShouldWait() {
        return shouldWait;
    }

    public boolean isPaintEnabled() {
        return paintEnabled;
    }

}
