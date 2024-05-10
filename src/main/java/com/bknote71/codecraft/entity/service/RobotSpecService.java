package com.bknote71.codecraft.entity.service;

import com.bknote71.codecraft.entity.RobotSpecEntity;
import com.bknote71.codecraft.entity.UserEntity;
import com.bknote71.codecraft.entity.repository.RobotSpecRepository;
import com.bknote71.codecraft.entity.repository.UserRepository;
import com.bknote71.codecraft.robocode.core.RobotSpecification;
import com.bknote71.codecraft.robocode.loader.AwsS3ClassLoader;
import com.bknote71.codecraft.session.packet.PacketHandler;
import com.bknote71.codecraft.web.dto.CompileRequest;
import com.bknote71.codecraft.web.dto.CompileResult;
import com.bknote71.codecraft.web.dto.RobotSpecDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RobotSpecService {

    private final UserRepository userRepository;
    private final RobotSpecRepository robotSpecRepository;
    private final PacketHandler packetHandler;

    public List<RobotSpecDto> getRobotInfo(String username) {
        AwsS3ClassLoader classLoader = new AwsS3ClassLoader("robot-class");
        UserEntity user = userRepository.findByUsername(username);
        List<RobotSpecEntity> specs = null;
        if (user == null || (specs = user.getSpecifications()) == null || specs.isEmpty()) {
            System.out.println("잘못된 접근");
            return new ArrayList<>();
        }

        List<RobotSpecDto> robotSpecDtos = new ArrayList<>();
        for (RobotSpecEntity spec : specs) {
            // 여기서 뭔가 dto 로 변환 ??
            String lang = spec.getLang();
            String specLang = lang == null || lang.isBlank() || lang.isEmpty() ? "java" : spec.getLang();
            robotSpecDtos.add(new RobotSpecDto(null, spec.getName(), username, spec.getFullClassName(), spec.getCode(), specLang));
        }

        return robotSpecDtos;
    }


    @Transactional
    public RobotSpecDto saveRobotSpec(String username, int specIndex, String robotName, String fullClassName, String code, String lang) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            System.out.println("save robot spec 하는데 유저가 존재하지 않음");
            return null;
        }

        RobotSpecEntity robotSpecEntity = new RobotSpecEntity();
        robotSpecEntity.setName(robotName);
        robotSpecEntity.setUsername(username);
        robotSpecEntity.setFullClassName(fullClassName);
        robotSpecEntity.setCode(code);
        robotSpecEntity.setLang(lang);
        robotSpecEntity.setUser(user);

        user.changeSpec(specIndex, robotSpecEntity);

        RobotSpecEntity savedEntity = robotSpecRepository.save(robotSpecEntity);
        if (savedEntity == null) {
            System.out.println("저장 실패");
            return null;
        }

        return new RobotSpecDto(null, savedEntity.getName(), savedEntity.getUsername(), savedEntity.getFullClassName(), robotSpecEntity.getCode(), lang);
    }

    @Transactional
    public RobotSpecDto changeRobotSpec(String username, CompileRequest request, String robotName, String fullClassName) {
        log.info("submit new code");
        int robotId = request.getRobotId();
        int specIndex = request.getSpecIndex();
        String code = request.getCode();
        String lang = request.getLang();

        // spec 변경 및 저장
        Long updateResult = updateRobotSpec(username, specIndex, robotName, fullClassName, code, lang);

        if (updateResult == null) {
            log.error("업데이트 로봇 스펙 실패");
            return null;
        }

        RobotSpecification spec = packetHandler.changeAndReenter(request.getRobotId(), username, specIndex);
        if (spec == null) {
            log.error("change and reenter 실패");
            return null;
        }

        return new RobotSpecDto(robotId, spec.getName(), spec.getUsername(), spec.getFullClassName(), code, lang);
    }

    @Transactional
    private Long updateRobotSpec(String username, int specIndex, String robotName, String fullClassName, String code, String lang) {
        UserEntity user = userRepository.findByUsername(username);

        List<RobotSpecEntity> specifications = user.getSpecifications();
        if (specifications.size() <= specIndex) {
            log.error("스펙 인덱스가 잘못됨");
            return null;
        }

        RobotSpecEntity robotSpecEntity = specifications.get(specIndex);
        robotSpecEntity.setName(robotName);
        robotSpecEntity.setFullClassName(fullClassName);
        robotSpecEntity.setCode(code);
        robotSpecEntity.setLang(lang);
        return robotSpecEntity.getId();
    }

    private static final String DEFAULTROBOT =
            "public class DefaultRobot%d extends Robot {\n" +
            "    public void run() {\n" +
            "    }\n" +
            "}\n";

    @Transactional
    public void createDefaultRobot(String username) {
        saveRobotSpec(username, 0, "DefaultRobot0", "DefaultRobot0.class", String.format(DEFAULTROBOT, 0), "java");
        saveRobotSpec(username, 1, "DefaultRobot1", "DefaultRobot1.class", String.format(DEFAULTROBOT, 1), "java");
        saveRobotSpec(username, 2, "DefaultRobot2", "DefaultRobot2.class", String.format(DEFAULTROBOT, 2), "java");
    }

    public Boolean compareWithRequestCode(String username, CompileRequest compileRequest) {
        // lang 과 code 가 같아야 함
        UserEntity user = userRepository.findByUsername(username);

        List<RobotSpecEntity> specifications = user.getSpecifications();
        if (specifications.size() <= compileRequest.getSpecIndex()) {
            log.error("스펙 인덱스가 잘못됨");
            return null;
        }

        RobotSpecEntity robotSpecEntity = specifications.get(compileRequest.getSpecIndex());
        if (robotSpecEntity.getLang() == compileRequest.getLang()
                && robotSpecEntity.getCode() == compileRequest.getCode()) {
            return true;
        }

        return false;
    }
}
