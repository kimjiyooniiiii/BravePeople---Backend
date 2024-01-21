package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.enumclass.Authority;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank @Size(min = 2, max = 5)
    private String name;

    @NotBlank @Size(min = 2, max = 2)
    private String gender;

    @NotBlank @Size(min = 6, max = 20)
    private String username;

    @NotBlank  @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$") // 영문 + 숫자, 8자리 아상
    private String pw;

    @NotBlank @Size(max = 30) @Email
    private String email;

    @NotBlank @Size(max = 6)
    private String nickname;

    @NotBlank
    private String lat;

    @NotBlank
    private String lng;

    @NotNull
    private Long emailId;

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
                .name(name)
                .build();
    }

}