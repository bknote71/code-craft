package com.bknote71.codecraft.proto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@JsonTypeName("cchangerobot")
public class CChangeRobot extends Protocol {

    private int specIndex;

    public CChangeRobot() {
        super(ProtocolType.C_ChangeRobot);
    }

    public int getSpecIndex() {
        return specIndex;
    }

    public void setSpecIndex(int specIndex) {
        this.specIndex = specIndex;
    }
}
