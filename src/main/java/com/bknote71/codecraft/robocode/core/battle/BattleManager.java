package com.bknote71.codecraft.robocode.core.battle;

import java.util.HashMap;
import java.util.Map;

public class BattleManager {

    public static BattleManager Instance = new BattleManager();

    private BattleProperties battleProperties;

    private Object lock = new Object();
    private Map<Integer, Battle> battles = new HashMap<>();
    private int battleId;

    public BattleManager() {
        battleProperties = new BattleProperties();
    }

    public Battle startNewBattle() {
        // 새로운 배틀 생성
        Battle newBattle = new Battle();
        synchronized (lock) {
            newBattle.setBattleId(++battleId);
            newBattle.setup(battleProperties);
            battles.put(battleId, newBattle);
        }
        return newBattle;
    }

    public Battle find(Integer battleId) {
        Battle battle = null;
        synchronized (lock) {
            battle = battles.get(battleId);
        }
        return battle;
    }
}
