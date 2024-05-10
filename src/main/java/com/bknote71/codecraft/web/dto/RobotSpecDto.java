package com.bknote71.codecraft.web.dto;

import com.bknote71.codecraft.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RobotSpecDto {

    private Integer robotId;
    private String name; // robot 의 이름
    private String username; // 유저의 이름
    private String fullClassName; // 필요 없는 이름
    private String code; // java code
    private String lang;
}
