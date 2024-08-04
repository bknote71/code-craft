package com.bknote71.codecraft.engine.proxy;

import com.bknote71.codecraft.compile.S3Uploader;
import com.bknote71.codecraft.engine.api.Robot;
import com.bknote71.codecraft.engine.loader.RobotClassLoader;
import com.bknote71.codecraft.engine.event.EventManager;
import com.bknote71.codecraft.engine.robointerface.IBasicRobotPeer;
import com.bknote71.codecraft.engine.robointerface.IRobotPeer;
import com.bknote71.codecraft.engine.core.*;

/**
 * 로봇 프록시
 * (커스텀) 로봇을 로드하고, 그 로봇을 실행시키는 주체
 */
public abstract class RobotProxy implements Runnable, IRobotPeer {
    protected EventManager eventManager;
    protected RobotThreadManager robotThreadManager;
    private ThreadManager threadManager;

    private Robot robot;
    protected final RobotPeer peer;

    private final RobotSpecification robotSpec;
    protected RobotClassLoader robotClassLoader;
    protected final RobotStatics statics;

    public RobotProxy(RobotSpecification robotSpecification, RobotPeer robotPeer, RobotStatics statics) {
        this.peer = robotPeer;
        this.statics = statics;
        this.robotSpec = robotSpecification;

        robotClassLoader = createLoader(robotSpecification);
        robotThreadManager = new RobotThreadManager(this);

        loadClassBattle();
    }

    public Robot getRobot() {
        return robot;
    }

    public RobotPeer getRobotPeer() {
        return peer;
    }

    public ClassLoader getRobotClassloader() {
        return robotClassLoader;
    }

    public RobotStatics getStatics() {
        return statics;
    }

    private RobotClassLoader createLoader(RobotSpecification robotSpecification) {
        return new RobotClassLoader(
                robotSpecification.getUsername(),
                robotSpecification.getSpecIndex(),
                robotSpecification.getFullClassName()
        );
    }

    protected abstract void initializeBattle(ExecCommands commands, RobotStatus stat);

    public void startBattle(ExecCommands commands, RobotStatus stat) {
        initializeBattle(commands, stat);
        threadManager = null;
        robotThreadManager.start(threadManager);
    }

    private void loadClassBattle() {
//        robotClassLoader.loadRobotMainClass();
    }

    // 매번 죽고 태어날 때마다 로봇 로드
    private boolean loadRobot() {
        robot = null;
        try {
            // if s3 uploading: (author + specIndex) << 있는지
            String author = robotSpec.getUsername();
            int specIndex = robotSpec.getSpecIndex();
            if (S3Uploader.Instance.isUploading(author, specIndex)) {
                robot = (Robot) S3Uploader.Instance.loadFromLocal(author, specIndex).newInstance();
                System.out.println("load from local " + robot);
            } else {
                robot = robotClassLoader.createRobotInstance();
            }

            if (robot == null) {
                System.out.println("로봇 생성 실패");
                return false;
            }

            robot.setPeer((IBasicRobotPeer) this);
            eventManager.setRobot(robot);
        } catch (Exception e) {
            robot = null;
            return false;
        }
        return true;
    }

    // 로봇 로직 수행
    protected abstract void executeImpl();

    @Override
    public void run() {
        try {
            if (loadRobot() && robot != null) {
                // 로봇 피어 실행 시작
                peer.setRunning(true);

                // 처음 시작할 때의 이벤트 처리
                eventManager.processEvents();

                callUserCode();
            }

            while (peer.isRunning())
                executeImpl();

            peer.setRunning(false);
        } catch (Exception e) {
            System.out.println("thread interrupted? " + Thread.interrupted());
        }
    }

    private void callUserCode() {
        Runnable runnable = robot.getRobotRunnable();
        if (runnable != null)
            runnable.run();
    }

    public void cleanup() {
        robot = null;
        System.out.println("robot proxy cleanup");

        if (robotThreadManager != null) {
            robotThreadManager.cleanup();
            robotThreadManager = null;
        }

        if (robotClassLoader != null) {
            robotClassLoader.cleanup();
            robotClassLoader = null;
        }
    }
}
