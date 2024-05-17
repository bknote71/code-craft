package com.bknote71.codecraft.engine.core;

import com.bknote71.codecraft.proto.SDie;
import com.bknote71.codecraft.engine.Player;
import com.bknote71.codecraft.engine.core.battle.Battle;
import com.bknote71.codecraft.engine.core.battle.BattleRules;
import com.bknote71.codecraft.engine.event.*;
import com.bknote71.codecraft.engine.leaderboard.LeaderBoardTemplate;
import com.bknote71.codecraft.engine.proxy.BasicRobotProxy;
import com.bknote71.codecraft.engine.proxy.RobotProxy;
import com.bknote71.codecraft.session.ClientSession;
import com.bknote71.codecraft.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.bknote71.codecraft.util.Utils.normalRelativeAngle;
import static java.lang.Math.*;

public class RobotPeer {

    private static final Logger log = LoggerFactory.getLogger(RobotPeer.class);

    public static final int
            WIDTH = 36,
            HEIGHT = 36;

    private static final int
            HALF_WIDTH_OFFSET = WIDTH / 2,
            HALF_HEIGHT_OFFSET = HEIGHT / 2;

    // session
    private ClientSession session;

    private int id;

    // game room
    private Battle battle;
    private RobotStatistics statistics;
    private RobotSpecification specification;

    // 선택
    private int specIndex;
    private RobotSpecification[] specifications;

    // robot proxy (로봇 로직인 로봇피어와, 커스텀 정보인 로봇을 합쳐서 실행시킬 주체)
    private RobotProxy robotProxy;

    private AtomicReference<RobotStatus> status = new AtomicReference<>();
    private AtomicReference<ExecCommands> commands = new AtomicReference<>();
    private AtomicReference<EventQueue> events = new AtomicReference<>(new EventQueue());
    private AtomicReference<List<BulletStatus>> bulletUpdates = new AtomicReference<List<BulletStatus>>(
            new ArrayList<BulletStatus>());

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private RobotStatics statics; // 로봇의 공통 정보 ??
    private BattleRules battleRules; // 배틀의 규칙

    // 현재 실행중인 명령어 (goAhead, back, turnLeft, turnRight, ...)
    private ExecCommands currentCommands;

    // last direction
    private double lastHeading;
    private double lastGunHeading;
    private double lastRadarHeading;

    // robot status
    private double score;
    private double hp;
    private double energy;
    private double velocity;
    private double bodyHeading; // direction
    private double gunHeading;
    private double radarHeading;

    private double gunHeat; // 총 쏘기까지 남은 시간 (0 이어야 쏠 수 있음)
    private double x; // 현재 좌표
    private double y;

    // scanning ?
    private boolean scan;
    private boolean turnedRadarWithGun;

    private boolean isIORobot;
    private boolean isPaintEnabled;
    private boolean sgPaintEnabled;

    // waiting for next tick
    private final AtomicBoolean isSleeping = new AtomicBoolean(false);
    private final AtomicBoolean halt = new AtomicBoolean(false);

    // last and current execution time and detecting skipped turns
    private int lastExecutionTime = -1;
    private int currentExecutionTime;

    boolean isExecFinishedAndDisabled;
    private boolean inCollision;
    private boolean isOverDriving;
    private boolean isEnergyDrained;

    private RobotState state;

    private Arc2D scanArc; // 스캔 각도
    private BoundingRectangle boundingBox;

    // 로봇 이미지
    private String robotImage;

    public RobotPeer(int id) {
        this.id = id;

        this.boundingBox = new BoundingRectangle();
        this.scanArc = new Arc2D.Double();

        this.state = RobotState.ACTIVE;
    }


    public RobotPeer(int id, Battle battle, RobotSpecification robotSpecification) {
        this.id = id;
        this.battle = battle;
        this.specification = robotSpecification;

        this.boundingBox = new BoundingRectangle();
        this.scanArc = new Arc2D.Double();

        this.state = RobotState.ACTIVE;
        this.battleRules = battle.getBattleRules();

        this.statics = new RobotStatics(robotSpecification, robotSpecification.getName(), "", battleRules);

        this.robotProxy = new BasicRobotProxy(robotSpecification, this, statics);
    }

