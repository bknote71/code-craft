package com.bknote71.codecraft.proto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class UpdateInfo {
    public long t;
    public List<RobotInfo> robots = new ArrayList<>();
    public List<BulletInfo> bullets = new ArrayList<>();
    public List<ScanInfo> scans = new ArrayList<>();

    @Data
    public static class RobotInfo {
        int id;
        String name; // 로봇 이름
        String username;
        double x;
        double y;
        double bodyHeading;
        double gunHeading;
        double radarHeading;
        double hp;
        boolean dead;

        public RobotInfo(int id, String name, String username, double x, double y,
                         double bodyHeading, double gunHeading, double radarHeading, double hp, boolean dead) {
            this.id = id;
            this.name = name;
            this.username = username;
            this.x = x;
            this.y = y;
            this.bodyHeading = bodyHeading;
            this.gunHeading = gunHeading;
            this.radarHeading = radarHeading;
            this.hp = hp;
            this.dead = dead;
        }
    }

    @Data
    public static class BulletInfo {
        int id;
        double x;
        double y;

        public BulletInfo(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    @Data
    public static class ScanInfo {
        // 시작 위치
        int id;
        String name;
        double robotX;
        double robotY;
        double angleStart;
        double angleExtent;
        double x;
        double y;
        double width;
        double height;

        public ScanInfo(int id, String name, double robotX, double robotY,
                        double angleStart, double angleExtent, double x, double y, double width, double height) {
            this.id = id;
            this.name = name;
            this.robotX = robotX;
            this.robotY = robotY;
            this.angleStart = angleStart;
            this.angleExtent = angleExtent;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

}
