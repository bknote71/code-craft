package com.bknote71.codecraft.session.packet;

import com.bknote71.codecraft.entity.RobotSpecEntity;
import com.bknote71.codecraft.entity.UserEntity;
import com.bknote71.codecraft.proto.CChangeRobot;
import com.bknote71.codecraft.proto.CChat;
import com.bknote71.codecraft.proto.CEnterBattle;
import com.bknote71.codecraft.proto.Protocol;
import com.bknote71.codecraft.entity.repository.UserRepository;
import com.bknote71.codecraft.engine.core.RobotManager;
import com.bknote71.codecraft.engine.core.RobotPeer;
import com.bknote71.codecraft.engine.core.battle.Battle;
import com.bknote71.codecraft.engine.core.battle.BattleManager;
import com.bknote71.codecraft.engine.core.RobotSpecification;
import com.bknote71.codecraft.session.ClientSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class PacketHandler {
    private final UserRepository userRepository;

    public void CEnterBattleHandler(ClientSession clientSession, Protocol protocol) {
        CEnterBattle enterPacket = (CEnterBattle) protocol;

        // 로봇 생성 (음.. 어떻게 하지?)
        // 로봇피어를 생성하기 전에 specification 을 읽어와야 한다.
        String username = clientSession.getUsername();
        // username = username.equals("anon") ? enterPacket.getUsername() : username;

        log.info("{} is entered", username);

        // robot index = 0
        RobotPeer robot = createRobotPeer(clientSession, username, enterPacket.getSpecIndex());
        if (robot == null) {
            log.error("로봇 생성 실패");
            return;
        }

        Battle battle = robot.getBattle();
        if (battle == null) {
            log.error("배틀에 들어가지 못했습니다.");
            return;
        }

        battle.push(battle::enterBattle, robot);
    }

    public void CChangeRobotHandler(ClientSession clientSession, Protocol protocol) {
        CChangeRobot changePacket = (CChangeRobot) protocol;
        RobotPeer myRobot = clientSession.getMyRobot();
        if (myRobot == null) {
            log.error("my robot is null");
            return;
        }
        changeAndReenter(myRobot.getId(), clientSession.getUsername(), changePacket.getSpecIndex());
    }


    public void CChatHandler(ClientSession session, Protocol protocol) {
        CChat chatPacket = (CChat) protocol;
        RobotPeer robot = session.getMyRobot();

        if (robot == null) {
            log.error("robot이 없어 채팅을 칠 수 없습니다.");
            return;
        }

        Battle battle = robot.getBattle();
        if (battle == null) {
            log.error("battle이 없어 채팅을 칠 수 없습니다.");
            return;
        }

        battle.push(battle::handleChat, robot, chatPacket.getContent());
    }

    public RobotSpecification changeAndReenter(int robotId, String username, int robotIndex) {
        // 새로운 스펙으로 새로운 로봇 생성
        log.info("{} {} {}", robotId, username, robotIndex);
        RobotPeer robot = RobotManager.Instance.find(robotId);
        RobotSpecification[] robotSpecifications = getRobotSpecifications(username);

        if (robot == null) {
            System.out.println("로봇 생성 실패");
            return null;
        }

        Battle battle = robot.getBattle();
        if (battle == null) {
            System.out.println("배틀에 들어가지 못했습니다.");
            return null;
        }

        battle.push(battle::changeRobot, robot.getId(), robotSpecifications, robotIndex);
        return robotSpecifications[robotIndex];
    }

    private Integer getBattleId(WebSocketSession session) {
        String[] paths = session.getUri().getPath().split("/");
        // path: /battle/{battleId}
        Integer roomId = Integer.valueOf(paths[2]);
        return roomId;
    }

    private RobotPeer createRobotPeer(ClientSession session, String username, int robotIndex) {
        RobotSpecification[] robotSpecifications = getRobotSpecifications(username);

        RobotPeer robotPeer = RobotManager.Instance.create();
        robotPeer.setSession(session);
        session.setMyRobot(robotPeer);

        Integer battleId = getBattleId(session.getWebSocketSession());
        Battle battle = BattleManager.Instance.find(battleId);
        if (battle == null) {
            System.out.println("battle 을 찾지 못하였습니다.");
            robotPeer = null;
            return null;
        }

        log.info("init start");
        robotPeer.init(battle, robotSpecifications, robotIndex);
        log.info("init end");

        return robotPeer;
    }

    private RobotSpecification[] getRobotSpecifications(String username) {
        UserEntity user = userRepository.findByUsername(username);

        int specCount = user.getSpecifications().size();
        RobotSpecification[] robotSpecifications = new RobotSpecification[specCount];
        for (int i = 0; i < specCount; ++i) {
            RobotSpecEntity spec = user.getSpecifications().get(i);
            robotSpecifications[i] = new RobotSpecification(spec.getName(), spec.getUsername(), spec.getFullClassName(), i);
        }

        return robotSpecifications;
    }
}
