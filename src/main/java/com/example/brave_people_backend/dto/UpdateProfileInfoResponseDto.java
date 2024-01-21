package com.example.brave_people_backend.dto;

import com.example.brave_people_backend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileInfoResponseDto {

     String nickname;
     String introduction;

    // UpdateProfileInfoResponseDto 객체를 생성하는 함수 (of 함수를 이용함으로서 유지보수 편리)
    public static UpdateProfileInfoResponseDto of(Member member) {
         return UpdateProfileInfoResponseDto.builder()
                 .nickname(member.getNickname())
                 .introduction(member.getIntroduction())
                 .build();
     }
}
