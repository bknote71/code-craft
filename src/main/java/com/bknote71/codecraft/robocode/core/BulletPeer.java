package com.bknote71.codecraft.robocode.core;

import com.bknote71.codecraft.robocode.core.battle.Battle;
import com.bknote71.codecraft.robocode.core.battle.BattleRules;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class BulletPeer {

    private static final int EXPLOSION_LENGTH = 17;

    private static final int RADIUS = 20;

    protected final RobotPeer owner;
    private final BattleRules battleRules;
    private final int bulletId;
    private final Battle battle;

    protected BulletState state;

    private double heading;
    protected double x;
    protected double y;
    protected double power;
    private double deltaX;
    private double deltaY;
    private double velocity = 13; // 임시값

    private double lastX;
    private double lastY;

    public BulletPeer(RobotPeer owner, BattleRules battleRules, int bulletId) {
        this.owner = owner;
        this.battleRules = battleRules;
        this.bulletId = bulletId;
        this.battle = owner.getBattle();
    }

    public void update() {
        // 원으로 판단
        if (owner == null) {
            // leave game
            return;
        }

        // 선 이동
        double v = velocity;
        double tx = x + v * sin(heading);
        double ty = y + v * cos(heading);

        // 위치 밖이면 그냥 없애는 걸로 ㄱㄱ <<
        if (tx < 0 || tx >= battleRules.getBattlefieldWidth() || ty < 0 || ty >= battleRules.getBattlefieldHeight()) {
            state = BulletState.INACTIVE;
            return;
        }

        RobotPeer victim;
        if ((victim = battle.getRobotInRange(owner, tx, ty, RADIUS)) == null || victim == owner) {
            // 좌표 갱신 이후 이동 패킷 보내기(SMove)
            x = tx;
            y = ty;
        } else {
            victim.onDamaged(this);
            // 총알 삭제 : inactive
            state = BulletState.INACTIVE;
        }

        // update bullet state

        owner.addBulletStatus(createStatus());
    }

    public int getId() {
        return bulletId;
    }

    public BulletState getState() {
        return state;
    }

    public double getHeading() {
        return heading;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getPower() {
        return power;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setState(BulletState state) {
        this.state = state;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public boolean isActive() {
        return state.isActive();
    }

    private BulletStatus createStatus() {
        return new BulletStatus(bulletId, x, y, null, false);
    }

}
