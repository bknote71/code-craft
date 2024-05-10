package com.bknote71.codecraft.proto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@JsonTypeName("centerbattle")
@Data
public class CEnterBattle extends Protocol {
    // private String username;
    private int specIndex;

    public CEnterBattle() {
        super(ProtocolType.C_EnterBattle);
    }
}
