package com.bknote71.codecraft.robocode.core;

public class BulletIdGenerator {
    public static final BulletIdGenerator Instance = new BulletIdGenerator();

    private int id = 0;
    private Object lock = new Object();

    public int getId() {
        synchronized (lock) {
            return ++id;
        }
    }
}
