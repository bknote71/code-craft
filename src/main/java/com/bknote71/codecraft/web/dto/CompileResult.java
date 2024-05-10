package com.bknote71.codecraft.web.dto;

import lombok.Data;

@Data
public class CompileResult {
    public int exitCode;
    public String content;
    public String robotname;
    public String fullClassName;
    public String code;
    public String lang;
    public String username;

    public CompileResult(int exitCode, String content) {
        this.exitCode = exitCode;
        this.content = content;
    }

    public CompileResult(int exitCode, String content, String robotname, String fullClassName, String code) {
        this.exitCode = exitCode;
        this.content = content;
        this.robotname = robotname;
        this.fullClassName = fullClassName;
        this.code = code;
    }
}

