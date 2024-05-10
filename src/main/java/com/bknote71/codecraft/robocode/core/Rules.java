package com.bknote71.codecraft.robocode.core;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Rules {
    // Hide the constructor in the Javadocs as it should not be used
    private Rules() {}

    public static final double ACCELERATION = 1;

    public static final double DECELERATION = 2;

    public static final double MAX_VELOCITY = 8;

    public static final double RADAR_SCAN_RADIUS = 700;

    public static final double MIN_BULLET_POWER = 0.1;

    public static final double MAX_BULLET_POWER = 3;

    public static final double MAX_TURN_RATE = 10;

    public static final double MAX_TURN_RATE_RADIANS = Math.toRadians(MAX_TURN_RATE);

    public static final double GUN_TURN_RATE = 20;

    public static final double GUN_TURN_RATE_RADIANS = Math.toRadians(GUN_TURN_RATE);

    public static final double RADAR_TURN_RATE = 45;

    public static final double RADAR_TURN_RATE_RADIANS = Math.toRadians(RADAR_TURN_RATE);

    public static final double ROBOT_HIT_DAMAGE = 0.6;

    public static final double ROBOT_HIT_BONUS = 2 * ROBOT_HIT_DAMAGE;

    public static double getTurnRate(double velocity) {
        return MAX_TURN_RATE - 0.75 * abs(velocity);
    }

    public static double getTurnRateRadians(double velocity) {
        return Math.toRadians(getTurnRate(velocity));
    }

    public static double getWallHitDamage(double velocity) {
        return max(abs(velocity) / 2 - 1, 0);
    }

    public static double getBulletDamage(double bulletPower) {
        double damage = 4 * bulletPower;

        if (bulletPower > 1) {
            damage += 2 * (bulletPower - 1);
        }
        return damage;
    }

    public static double getBulletHitBonus(double bulletPower) {
        return 3 * bulletPower;
    }

    public static double getBulletSpeed(double bulletPower) {
        bulletPower = Math.min(Math.max(bulletPower, MIN_BULLET_POWER), MAX_BULLET_POWER);
        return 20 - 3 * bulletPower;
    }

    public static double getGunHeat(double bulletPower) {
        return 1 + (bulletPower / 5);
    }
}
