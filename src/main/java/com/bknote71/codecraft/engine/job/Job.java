package com.bknote71.codecraft.engine.job;

public class Job implements IJob {

    private Runnable action;

    public Job(Runnable action) {
        this.action = action;
    }

    @Override
    public void execute() {
        action.run();
    }
}
