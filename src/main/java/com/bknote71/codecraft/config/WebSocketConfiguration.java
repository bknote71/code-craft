package com.bknote71.codecraft.config;

import com.bknote71.codecraft.session.ClientSessionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final ClientSessionHandler sessionHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(sessionHandler, "/battle/*")
                .addInterceptors(new HttpSessionHandshakeInterceptor(), new CustomHandshakeInterceptor())
                .setAllowedOrigins("http://codecraft.world")
        ;
    }
}
