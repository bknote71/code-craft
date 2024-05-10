package com.bknote71.codecraft.web;

import com.bknote71.codecraft.compile.JavaClassCompiler;
import com.bknote71.codecraft.entity.service.RobotSpecService;
import com.bknote71.codecraft.web.dto.CompileRequest;
import com.bknote71.codecraft.web.dto.CompileResult;
import com.bknote71.codecraft.web.dto.RobotSpecDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class HomeController {
    private final RobotSpecService robotSpecService;
    private final CodeConvertService convertJavaCode;
    private final JavaClassCompiler javaClassCompiler;

    public HomeController(RobotSpecService robotSpecService,
                          CodeConvertService convertJavaCode,
                          JavaClassCompiler javaClassCompiler
    ) {
        this.robotSpecService = robotSpecService;
        this.convertJavaCode = convertJavaCode;
        this.javaClassCompiler = javaClassCompiler;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/lobby")
    public String lobby(@AuthenticationPrincipal(expression = "username") String username,
                        Model model) {
        // 모델
        List<RobotSpecDto> robotInfos = robotSpecService.getRobotInfo(username);
        model.addAttribute("robotInfos", robotInfos);
        return "lobby";
    }

    @GetMapping("/ingame")
    public String ingame(@AuthenticationPrincipal(expression = "username") String username,
                         Model model,
                         int specIndex) {
        List<RobotSpecDto> robotInfos = robotSpecService.getRobotInfo(username);

        model.addAttribute("robotInfos", robotInfos);
        model.addAttribute("specIndex", specIndex < 1 ? 0 : specIndex - 1);
        return "ingame";
    }

    @PostMapping("/compile/ingame-robot")
    @ResponseBody
    public ResponseEntity<?> compileRobot(@AuthenticationPrincipal(expression = "username") String username,
                                          CompileRequest compileRequest) { // author == username
        Boolean compareResult = robotSpecService.compareWithRequestCode(username, compileRequest);
        if (compareResult == null) {
            return null;
        }

        if (compareResult == true) {
            return new ResponseEntity<>(new CompileResult(0, "same code"), HttpStatus.OK);
        }

        String javaCode = compileRequest.getCode();
        String lang = compileRequest.getLang();
        if ((lang != null && !lang.isEmpty() && !lang.isBlank() && !lang.equals("undefined"))
                && !lang.equals("java")) {
            assert !lang.equals("java");
            javaCode = convertJavaCode.convertLangToJava(compileRequest.getLang(), compileRequest.getCode());
        }

        CompileResult result = javaClassCompiler.createRobot(username, javaCode, compileRequest.getSpecIndex());
        RobotSpecDto changeResult;
        if (result.exitCode == 0) {
            changeResult = robotSpecService.changeRobotSpec(
                    username,
                    compileRequest,
                    result.robotname,
                    result.fullClassName
            );

            if (changeResult == null) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        result.username = username;
        result.lang = lang;

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/convert-check")
    @ResponseBody
    public String convertCheck(String lang, String code) {
        return convertJavaCode.convertLangToJava(lang, code);
    }
}
