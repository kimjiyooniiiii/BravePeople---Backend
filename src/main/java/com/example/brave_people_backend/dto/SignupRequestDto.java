package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.enumclass.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    private String name;
    private String gender;
    private String username;
    private String pw;
    private String email;
    private String nickname;
    private String lat;
    private String lng;

    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .username(username)
                .pw(passwordEncoder.encode(pw))
                .email(email)
                .nickname(nickname)
                .gender(gender.equals("여성"))
                .lat(new BigDecimal(lat))
                .lng(new BigDecimal(lng))
                .authority(Authority.ROLE_USER)
                .build();
    }

}