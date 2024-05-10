package com.bknote71.codecraft.robocode.core;

import com.bknote71.codecraft.robocode.core.battle.BattleRules;

public final class RobotStatics {
    private final boolean isPaintRobot;

    private final String name;

    private final String fullClassName;
    private final String shortClassName;

    private final BattleRules battleRules;

    public RobotStatics(RobotSpecification robotSpecification, String name, String suffix, BattleRules rules) {
        RobotSpecification specification = robotSpecification;

        this.shortClassName = specification.getShortClassName();
        this.fullClassName = specification.getFullClassName();
        this.name = name;

         this.isPaintRobot = specification.isPaintRobot();
        this.battleRules = rules;
    }

    public boolean isPaintRobot() {
        return isPaintRobot;
    }

    public String getName() {
        return name;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public BattleRules getBattleRules() {
        return battleRules;
    }
}
