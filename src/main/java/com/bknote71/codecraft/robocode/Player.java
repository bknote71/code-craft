package com.bknote71.codecraft.robocode;

import com.bknote71.codecraft.robocode.core.RobotPeer;
import com.bknote71.codecraft.robocode.core.battle.Battle;
import com.bknote71.codecraft.session.ClientSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {

    private int id;
    private String username;

    // session connection
    private ClientSession session;

    // ingame: main robot, sub robot
    private Map<Integer, RobotPeer> robots = new HashMap<>();
    private Battle battle;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ClientSession getSession() {
        return session;
    }

    public ClientSession session() {
        return session;
    }

    public void setSession(ClientSession session) {
        this.session = session;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public List<RobotPeer> getRobots() {
        return new ArrayList<>(robots.values());
    }

    public void addRobot(RobotPeer robotPeer) {
        robotPeer.setPlayer(this);
        robots.put(robotPeer.getId(), robotPeer);
    }

    public void cleanup() {
        for (RobotPeer robotPeer : robots.values()) {
            robotPeer.cleanup();
        }
    }


}
