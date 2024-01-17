package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원가입 요청에 대한 응답
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SignupResponseDto {
    private String username;
    private String email;
    private String nickname;

    // SignupResponseDto 객체를 생성하는 함수 (of 함수를 이용함으로서 유지보수 편리)
    public static SignupResponseDto of(Member member){
        return SignupResponseDto.builder()
                .username(member.getUsername())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
