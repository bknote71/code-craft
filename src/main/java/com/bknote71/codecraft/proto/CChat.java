package com.bknote71.codecraft.proto;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("cchat")
public class CChat extends Protocol {
    private String content;

    public CChat() {
        super(ProtocolType.C_Chat);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
