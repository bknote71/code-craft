package com.bknote71.codecraft.web.dto;

public class CompileRequest {
    int robotId;
    int specIndex;
    String code;
    String lang;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
