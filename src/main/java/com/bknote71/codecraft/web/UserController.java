package com.bknote71.codecraft.web;

import com.bknote71.codecraft.entity.service.RobotSpecService;
import com.bknote71.codecraft.entity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Slf4j
@Controller
public class UserController {
    private final UserService userService;
    private final RobotSpecService robotSpecService;

    public UserController(UserService userService, RobotSpecService robotSpecService) {
        this.userService = userService;
        this.robotSpecService = robotSpecService;
    }

    @ResponseBody
    public String getusernmae(@AuthenticationPrincipal(expression = "username") String username) {
        return "this username: " + username;
    }

    @PostMapping("/signup")
    public String signup(String username, String password) {
        Long signup = userService.signup(username, password);

        if (signup == null) {
            System.out.println("회원가입 실패");
            return "redirect:/";
        }
        // default 로봇 제공
        robotSpecService.createDefaultRobot(username);
        userService.login(username, password);
        return "redirect:/lobby";
    }

    @GetMapping("/login")
    public String loginpage() {
        return "signup";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);;
        }

        return "redirect:/login";
    }
}
