package com.bknote71.codecraft.config;

import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class ChatGptConfiguration {

    @Value("${gpt.token}")
    private String token;

    @Bean
    public OpenAiService openAiService() {
        log.info("token: {}를 활용한 OpenAiService 를 생성한다.", token);
        return new OpenAiService(token, Duration.ofSeconds(60));
    }

}
