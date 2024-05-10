package com.bknote71.codecraft.session.packet;

import com.bknote71.codecraft.proto.*;
import com.bknote71.codecraft.session.ClientSession;
import com.bknote71.codecraft.util.PacketTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@Component
public class ServerPacketManager {
    private final PacketHandler packetHandler;
    private final Map<ProtocolType, BiConsumer<ClientSession, Protocol>> handlers = new ConcurrentHashMap<>();

    @Autowired
    public ServerPacketManager(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
        register();
    }

    public void register() {
        handlers.put(ProtocolType.C_EnterBattle, packetHandler::CEnterBattleHandler);
        handlers.put(ProtocolType.C_ChangeRobot, packetHandler::CChangeRobotHandler);
        handlers.put(ProtocolType.C_Chat, packetHandler::CChatHandler);
    }

    public void handlePacket(ClientSession session, TextMessage message) {
        ProtocolType protocolType = PacketTranslator.protocol(message.getPayload());
        BiConsumer<ClientSession, Protocol> handler = handlers.get(protocolType);

        if (handler == null) {
            return; // throw
        }

        handler.accept(session, PacketTranslator.object(message.getPayload()));
    }
}
