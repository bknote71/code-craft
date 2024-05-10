package com.bknote71.codecraft.robocode.core;

import java.util.HashMap;
import java.util.Map;

public class RobotManager {

    public static RobotManager Instance = new RobotManager();

    private Object lock = new Object();
    private Map<Integer, RobotPeer> robots = new HashMap<>();

    private int id;

    public RobotPeer create() {
        synchronized (lock) {
            int robotId = ++id;
            RobotPeer robotPeer = new RobotPeer(robotId);
            robots.put(robotId, robotPeer); // 만일을 위해 남겨둔다.
            return robotPeer;
        }
    }

    public RobotPeer find(int robotId) {
        synchronized (lock) {
            return robots.get(robotId);
        }
    }

    public void remove(int robotId) {
        synchronized (lock) {
            robots.remove(robotId);
        }
    }
}
