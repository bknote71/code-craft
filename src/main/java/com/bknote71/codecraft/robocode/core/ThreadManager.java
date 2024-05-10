package com.bknote71.codecraft.robocode.core;

import com.bknote71.codecraft.robocode.proxy.RobotProxy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ThreadManager {

    private final List<Thread> safeThreads = new CopyOnWriteArrayList<Thread>();
    private final List<ThreadGroup> safeThreadGroups = new CopyOnWriteArrayList<ThreadGroup>();
    private final List<ThreadGroup> groups = new CopyOnWriteArrayList<ThreadGroup>();
    private final List<RobotProxy> robots = new CopyOnWriteArrayList<>();

    private Thread robotLoaderThread;
    private RobotProxy loadingRobot;

    public ThreadManager() {}

    public void addSafeThread(Thread safeThread) {
        safeThreads.add(safeThread);
    }

    public void removeSafeThread(Thread safeThread) {
        safeThreads.remove(safeThread);
    }

    public void addSafeThreadGroup(ThreadGroup safeThreadGroup) {
        safeThreadGroups.add(safeThreadGroup);
    }

    public void reset() {
        groups.clear();
        robots.clear();
    }

    public void addThreadGroup(ThreadGroup g, RobotProxy robotProxy) {
        if (!groups.contains(g)) {
            groups.add(g);
            robots.add(robotProxy);
        }
    }

    public synchronized RobotProxy getLoadingRobotProxy(Thread t) {
        if (t != null && robotLoaderThread != null
                && (t.equals(robotLoaderThread)
                || (t.getThreadGroup() != null && t.getThreadGroup().equals(robotLoaderThread.getThreadGroup())))) {
            return loadingRobot;
        }
        return null;
    }

    public synchronized RobotProxy getLoadedOrLoadingRobotProxy(Thread t) {
        RobotProxy robotProxy = getRobotProxy(t);

        if (robotProxy == null) {
            robotProxy = getLoadingRobotProxy(t);
        }
        return robotProxy;
    }

    public RobotProxy getRobotProxy(Thread t) {
        ThreadGroup g = t.getThreadGroup();

        if (g == null) {
            return null;
        }
        int index = groups.indexOf(g);

        if (index == -1) {
            return null;
        }
        return robots.get(index);
    }


}
