package com.bknote71.codecraft.session;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSessionManager {
    Map<String, ClientSession> sessions = new ConcurrentHashMap<>();
    Object lock = new Object();

    public static ClientSessionManager Instance = new ClientSessionManager();

    public ClientSession generate(WebSocketSession session) {
        String sessionId = session.getId();
        ClientSession clientSession = new ClientSession(sessionId, session);
        sessions.put(sessionId, clientSession);
        return clientSession;
    }

    public ClientSession find(String id) {
        return sessions.get(id);
    }

    public void remove(ClientSession session) {
       sessions.remove(session.getSessionId());
    }
}

