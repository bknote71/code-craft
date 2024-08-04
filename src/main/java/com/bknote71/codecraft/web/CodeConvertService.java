package com.bknote71.codecraft.web;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
//@Service
public class CodeConvertService {
    private final OpenAiService openAiService;

//    @Value("${gpt.model}")
    private String gpt_model;

    public CodeConvertService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public String convertLangToJava(String lang, String code) {
        String systemcmd = String.format(
                "이제부터 %s 코드를 \"public 자바 class 코드\"로 변환할거야. " +
                "Robot 이라는 클래스를 상속하고 있고 해당 클래스는 정의하지 않아도 괜찮아. " +
                "이때 자바코드 이외의 설명은 필요 없어. 마크다운하지마" +
                "public class부분부터 소스코드만 보여줘" +
                "그리고 틀린 부분이 있어도 고치지 않고 자바코드로 변환해줘줘",
                lang);
        return convert(systemcmd, code);
    }

    private String convert(String systemcmd, String code) {
        ChatMessage m1 = new ChatMessage("system", systemcmd);
        ChatMessage m2 = new ChatMessage("user", code);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .messages(Arrays.asList(m1, m2))
                .model(gpt_model)
                .build();

        ChatCompletionResult chatCompletionResult = openAiService.createChatCompletion(chatCompletionRequest);
        List<ChatCompletionChoice> choices = chatCompletionResult.getChoices();

        if (choices == null || choices.isEmpty()) {
            return null;
        }

        ChatCompletionChoice chatCompletionChoice = choices.get(0);
        ChatMessage message = chatCompletionChoice.getMessage();
        String text = message.getContent();
        return text;
    }
}
