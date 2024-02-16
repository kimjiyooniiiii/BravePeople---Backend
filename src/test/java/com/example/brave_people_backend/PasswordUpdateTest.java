package com.example.brave_people_backend;

import com.example.brave_people_backend.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class PasswordUpdateTest {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void updatePw() {
        for(long i=1; i<=38; i++) {
            memberRepository.findById(i).ifPresent(
                    (c) -> c.changePw(passwordEncoder.encode("rktlrhrl123"))
            );
        }
    }
}
