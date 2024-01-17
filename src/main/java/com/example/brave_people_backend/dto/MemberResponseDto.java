package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberResponseDto {
    private String username;

    // MemberResponseDto 객체를 생성하는 함수 (of 함수를 이용함으로서 유지보수 편리)
    public static MemberResponseDto of(Member member){
        return MemberResponseDto.builder()
                .username(member.getUsername())
                .build();
    }
}
