package com.bknote71.codecraft.entity;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class RobotSpecEntity {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String username;
    private String fullClassName;

    @Column(columnDefinition = "LONGTEXT")
    private String code; // java code

    private String lang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 외래 키 관계의 주인(UserEntity)이 변경될 때 같이 변경되도록 연관 관계 설정
    public void setUser(UserEntity user) {
        this.user = user;
        if (!user.getSpecifications().contains(this)) { // 무한 루프 방지
            user.getSpecifications().add(this);
        }
    }
}
