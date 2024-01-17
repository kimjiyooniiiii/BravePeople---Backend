package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.enumclass.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

// 회원가입 요청 Dto
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    private String username;
    private String pw;
    private String email;
    private String nickname;

    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .username(username)
                .pw(passwordEncoder.encode(pw))
                .email(email)
                .nickname(nickname)
                .authority(Authority.ROLE_USER)
                .build();
    }

}