    public void init(Battle battle, RobotSpecification[] robotSpecifications, int specIndex) {
        this.battle = battle;
        this.specIndex = specIndex;
        this.specification = robotSpecifications[specIndex];
        this.specifications = robotSpecifications;
        this.battleRules = battle.getBattleRules();
        this.statics = new RobotStatics(this.specification, this.specification.getName(), "", battleRules);
        this.robotProxy = new BasicRobotProxy(this.specification, this, statics);
    }

    // session
    public ClientSession session() {
        return session;
    }

    public void setSession(ClientSession session) {
        this.session = session;
    }

    public String getName() {
        return specification.getName();
    }

    public int getId() {
        return id;
    }

    public Battle getBattle() {
        return battle;
    }

    public int getScore() {
        return 0;
    }

    public double getHp() {
        return hp;
    }

    public double getEnergy() {
        return energy;
    }

    private double getVelocity() {
        return velocity;
    }

    public double getBodyHeading() {
        return bodyHeading;
    }

    public double getGunHeading() {
        return gunHeading;
    }

    public double getRadarHeading() {
        return radarHeading;
    }

    public double getGunHeat() {
        return gunHeat;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Arc2D getScanArc() {
        return scanArc;
    }

    public boolean isPaintEnabled() {
        return isPaintEnabled;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public boolean isAlive() {
        return state != RobotState.DEAD;
    }

    public boolean isDead() {
        return state == RobotState.DEAD;
    }

    public boolean isHalt() {
        return halt.get();
    }

    public RobotState getState() {
        return state;
    }

    public int getSpecIndex() {
        return specIndex;
    }

    public RobotSpecification[] getSpecifications() {
        return specifications;
    }

    // username
    public String getUsername() {
        if (specification == null)
            return null;

        return specification.getUsername();
    }


    private void setState(RobotState state) {
        this.state = state;
    }

    public void setRunning(boolean running) {
        isRunning.set(running);
    }

    public BoundingRectangle getBoundingBox() {
        return boundingBox;
    }

    public void setPaintEnabled(boolean enabled) {
        isPaintEnabled = enabled;
    }

    public void setRobotImage(String robotImageName) {
        this.robotImage = robotImageName;
    }

    // execute: 실행해야할 커맨드 세팅
    public final ExecResults executeImpl(ExecCommands newCommands) {
         validateCommands(newCommands);

        if (!isExecFinishedAndDisabled) {
            // robot -> battle
            commands.set(new ExecCommands(newCommands, true));
        } else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (isDead()) {
            isExecFinishedAndDisabled = true;
        }

        waitForUpdate();

        // from battle to robot
        ExecCommands resCommands = new ExecCommands(this.commands.get(), false);
        RobotStatus resStatus = status.get();

        return new ExecResults(
                resCommands,
                resStatus,
                readoutEvents(),
                new ArrayList<>(),
                isHalt(),
                isPaintEnabled()
        );
    }

    private void validateCommands(ExecCommands newCommands) {}

    private List<Event> readoutEvents() {
        return events.getAndSet(new EventQueue());
    }
    private List<BulletStatus> readoutBullets() {
        return bulletUpdates.getAndSet(new ArrayList<>());
    }

    private void waitForUpdate() {
        synchronized (isSleeping) {
            isSleeping.set(true); // 상태변화 -> notifyAll
            isSleeping.notifyAll();
            try {
                isSleeping.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            isSleeping.set(false);
            isSleeping.notifyAll();
        }
    }

    public boolean isSleeping() {
        return isSleeping.get();
    }

    public void waitWakeup() {
        synchronized (isSleeping) {
            if (isSleeping()) {
                isSleeping.notifyAll();
                try {
                    isSleeping.wait(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void waitWakeupNoWait() {
        synchronized (isSleeping) {
            if (isSleeping()) {
                isSleeping.notifyAll();
            }
        }
    }

    public void startBattle() {
        if (battle == null) {
            log.info("start battle but battle is null, so return");
            return;
        }
        initializeBattle();

        ExecCommands newExecCommands = new ExecCommands();
        newExecCommands.copyColors(commands.get());

        currentCommands = newExecCommands;

        RobotStatus stat = new RobotStatus(
                energy,
                x,
                y,
                bodyHeading,
                gunHeading,
                radarHeading,
                velocity,
                currentCommands.getBodyTurnRemaining(),
                currentCommands.getRadarTurnRemaining(),
                currentCommands.getGunTurnRemaining(),
                currentCommands.getDistanceRemaining(),
                gunHeat,
                battle.getTime()
        );
        status.set(stat);

        robotProxy.startBattle(currentCommands, stat);
    }

    private void initializeBattle() {
        Random random = ThreadLocalRandom.current();
        double rndX = random.nextDouble();
        double rndY = random.nextDouble();

        x = RobotPeer.WIDTH + rndX * (battleRules.getBattlefieldWidth() - 2 * RobotPeer.WIDTH);
        y = RobotPeer.HEIGHT + rndY * (battleRules.getBattlefieldHeight() - 2 * RobotPeer.HEIGHT);

        bodyHeading = 2 * Math.PI * random.nextDouble();
        gunHeading = radarHeading = bodyHeading;
        updateBoundingBox();

        setState(RobotState.ACTIVE);

        velocity = 0;
        hp = 5;
        energy = 100;
        gunHeat = 3;
        score = 0;

        isExecFinishedAndDisabled = false;
        scan = false;
        inCollision = false;

        scanArc.setAngleStart(0);
        scanArc.setAngleExtent(0);
        scanArc.setFrame(-100, -100, 1, 1);

        lastExecutionTime = -1;

        status = new AtomicReference<RobotStatus>();

        events = new AtomicReference<>(new EventQueue());
        bulletUpdates = new AtomicReference<>(new ArrayList<>());

        ExecCommands newExecCommands = new ExecCommands();
        commands = new AtomicReference<>(newExecCommands);
    }

    public void performLoadCommands() {
        currentCommands = commands.get();
        fireBullets(currentCommands.getBullets());

        if (currentCommands.isScan())
            scan = true;

        if (currentCommands.isMoved())
            currentCommands.setMoved(false);
    }

    private void fireBullets(List<BulletCommand> bulletCommands) {
        if (bulletCommands == null || bulletCommands.isEmpty()) {
            return;
        }

        BulletPeer newBullet = null;
        for (BulletCommand bulletCommand : bulletCommands) {
            if (gunHeat > 0 || energy == 0) {
                return;
            }

            double firePower = min(energy,
                    min(max(bulletCommand.getPower(), Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

            gunHeat += Rules.getGunHeat(firePower);

            newBullet = new BulletPeer(this, battleRules, bulletCommand.getBulletId());
            newBullet.setPower(firePower);

            if (!turnedRadarWithGun || !bulletCommand.isFireAssistValid())
                newBullet.setHeading(gunHeading); // heading == direction, 총방향
            else
                newBullet.setHeading(bulletCommand.getFireAssistAngle());

            newBullet.setX(x);
            newBullet.setY(y);
        }
        // there is only last bullet in one turn
        if (newBullet != null) {
            log.info("총알 발사 !!!");
            battle.addBullet(newBullet);
        }

        bulletCommands.clear();
    }

    public void performMove(List<RobotPeer> robots) {
        if (isDead())
            return;

        setState(RobotState.ACTIVE);

        // last 정보 (마지막 방향 기록)
        lastHeading = bodyHeading;
        lastGunHeading = gunHeading;
        lastRadarHeading = radarHeading;
        double lastX = x;
        double lastY = y;

        updateGunHeat();
        updateHeading();
        updateGunHeading();
        updateRadarHeading();
        updateMovement();

        // TODO: 벽 충돌 확인
        // checkWallCollision();

        // TODO: 로봇끼리 충돌 확인
        // checkRobotCollision(robots);

        if (!scan) {
            scan = (lastHeading != bodyHeading
                    || lastGunHeading != gunHeading
                    || lastRadarHeading != radarHeading
                    || lastX != x || lastY != y
            );
        }

    }

    public void performScan(List<RobotPeer> robots) {
        if (isDead())
            return;

        turnedRadarWithGun = false;

        if (scan) {
            scan(lastRadarHeading, robots);
            turnedRadarWithGun = (lastGunHeading == lastRadarHeading) && (gunHeading == radarHeading);
            scan = false;
        }

        currentCommands = null;
        lastHeading = -1;
        lastGunHeading = -1;
        lastRadarHeading = -1;
    }

    private void updateBoundingBox() {
        boundingBox.setRect(x - HALF_WIDTH_OFFSET, y - HALF_HEIGHT_OFFSET, WIDTH, HEIGHT);
    }

    public void addEvent(Event event) {
        if (isRunning()) {
            final EventQueue queue = events.get();

            if ((queue.size() > EventManager.MAX_QUEUE_SIZE)
                    && !(event instanceof DeathEvent
                    || event instanceof WinEvent)
            ) {

                queue.clear(battle.getTime() - EventManager.MAX_EVENT_STACK);
            } else {
                queue.add(event);
            }
        }
    }

    private void updateGunHeading() {
        if (currentCommands.getGunTurnRemaining() > 0) {
            if (currentCommands.getGunTurnRemaining() < Rules.GUN_TURN_RATE_RADIANS) {
                gunHeading += currentCommands.getGunTurnRemaining();
                radarHeading += currentCommands.getGunTurnRemaining();
                if (currentCommands.isAdjustRadarForGunTurn()) {
                    currentCommands.setRadarTurnRemaining(
                            currentCommands.getRadarTurnRemaining() - currentCommands.getGunTurnRemaining());
                }
                currentCommands.setGunTurnRemaining(0);
            } else {
                gunHeading += Rules.GUN_TURN_RATE_RADIANS;
                radarHeading += Rules.GUN_TURN_RATE_RADIANS;
                currentCommands.setGunTurnRemaining(currentCommands.getGunTurnRemaining() - Rules.GUN_TURN_RATE_RADIANS);
                if (currentCommands.isAdjustRadarForGunTurn()) {
                    currentCommands.setRadarTurnRemaining(
                            currentCommands.getRadarTurnRemaining() - Rules.GUN_TURN_RATE_RADIANS);
                }
            }
        } else if (currentCommands.getGunTurnRemaining() < 0) {
            if (currentCommands.getGunTurnRemaining() > -Rules.GUN_TURN_RATE_RADIANS) {
                gunHeading += currentCommands.getGunTurnRemaining();
                radarHeading += currentCommands.getGunTurnRemaining();
                if (currentCommands.isAdjustRadarForGunTurn()) {
                    currentCommands.setRadarTurnRemaining(
                            currentCommands.getRadarTurnRemaining() - currentCommands.getGunTurnRemaining());
                }
                currentCommands.setGunTurnRemaining(0);
            } else {
                gunHeading -= Rules.GUN_TURN_RATE_RADIANS;
                radarHeading -= Rules.GUN_TURN_RATE_RADIANS;
                currentCommands.setGunTurnRemaining(currentCommands.getGunTurnRemaining() + Rules.GUN_TURN_RATE_RADIANS);
                if (currentCommands.isAdjustRadarForGunTurn()) {
                    currentCommands.setRadarTurnRemaining(
                            currentCommands.getRadarTurnRemaining() + Rules.GUN_TURN_RATE_RADIANS);
                }
            }
        }
        gunHeading = Utils.normalAbsoluteAngle(gunHeading);
    }

    private void updateHeading() {
        boolean normalizeHeading = true;

        double turnRate = min(currentCommands.getMaxTurnRate(),
                (.4 + .6 * (1 - (abs(velocity) / Rules.MAX_VELOCITY))) * Rules.MAX_TURN_RATE_RADIANS);

        if (currentCommands.getBodyTurnRemaining() > 0) {
            if (currentCommands.getBodyTurnRemaining() < turnRate) {
                bodyHeading += currentCommands.getBodyTurnRemaining();
                gunHeading += currentCommands.getBodyTurnRemaining();
                radarHeading += currentCommands.getBodyTurnRemaining();
                if (currentCommands.isAdjustGunForBodyTurn()) {
                    currentCommands.setGunTurnRemaining(
                            currentCommands.getGunTurnRemaining() - currentCommands.getBodyTurnRemaining());
                }
                if (currentCommands.isAdjustRadarForBodyTurn()) {
                    currentCommands.setRadarTurnRemaining(
                            currentCommands.getRadarTurnRemaining() - currentCommands.getBodyTurnRemaining());
                }
                currentCommands.setBodyTurnRemaining(0);
            } else {
                bodyHeading += turnRate;
                gunHeading += turnRate;
                radarHeading += turnRate;
                currentCommands.setBodyTurnRemaining(currentCommands.getBodyTurnRemaining() - turnRate);
                if (currentCommands.isAdjustGunForBodyTurn()) {
                    currentCommands.setGunTurnRemaining(currentCommands.getGunTurnRemaining() - turnRate);
                }
                if (currentCommands.isAdjustRadarForBodyTurn()) {
                    currentCommands.setRadarTurnRemaining(currentCommands.getRadarTurnRemaining() - turnRate);
                }
            }
        } else if (currentCommands.getBodyTurnRemaining() < 0) {
            if (currentCommands.getBodyTurnRemaining() > -turnRate) {
                bodyHeading += currentCommands.getBodyTurnRemaining();
                gunHeading += currentCommands.getBodyTurnRemaining();
                radarHeading += currentCommands.getBodyTurnRemaining();
                if (currentCommands.isAdjustGunForBodyTurn()) {
                    currentCommands.setGunTurnRemaining(
                            currentCommands.getGunTurnRemaining() - currentCommands.getBodyTurnRemaining());
                }
                if (currentCommands.isAdjustRadarForBodyTurn()) {
                    currentCommands.setRadarTurnRemaining(
                            currentCommands.getRadarTurnRemaining() - currentCommands.getBodyTurnRemaining());
                }
                currentCommands.setBodyTurnRemaining(0);
            } else {
                bodyHeading -= turnRate;
                gunHeading -= turnRate;
                radarHeading -= turnRate;
                currentCommands.setBodyTurnRemaining(currentCommands.getBodyTurnRemaining() + turnRate);
                if (currentCommands.isAdjustGunForBodyTurn()) {
                    currentCommands.setGunTurnRemaining(currentCommands.getGunTurnRemaining() + turnRate);
                }
                if (currentCommands.isAdjustRadarForBodyTurn()) {
                    currentCommands.setRadarTurnRemaining(currentCommands.getRadarTurnRemaining() + turnRate);
                }
            }
        } else {
            normalizeHeading = false;
        }

        if (normalizeHeading) {
            if (currentCommands.getBodyTurnRemaining() == 0) {
                bodyHeading = Utils.normalNearAbsoluteAngle(bodyHeading);
            } else {
                bodyHeading = Utils.normalAbsoluteAngle(bodyHeading);
            }
        }
    }

    private void updateRadarHeading() {
        if (currentCommands.getRadarTurnRemaining() > 0) {
            if (currentCommands.getRadarTurnRemaining() < Rules.RADAR_TURN_RATE_RADIANS) {
                radarHeading += currentCommands.getRadarTurnRemaining();
                currentCommands.setRadarTurnRemaining(0);
            } else {
                radarHeading += Rules.RADAR_TURN_RATE_RADIANS;
                currentCommands.setRadarTurnRemaining(
                        currentCommands.getRadarTurnRemaining() - Rules.RADAR_TURN_RATE_RADIANS);
            }
        } else if (currentCommands.getRadarTurnRemaining() < 0) {
            if (currentCommands.getRadarTurnRemaining() > -Rules.RADAR_TURN_RATE_RADIANS) {
                radarHeading += currentCommands.getRadarTurnRemaining();
                currentCommands.setRadarTurnRemaining(0);
            } else {
                radarHeading -= Rules.RADAR_TURN_RATE_RADIANS;
                currentCommands.setRadarTurnRemaining(
                        currentCommands.getRadarTurnRemaining() + Rules.RADAR_TURN_RATE_RADIANS);
            }
        }

        radarHeading = Utils.normalAbsoluteAngle(radarHeading);
    }

    private void updateMovement() {
        // ahead: 해당하는 거리만큼 가라.
        // 그러면 남은 거리만큼 이동해야 하고, 남은거리가 없으면 속도가 0이되게 해야함 ??
        double distance = currentCommands.getDistanceRemaining();

        // 일단 해보고 안되면 그냥 일정하게 가자.
        velocity = getNewVelocity(velocity, distance);

        if (Utils.isNear(velocity, 0) && isOverDriving) {
            currentCommands.setDistanceRemaining(0);
            distance = 0;
            isOverDriving = false;
        }
//
//        // If we are moving normally and the breaking distance is more
//        // than remaining distance, enabled the overdrive flag.
        if (Math.signum(distance * velocity) != -1) {
            isOverDriving = getDistanceTraveledUntilStop(velocity) > Math.abs(distance);
        }

        // velocity 만큼 이동
        currentCommands.setDistanceRemaining(distance - velocity);

        if (velocity != 0) {
            x += velocity * sin(bodyHeading);
            y += velocity * cos(bodyHeading);

            // 위치 보정
            int battlefieldWidth = battleRules.getBattlefieldWidth();
            int battlefieldHeight = battleRules.getBattlefieldHeight();
            x = (x + battlefieldWidth) % battlefieldWidth;
            y = (y + battlefieldHeight) % battlefieldHeight;

            updateBoundingBox();
        }
    }


    private void checkRobotCollision(List<RobotPeer> robots) {
        inCollision = false;

        for (RobotPeer otherRobot : robots) {
            if (!(otherRobot == null || otherRobot == this || otherRobot.isDead())
                    && boundingBox.intersects(otherRobot.boundingBox)) {
                System.out.println("다른 로봇과의 충돌");
                // Bounce back
                double angle = atan2(otherRobot.x - x, otherRobot.y - y);

                double movedx = velocity * sin(bodyHeading);
                double movedy = velocity * cos(bodyHeading);

                boolean atFault;
                double bearing = normalRelativeAngle(angle - bodyHeading);

                if ((velocity > 0 && bearing > -PI / 2 && bearing < PI / 2)
                        || (velocity < 0 && (bearing < -PI / 2 || bearing > PI / 2))) {

                    inCollision = true;
                    atFault = true;
                    velocity = 0;
                    currentCommands.setDistanceRemaining(0);
                    x -= movedx;
                    y -= movedy;

                    this.updateEnergy(-Rules.ROBOT_HIT_DAMAGE);
                    otherRobot.updateEnergy(-Rules.ROBOT_HIT_DAMAGE);

                    if (otherRobot.energy == 0) {
                        if (otherRobot.isAlive()) {
                            otherRobot.onDead();
                        }
                    }
                    addEvent(
                            new HitRobotEvent(getNameForEvent(otherRobot), normalRelativeAngle(angle - bodyHeading),
                                    otherRobot.energy, atFault));
                    otherRobot.addEvent(
                            new HitRobotEvent(getNameForEvent(this),
                                    normalRelativeAngle(PI + angle - otherRobot.getBodyHeading()), energy, false));
                }
            }
        }
        if (inCollision) {
            setState(RobotState.HIT_ROBOT);
        }
    }

    public void updateAfterCollision() {
        if (state == RobotState.HIT_ROBOT) {
            updateBoundingBox();
        }
    }

    private double getDistanceTraveledUntilStop(double velocity) {
        double distance = 0;

        velocity = Math.abs(velocity);
        while (velocity > 0) {
            distance += (velocity = getNewVelocity(velocity, 0));
        }
        return distance;
    }

    private double getNewVelocity(double velocity, double distance) {
        if (distance < 0) {
            // If the distance is negative, then change it to be positive
            // and change the sign of the input velocity and the result
            return -getNewVelocity(-velocity, -distance);
        }

        final double goalVel;

        if (distance == Double.POSITIVE_INFINITY) {
            goalVel = currentCommands.getMaxVelocity();
        } else {
            goalVel = min(getMaxVelocity(distance), currentCommands.getMaxVelocity());
        }

        if (velocity >= 0) {
            return max(velocity - Rules.DECELERATION, min(goalVel, velocity + Rules.ACCELERATION));
        }

        return max(velocity - Rules.ACCELERATION, Math.min(goalVel, velocity + maxDecel(-velocity)));
    }

    private final static double getMaxVelocity(double distance) {
        final double decelTime = Math.max(1, Math.ceil(// sum of 0... decelTime, solving for decelTime using quadratic formula
                (Math.sqrt((4 * 2 / Rules.DECELERATION) * distance + 1) - 1) / 2));

        if (decelTime == Double.POSITIVE_INFINITY) {
            return Rules.MAX_VELOCITY;
        }

        final double decelDist = (decelTime / 2.0) * (decelTime - 1) // sum of 0..(decelTime-1)
                * Rules.DECELERATION;

        return ((decelTime - 1) * Rules.DECELERATION) + ((distance - decelDist) / decelTime);
    }

    private static double maxDecel(double speed) {
        double decelTime = speed / Rules.DECELERATION;
        double accelTime = (1 - decelTime);

        return Math.min(1, decelTime) * Rules.DECELERATION + Math.max(0, accelTime) * Rules.ACCELERATION;
    }

    private void updateGunHeat() { // 쿨 타임 적용, 1 초에 30 번 호출되니, 그만큼 비율을 조정할 필요가 있음
        gunHeat -= battleRules.getGunCoolingRate();
        if (gunHeat < 0)
            gunHeat = 0;
    }

    private void scan(double lastRadarHeading, List<RobotPeer> robots) {
        double startAngle = lastRadarHeading;
        double scanRadians = getRadarHeading() - startAngle;

        // Check if we passed through 360
        if (scanRadians < -PI)
            scanRadians = 2 * PI + scanRadians;
        else if (scanRadians > PI)
            scanRadians = scanRadians - 2 * PI;

        startAngle -= PI / 2; // 시계 방향으로 돌게하기 위함이라고 한다.
        startAngle = Utils.normalAbsoluteAngle(startAngle); // 양수화

        // 그릴 arc
        scanArc.setArc(x - Rules.RADAR_SCAN_RADIUS, y - Rules.RADAR_SCAN_RADIUS, 2 * Rules.RADAR_SCAN_RADIUS,
                2 * Rules.RADAR_SCAN_RADIUS, 180.0 * startAngle / PI, 180.0 * scanRadians / PI, Arc2D.PIE);

        for (RobotPeer otherRobot : robots) {
            if (!(otherRobot == null || otherRobot == this || otherRobot.isDead())
                    && intersects(scanArc, otherRobot.boundingBox)) {
                double dx = otherRobot.x - x;
                double dy = otherRobot.y - y;
                double angle = atan2(dx, dy);
                double dist = Math.hypot(dx, dy);
                ScannedRobotEvent event = new ScannedRobotEvent(getNameForEvent(otherRobot), otherRobot.energy,
                        normalRelativeAngle(angle - getBodyHeading()), dist, otherRobot.getBodyHeading(),
                        otherRobot.getVelocity());

                addEvent(event);
            }
        }
    }

    private boolean intersects(Arc2D arc, Rectangle2D rect) {
        return (rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
                arc.getStartPoint().getY()))
                || arc.intersects(rect);
    }

    public String getNameForEvent(RobotPeer otherRobot) {
        return otherRobot.getName();
    }

    // 상태 변화
    public void publishStatus(long currentTime) {
        ExecCommands currentCommands = commands.get();

        RobotStatus stat = new RobotStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity,
                currentCommands.getBodyTurnRemaining(), currentCommands.getRadarTurnRemaining(),
                currentCommands.getGunTurnRemaining(), currentCommands.getDistanceRemaining(), gunHeat, battle.getTime());

        status.set(stat);
    }

    // onXXX
    public void onDamaged(BulletPeer bulletPeer) {
        if (battle == null)
            return;

        this.hp = max(hp - bulletPeer.getPower(), 0);

        if (hp <= 0) {
            RobotPeer owner = bulletPeer.owner;
            // compensate
            owner.compensate(3, 2);
            onDead();
            return;
        }

        addEvent(new HitByBulletEvent(bulletPeer.getHeading() + PI - bodyHeading, bulletPeer));
        bulletPeer.owner.addEvent(
                new BulletHitEvent(bulletPeer.owner.getNameForEvent(this), energy, bulletPeer));
    }

    public void onDead() { // 죽음 ?? 리스폰 해야함
        if (battle == null)
            return;

        LeaderBoardTemplate.resetLeaderboardScore(battle.getId(), getUsername());
        LeaderBoardTemplate.updateTodayLeaderBoard(getUsername(), score);

        // 죽음 처리 작업 푸쉬
        if (isAlive()) {
            // addEvent(new DeathEvent());
            // battle.registerDeathRobot(this);
        }
        // updateEnergy(-energy);
        setState(RobotState.DEAD); // 사실 이것도 필요 없음

        // die 패킷
        SDie diePacket = new SDie();
        diePacket.setId(id);
        diePacket.setX(x);
        diePacket.setY(y);
        battle.broadcast(diePacket);

        // 다시 배틀 시작
        RobotPeer robotPeer = this;
        battle.pushAfter(1000, robotPeer::startBattle);
    }

    private void compensate(double hp, double score) {
        if (battle == null)
            return;

        String username = getUsername();

        log.info("compensate for {} ({}, {}) + ({}, {})", username, this.hp, this.score, hp, score);

        this.hp = min(this.hp + hp, 5);
        this.score += score;
        LeaderBoardTemplate.incrementLeaderboardScore(battle.getId(), username, score);
    }

    public void setDead() {
        setState(RobotState.DEAD);
    }

    void updateEnergy(double delta) {
        if ((!isExecFinishedAndDisabled && !isEnergyDrained) || delta < 0) {
            setEnergy(energy + delta, true);
        }
    }

    private void setEnergy(double newEnergy, boolean resetInactiveTurnCount) {
//        if (resetInactiveTurnCount && (energy != newEnergy)) {
//            battle.resetInactiveTurnCount(energy - newEnergy);
//        }
        energy = newEnergy;
        if (energy < .01) {
            energy = 0;
            ExecCommands localCommands = commands.get();

            localCommands.setDistanceRemaining(0);
            localCommands.setBodyTurnRemaining(0);
        }
    }

    public void cleanup() {
        battle = null;

        if (robotProxy != null) {
            robotProxy.cleanup();
            robotProxy = null;
        }

        if (statistics != null) {
            statistics.cleanup();
            statistics = null;
        }

        status = null;
        commands = null;
        events = null;
        bulletUpdates = null;
        statics = null;
        battleRules = null;
    }

    void addBulletStatus(BulletStatus bulletStatus) {
        if (isAlive()) {
            if (bulletUpdates == null)
                return;
            bulletUpdates.get().add(bulletStatus);
        }
    }

    // player 로직 (임시로)
    private Player player;

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
