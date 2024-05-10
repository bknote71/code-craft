package com.bknote71.codecraft.entity.service;

import com.bknote71.codecraft.entity.UserEntity;
import com.bknote71.codecraft.entity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(String username, String password) {
        UserEntity user = userRepository.findByUsername(username);
        if (user != null) {
            System.out.println("이미 존재하는 아이디 입니다.");
            return null;
        }

        UserEntity userEntity = new UserEntity(username, passwordEncoder.encode(password));
        UserEntity savedUser = userRepository.save(userEntity);

        System.out.println("create user success " + savedUser.getUsername());

        return savedUser.getId();
    }

    public boolean login(String username, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new User(username, password, List.of(new SimpleGrantedAuthority("ROLE_USER"))),
                password,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        if (token == null) {
            return false;
        }

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return true;
    }
}
