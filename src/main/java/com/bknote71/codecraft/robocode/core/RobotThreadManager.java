package com.bknote71.codecraft.robocode.core;

import com.bknote71.codecraft.robocode.proxy.RobotProxy;

// 로봇을 비동기적으로 실행할 스레드를 관리하는 매니저 (스레드 풀?)
public class RobotThreadManager {

    private RobotProxy robotProxy;
    private Thread runThread;
    private ThreadGroup runThreadGroup; // runThread 를 관리하기 위한 목적인가?

    public RobotThreadManager(RobotProxy robotProxy) {
        this.robotProxy = robotProxy;
        this.runThreadGroup = new ThreadGroup(robotProxy.getRobotPeer().getId() + ":" + robotProxy.getStatics().getName());

        // bit lower than battle have
        this.runThreadGroup.setMaxPriority(Thread.NORM_PRIORITY - 1);
    }

    public void cleanup() {
        System.out.println("robot thread group clean");
        if (runThread == null || robotProxy == null || robotProxy.getRobot() == null) {
            if (runThread != null)
                runThread.stop();
            else
                runThreadGroup.interrupt();
        }
    }

    public void checkRunThread() {
        if (Thread.currentThread() != runThread) {
            System.out.println("현재 스레드: " + Thread.currentThread().getName() + ", 객체: " + Thread.currentThread());
            System.out.println("run thread: " + runThread.getName() + ", 객체: " + Thread.currentThread());
            System.out.println("같지 않아? " + (Thread.currentThread() != runThread));
            throw new RobotException("You cannot take action in this thread!");
        }
    }

    // 모든 스레드를 관리하는 스레드 매니저
    public void start(ThreadManager threadManager) {
        try {
            // threadManager.addThreadGroup(runThreadGroup, robotProxy);
            runThread = new Thread(runThreadGroup, robotProxy, robotProxy.getRobotPeer().getId() + ":" + robotProxy.getStatics().getName());
            runThread.setDaemon(true);
            runThread.setPriority(Thread.NORM_PRIORITY - 1);
            runThread.setContextClassLoader(this.robotProxy.getRobotClassloader());
            runThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
