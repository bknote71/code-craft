package com.bknote71.codecraft.robocode;

import com.bknote71.codecraft.robocode.core.RobotPeer;
import com.bknote71.codecraft.robocode.core.battle.Battle;
import com.bknote71.codecraft.robocode.core.battle.BattleProperties;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    public static PlayerManager Instance = new PlayerManager();

    private int id;
    private Map<Integer, Player> players = new HashMap<>();
    private Object lock = new Object();

    public Player add() {
        // 새로운 배틀 생성
        Player newPlayer = new Player();
        synchronized (lock) {
            newPlayer.setId(++id);
            players.put(id, newPlayer);
        }
        return newPlayer;
    }

    public Player find(Integer playerId) {
        Player player = null;
        synchronized (lock) {
            player = players.get(playerId);
        }
        return player;
    }

    public void remove(int playerId) {
        synchronized (lock) {
            Player player = players.remove(playerId);
            if (player == null)
                return;

            player.cleanup();
        }
    }
}
