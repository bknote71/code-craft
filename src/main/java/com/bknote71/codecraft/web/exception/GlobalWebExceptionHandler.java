package com.bknote71.codecraft.web.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalWebExceptionHandler {
    @ExceptionHandler(RobotNotFoundException.class)
    public String handleRobotNotFoundException(RobotNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "lobby"; // 에러 페이지로 이동
    }
}
