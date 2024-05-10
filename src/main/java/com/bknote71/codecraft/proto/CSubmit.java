package com.bknote71.codecraft.proto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@JsonTypeName("csubmit")
@Data
public class CSubmit extends Protocol {
    private String username;
    private Integer robotId;
    private String code;
    private Integer specIndex;

    public CSubmit() {
        super(ProtocolType.C_Submit);
    }
}
