package com.example.brave_people_backend.auth.dto;

import com.example.brave_people_backend.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class LoginResponseDto {

    private String lat;

    private String lng;

    private String nickname;

    private String profileImg;

    private String memberId;

    private TokenDto tokenDto;

    private List<Long> chatRooms;

    private Boolean isRead;

    public static LoginResponseDto of(Member member, List<Long> chatRooms, TokenDto tokenDto, boolean isRead) {
        return LoginResponseDto.builder()
                .memberId(member.getMemberId().toString())
                .nickname(member.getNickname())
                .lat(String.valueOf(member.getLat()))
                .lng(String.valueOf(member.getLng()))
                .profileImg(member.getProfileImg())
                .tokenDto(tokenDto)
                .chatRooms(chatRooms)
                .isRead(isRead)
                .build();
    }
}
