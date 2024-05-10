package com.bknote71.codecraft.web.exception;

public class RobotNotFoundException extends RuntimeException {
    public RobotNotFoundException(String message) {
        super(message);
    }
}