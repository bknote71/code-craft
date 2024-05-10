package com.bknote71.codecraft.proto;

import com.bknote71.codecraft.robocode.core.RobotSpecification;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;


@JsonTypeName("senterbattle")
public class SEnterBattle extends Protocol {

    private int robotId;
    private int specIndex;
    private String robotName;
    private RobotSpecification[] specifications;
    private String username;

    public SEnterBattle() {
        super(ProtocolType.S_EnterBattle);
    }

    public int getRobotId() {
        return robotId;
    }

    public void setRobotId(int robotId) {
        this.robotId = robotId;
    }

    public int getSpecIndex() {
        return specIndex;
    }

    public void setSpecIndex(int specIndex) {
        this.specIndex = specIndex;
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public RobotSpecification[] getSpecifications() {
        return specifications;
    }

    public void setSpecifications(RobotSpecification[] specifications) {
        this.specifications = specifications;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
