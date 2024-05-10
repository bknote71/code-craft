package com.bknote71.codecraft.proto;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CEnterBattle.class, name = "centerbattle"),
        @JsonSubTypes.Type(value = CChat.class, name = "cchat"),
        @JsonSubTypes.Type(value = CChangeRobot.class, name = "cchangerobot"),
})
public class Protocol {

    private ProtocolType protocol;

    public Protocol() {
    }

    public Protocol(ProtocolType protocol) {
        this.protocol = protocol;
    }

    public Protocol(String protocol) {
        this.protocol = Enum.valueOf(ProtocolType.class, protocol);
    }

    public short protocol() {
        return (short) protocol.ordinal();
    }

    public void setProtocol(ProtocolType protocol) {
        this.protocol = protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = Enum.valueOf(ProtocolType.class, protocol);
    }
}
