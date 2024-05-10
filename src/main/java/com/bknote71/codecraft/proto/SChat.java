package com.bknote71.codecraft.proto;


import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@JsonTypeName("schat")
public class SChat extends Protocol {

    private int robotId; // sender
    private String content;

    public SChat() {
        super(ProtocolType.S_Chat);
    }

    public int getRobotId() {
        return robotId;
    }

    public void setRobotId(int robotId) {
        this.robotId = robotId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
