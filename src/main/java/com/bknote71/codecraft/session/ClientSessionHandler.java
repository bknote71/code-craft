package com.bknote71.codecraft.session;

import com.bknote71.codecraft.engine.core.RobotManager;
import com.bknote71.codecraft.engine.core.battle.Battle;
import com.bknote71.codecraft.engine.core.RobotPeer;
import com.bknote71.codecraft.session.packet.ServerPacketManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class ClientSessionHandler extends TextWebSocketHandler {
    private final ServerPacketManager serverPacketManager;

    public ClientSessionHandler(ServerPacketManager serverPacketManager) {
        this.serverPacketManager = serverPacketManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        ClientSession generatedSession = ClientSessionManager.Instance.generate(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ClientSession clientSession = ClientSessionManager.Instance.find(session.getId());
        serverPacketManager.handlePacket(clientSession, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("ex? " + exception);
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ClientSession clientSession = ClientSessionManager.Instance.find(session.getId());
        RobotPeer robot = clientSession.getMyRobot();
        if (robot == null)
            return;

        Battle battle = robot.getBattle();
        if (battle == null)
            return;

        battle.push(battle::leaveBattle, robot.getId());
        RobotManager.Instance.remove(robot.getId()); // 임시 <<
        ClientSessionManager.Instance.remove(clientSession);
    }
}
