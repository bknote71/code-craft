package com.bknote71.codecraft.robocode.core.battle;

public final class BattleRules {
    private final int battlefieldWidth;
    private final int battlefieldHeight;
    private final double gunCoolingRate;

    public int getBattlefieldWidth() {
        return battlefieldWidth;
    }

    public int getBattlefieldHeight() {
        return battlefieldHeight;
    }

    public double getGunCoolingRate() {
        return gunCoolingRate;
    }

    private BattleRules(int battlefieldWidth, int battlefieldHeight, double gunCoolingRate) {
        this.battlefieldWidth = battlefieldWidth;
        this.battlefieldHeight = battlefieldHeight;
        this.gunCoolingRate = gunCoolingRate;
    }

    static HiddenHelper createHiddenHelper() {
        return new HiddenHelper();
    }

    public static class HiddenHelper {
        public BattleRules createRules(int battlefieldWidth, int battlefieldHeight, double gunCoolingRate) {
            return new BattleRules(battlefieldWidth, battlefieldHeight, gunCoolingRate);
        }

        public static BattleRules createRules(BattleProperties battleProperties) {
            return new BattleRules(1500, 1000, 0.1);
        }

    }
}
