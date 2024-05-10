package com.bknote71.codecraft.entity.repository;

import com.bknote71.codecraft.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
//
//    @Test
//    void userdelete() {
//        UserEntity sa = userRepository.findByUsername("sa");
//        userRepository.delete(sa);
//    }

//    @Test
//    void deleteAll() {
//        userRepository.deleteAll();
//    }


}