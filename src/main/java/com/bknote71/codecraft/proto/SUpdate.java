package com.bknote71.codecraft.proto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@JsonTypeName("supdate")
public class SUpdate extends Protocol {
    private UpdateInfo update;

    public SUpdate() {
        super(ProtocolType.S_Update);
    }

    public UpdateInfo getUpdate() {
        return update;
    }

    public void setUpdate(UpdateInfo update) {
        this.update = update;
    }
}
