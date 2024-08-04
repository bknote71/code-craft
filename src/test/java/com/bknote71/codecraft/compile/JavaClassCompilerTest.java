package com.bknote71.codecraft.compile;

import com.bknote71.codecraft.entity.UserEntity;
import com.bknote71.codecraft.entity.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JavaClassCompilerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    void timeTest() throws Exception {
        long startTime = System.currentTimeMillis();

        // settings

        // 동일한 CompileRequest
        int specIndex = 0;
        String code =
                "public class Star2Bot extends Robot {\n" +
                        "    @Override\n" +
                        "    public void run() {\n" +
                        "        while (true) {\n" +
                        "            ahead(3000);\n" +
                        "            ahead(1200);\n" +
                        "            turnRight(144);\n" +
                        "        }\n" +
                        "    }\n" +
                        "}";
        String lang = "java";

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 40; i++) {
            // 사용자 세부 정보 로드
            int uIdx = i;
            executorService.submit(() -> {
                try {
                    mockMvc.perform(post("/signup")
                                    .param("username", "user" + uIdx)
                                    .param("password", "password"))
                            .andExpect(status().is3xxRedirection());


                    MvcResult result = mockMvc.perform(post("/login")
                                    .param("username", "user" + uIdx)
                                    .param("password", "password"))
                            .andExpect(status().is3xxRedirection())
                            .andReturn();

                    Cookie sessionCookie = result.getResponse().getCookie("SESSION");
//            System.out.println(session);

                    // 요청 수행 및 검증
                    mockMvc.perform(post("/compile/ingame-robot")
                                    .cookie(sessionCookie)
                                    .content("robotId=" + uIdx + "&specIndex=" + specIndex + "&code=" + code + "&lang=" + lang)
                                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                            .param("robotId", String.valueOf(i))
//                            .param("specIndex", String.valueOf(specIndex))
//                            .param("code", code)
//                            .param("lang", lang))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    // pass
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Total test duration: " + duration + " milliseconds");
    }

}